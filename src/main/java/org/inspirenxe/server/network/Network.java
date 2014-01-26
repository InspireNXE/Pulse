/**
 * This file is part of Pulse, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2014 InspireNXE <http://inspirenxe.org/>
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
package org.inspirenxe.server.network;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.flowpowered.commons.ticking.TickingElement;
import com.flowpowered.networking.util.AnnotatedMessageHandler;
import com.flowpowered.networking.util.AnnotatedMessageHandler.Handle;
import io.netty.channel.ChannelFutureListener;
import org.inspirenxe.server.Game;
import org.inspirenxe.server.game.Difficulty;
import org.inspirenxe.server.game.Dimension;
import org.inspirenxe.server.game.GameMode;
import org.inspirenxe.server.game.LevelType;
import org.inspirenxe.server.network.ChannelMessage.Channel;
import org.inspirenxe.server.network.message.DisconnectMessage;
import org.inspirenxe.server.network.message.handshake.HandshakeMessage;
import org.inspirenxe.server.network.message.login.LoginStartMessage;
import org.inspirenxe.server.network.message.login.LoginSuccessMessage;
import org.inspirenxe.server.network.message.play.JoinGameMessage;
import org.inspirenxe.server.network.protocol.LoginProtocol;
import org.inspirenxe.server.network.protocol.PlayProtocol;

public class Network extends TickingElement {
    private static final int TPS = 20;
    private final Game game;
    private final Access access;
    private final GameNetworkServer server;
    private final AnnotatedMessageHandler handler = new AnnotatedMessageHandler(this);
    private final Map<Channel, ConcurrentLinkedQueue<ChannelMessage>> messageQueue = new EnumMap<>(Channel.class);

    public Network(Game game) {
        super("network", TPS);
        this.game = game;
        this.access = new Access(this);
        server = new GameNetworkServer(game);
        messageQueue.put(Channel.UNIVERSE, new ConcurrentLinkedQueue<ChannelMessage>());
    }

    @Override
    public void onStart() {
        game.getLogger().info("Starting network");
        access.load();
        final InetSocketAddress address = new InetSocketAddress(game.getConfiguration().getAddress(), game.getConfiguration().getPort());
        server.bind(address);
        game.getLogger().info("Listening on " + address);
    }

    @Override
    public void onTick(long l) {
    }

    @Override
    public void onStop() {
        game.getLogger().info("Stopping network");
        server.shutdown();
    }

    public Game getGame() {
        return game;
    }

    public Access getAccess() {
        return access;
    }

    /**
     * Gets the {@link java.util.Iterator} storing the messages for the {@link ChannelMessage.Channel}
     *
     * @param c See {@link ChannelMessage}
     * @return The iterator
     */
    public Iterator<ChannelMessage> getChannel(Channel c) {
        return messageQueue.get(c).iterator();
    }

    /**
     * Offers a {@link ChannelMessage} to a queue mapped to {@link ChannelMessage.Channel}
     *
     * @param c See {@link ChannelMessage.Channel}
     * @param m See {@link ChannelMessage}
     */
    public void offer(Channel c, ChannelMessage m) {
        if (c == Channel.NETWORK) {
            handler.handle(m);
        } else {
            messageQueue.get(c).offer(m);
        }
    }

    @Handle
    private void handleHandshake(HandshakeMessage message) {
        switch (message.getState()) {
            case STATUS:
                //TODO Implement status protocol
                break;
            case LOGIN:
                message.getSession().setProtocol(new LoginProtocol(game));
        }
    }

    @Handle
    private void handleLoginStart(LoginStartMessage message) {
        message.getSession().setUUID(new UUID(0, message.getUsername().hashCode()).toString());
        message.getSession().send(new LoginSuccessMessage(message.getSession().getUUID(), message.getUsername()));
        message.getSession().setProtocol(new PlayProtocol(game));
    }
}

