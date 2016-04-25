/**
 * This file is part of Pulse, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2014-2015 InspireNXE <http://inspirenxe.org/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.inspirenxe.pulse.network;

import org.inspirenxe.pulse.SpongeGame;
import org.inspirenxe.pulse.SpongeServer;
import org.inspirenxe.pulse.network.pc.PCSession;
import org.inspirenxe.pulse.network.pc.PCSessionFactory;
import org.inspirenxe.pulse.network.pc.protocol.PCProtocol;
import org.inspirenxe.pulse.network.pc.protocol.ProtocolPhase;
import org.inspirenxe.pulse.network.pc.thread.KeepAliveThread;
import org.inspirenxe.pulse.util.TickingElement;
import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.MinecraftConstants;
import org.spacehq.mc.protocol.data.game.MessageType;
import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import org.spacehq.packetlib.Server;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.event.server.ServerBoundEvent;
import org.spacehq.packetlib.event.server.ServerClosedEvent;
import org.spacehq.packetlib.event.server.ServerClosingEvent;
import org.spacehq.packetlib.event.server.ServerListener;
import org.spacehq.packetlib.event.server.SessionAddedEvent;
import org.spacehq.packetlib.event.server.SessionRemovedEvent;
import org.spacehq.packetlib.event.session.ConnectedEvent;
import org.spacehq.packetlib.event.session.DisconnectedEvent;
import org.spacehq.packetlib.event.session.DisconnectingEvent;
import org.spacehq.packetlib.event.session.PacketReceivedEvent;
import org.spacehq.packetlib.event.session.PacketSentEvent;
import org.spacehq.packetlib.event.session.SessionListener;

import java.net.Proxy;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Network extends TickingElement implements ServerListener, SessionListener {
    private static final Proxy AUTH_PROXY = Proxy.NO_PROXY;

    private final SpongeServer server;
    private final Server listener;
    private final Queue<PacketReceivedEvent> incomingNetworkEvents = new ConcurrentLinkedQueue<>();

    public Network(SpongeServer server) {
        super("network", server.getGame().getConfiguration().getTickRate());
        this.server = server;

        this.listener = new Server(server.getGame().getConfiguration().getAddress(), server.getGame().getConfiguration().getPort(), PCProtocol.class,
                new PCSessionFactory());
        this.listener.setGlobalFlag(MinecraftConstants.AUTH_PROXY_KEY, AUTH_PROXY);
        this.listener.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, server.getGame().getConfiguration().isAuthenticateSessions());
        this.listener.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 100);
    }

    @Override
    public void onStart() {
        SpongeGame.logger.info("Starting network");

        if (!this.server.getGame().getConfiguration().isAuthenticateSessions()) {
            SpongeGame.logger.warn("**** SERVER IS NOT AUTHENTICATING SESSIONS ****");
        }

        this.listener.addListener(this);
        this.listener.bind(true);
    }

    @Override
    public void onTick(long l) {
        PacketReceivedEvent incomingNetworkEvent;
        while ((incomingNetworkEvent = incomingNetworkEvents.poll()) != null) {
            final PCSession pcSession = (PCSession) incomingNetworkEvent.getSession();
            pcSession.getPacketProtocol().getProtocolPhase().getHandlerInvoker().handle(pcSession, incomingNetworkEvent.getPacket());
        }
    }

    @Override
    public void onStop() {

    }

    @Override
    public void packetReceived(PacketReceivedEvent event) {
        if (event.getPacket().isPriority()) {
            final PCSession pcSession = (PCSession) event.getSession();
            pcSession.getPacketProtocol().getProtocolPhase().getHandlerInvoker().handle(pcSession, event.getPacket());
        } else {
            incomingNetworkEvents.add(event);
        }
    }

    @Override
    public void packetSent(PacketSentEvent event) {
    }

    @Override
    public void connected(ConnectedEvent event) {
    }

    @Override
    public void disconnecting(DisconnectingEvent event) {
        event.getSession().send(new ServerDisconnectPacket(event.getReason()));
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        if (((PCSession) event.getSession()).getPacketProtocol().getProtocolPhase() == ProtocolPhase.INGAME) {
            final GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
            final String message = profile.getName() + " has left the game.";

            SpongeGame.logger.info(message);

            this.listener.getSessions().stream().filter(Session::isConnected).forEach(session -> {
                session.send(new ServerChatPacket(message, MessageType.SYSTEM));
            });
        }
    }

    @Override
    public void serverBound(ServerBoundEvent serverBoundEvent) {
        SpongeGame.logger.info("Listening for connections on [{}:{}].", serverBoundEvent.getServer().getHost(), serverBoundEvent.getServer()
                .getPort());

        new KeepAliveThread().start();
    }

    @Override
    public void serverClosing(ServerClosingEvent serverClosingEvent) {

    }

    @Override
    public void serverClosed(ServerClosedEvent serverClosedEvent) {

    }

    @Override
    public void sessionAdded(SessionAddedEvent event) {
        if (event.getSession() instanceof PCSession) {
            SpongeGame.logger.info("Connection received from [{}:{}]", event.getSession().getHost(), event.getSession().getPort());
            event.getSession().addListener(this);
        }

    }

    @Override
    public void sessionRemoved(SessionRemovedEvent event) {
        event.getSession().removeListener(this);
    }

    public List<Session> getSessions() {
        return this.listener.getSessions();
    }
}

