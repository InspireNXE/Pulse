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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.inspirenxe.pulse.SpongeGame;
import org.inspirenxe.pulse.util.TickingElement;
import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.MinecraftConstants;
import org.spacehq.mc.protocol.MinecraftProtocol;
import org.spacehq.mc.protocol.ServerLoginHandler;
import org.spacehq.mc.protocol.data.game.PlayerListEntry;
import org.spacehq.mc.protocol.data.game.PlayerListEntryAction;
import org.spacehq.mc.protocol.data.game.chunk.Chunk;
import org.spacehq.mc.protocol.data.game.chunk.Column;
import org.spacehq.mc.protocol.data.game.entity.metadata.Position;
import org.spacehq.mc.protocol.data.game.entity.player.GameMode;
import org.spacehq.mc.protocol.data.game.setting.Difficulty;
import org.spacehq.mc.protocol.data.game.world.WorldType;
import org.spacehq.mc.protocol.data.game.world.block.BlockState;
import org.spacehq.mc.protocol.data.message.TextMessage;
import org.spacehq.mc.protocol.data.status.PlayerInfo;
import org.spacehq.mc.protocol.data.status.ServerStatusInfo;
import org.spacehq.mc.protocol.data.status.VersionInfo;
import org.spacehq.mc.protocol.data.status.handler.ServerInfoBuilder;
import org.spacehq.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerAbilitiesPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerSpawnPositionPacket;
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
import org.spacehq.packetlib.tcp.TcpServerSession;
import org.spacehq.packetlib.tcp.TcpSessionFactory;
import org.spacehq.packetlib.tcp.io.ByteBufNetOutput;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Network extends TickingElement implements ServerListener, SessionListener {
    private static final int TPS = 20;
    private static final boolean VERIFY_USERS = false;
    private static final Proxy AUTH_PROXY = Proxy.NO_PROXY;
    private Set<Session> sessions = new HashSet<>();
    private Queue<PacketReceivedEvent> incomingNetworkEvents = new ConcurrentLinkedQueue<>();

    public Network() {
        super("network", TPS);
    }

    @Override
    public void onStart() {
        SpongeGame.logger.info("Starting network");
        final Server server = new Server("0.0.0.0", 25565, MinecraftProtocol.class, new TcpSessionFactory());
        server.setGlobalFlag(MinecraftConstants.AUTH_PROXY_KEY, AUTH_PROXY);
        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, VERIFY_USERS);
        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY,
                (ServerInfoBuilder) session -> new ServerStatusInfo(
                        new VersionInfo(MinecraftConstants.GAME_VERSION, MinecraftConstants.PROTOCOL_VERSION),
                        new PlayerInfo(100, 0, new GameProfile[0]), new TextMessage("Hello world!"), null));

        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 1000);

        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, (ServerLoginHandler) session -> {
            final GameProfile profile = session.getFlag(MinecraftConstants.PROFILE_KEY);
            session.send(new ServerJoinGamePacket(0, true, GameMode.SURVIVAL, 0, Difficulty.PEACEFUL, 10, WorldType.DEFAULT, false));
            final ByteBuf buffer = Unpooled.buffer();
            final ByteBufNetOutput adapter = new ByteBufNetOutput(buffer);
            try {
                adapter.writeString(SpongeGame.ECOSYSTEM_NAME);
            } catch (IOException e) {
                SpongeGame.logger.error(e.toString());
            }
            session.send(new ServerPluginMessagePacket("MC|Brand", buffer.array()));
            session.send(new ServerSpawnPositionPacket(new Position(0, 64, 0)));
            session.send(new ServerPlayerAbilitiesPacket(false, false, false, true, 0.05f, 0.1f));
            session.send(new ServerPlayerPositionRotationPacket(0, 64, 0, 0, 0, 0));
            for (Session activeSession : sessions) {
                activeSession
                        .send(new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, new PlayerListEntry[]{new PlayerListEntry(profile,
                                GameMode.CREATIVE, 10, new TextMessage(profile.getName()))}));

                if (activeSession != session) {
                    final GameProfile otherProfile = activeSession.getFlag(MinecraftConstants.PROFILE_KEY);
                    session.send(
                            new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, new PlayerListEntry[]{new PlayerListEntry(otherProfile,
                                    GameMode.CREATIVE, 10, new TextMessage(otherProfile.getName()))}));
                }

                activeSession.send(new ServerChatPacket(profile.getName() + " has joined the game."));
            }


            final BlockState state = new BlockState(1, 0);
            final List<Column> columns = new ArrayList<>();
            for (int cx = 0; cx < 16; cx++) {
                for (int cz = 0; cz < cx; cz++) {
                    final Chunk[] sections = new Chunk[16];
                    for (int section = 0; section < 16; section++) {
                        final Chunk chunk = new Chunk(true);

                        for (int bx = 0; bx < 16; bx++) {
                            for (int bz = 0; bz < bx; bz++) {
                                for (int by = 0; by < bz; by++) {
                                    if (new Random().nextBoolean()) {
                                        chunk.getBlocks().set(bx, by, bz, state);
                                    }
                                }
                            }
                        }

                        sections[section] = chunk;
                    }

                    final byte[] biomeData = new byte[256];
                    Arrays.fill(biomeData, (byte) 2);
                    columns.add(new Column(cx, cz, sections, biomeData));
                }
            }

            for (Column column : columns) {
                session.send(new ServerChunkDataPacket(column));
            }

        });

        server.addListener(this);
        server.bind();

    }

    @Override
    public void onTick(long l) {
        PacketReceivedEvent incomingNetworkEvent;
        while ((incomingNetworkEvent = incomingNetworkEvents.poll()) != null) {
            if (incomingNetworkEvent.getPacket() instanceof ClientKeepAlivePacket) {
                continue;
            }

            SpongeGame.logger.error("[INCOMING] " + incomingNetworkEvent.getPacket().toString());
        }
    }

    @Override
    public void onStop() {

    }

    @Override
    public void packetReceived(PacketReceivedEvent event) {
        incomingNetworkEvents.add(event);
    }

    @Override
    public void packetSent(PacketSentEvent event) {
        if (!(event.getPacket() instanceof ServerKeepAlivePacket)) {
            SpongeGame.logger.error("[OUTGOING] " + event.getPacket().toString());
        }
    }

    @Override
    public void connected(ConnectedEvent event) {
    }

    @Override
    public void disconnecting(DisconnectingEvent disconnectingEvent) {

    }

    @Override
    public void disconnected(DisconnectedEvent disconnectedEvent) {

    }

    @Override
    public void serverBound(ServerBoundEvent serverBoundEvent) {

    }

    @Override
    public void serverClosing(ServerClosingEvent serverClosingEvent) {

    }

    @Override
    public void serverClosed(ServerClosedEvent serverClosedEvent) {

    }

    @Override
    public void sessionAdded(SessionAddedEvent event) {
        SpongeGame.logger.info("Connection received from [{}:{}]", event.getSession().getHost(), event.getSession().getPort());
        if (event.getSession() instanceof TcpServerSession) {
            sessions.add(event.getSession());
            event.getSession().addListener(this);
        }

    }

    @Override
    public void sessionRemoved(SessionRemovedEvent event) {
        event.getSession().removeListener(this);
        sessions.remove(event.getSession());
    }
}

