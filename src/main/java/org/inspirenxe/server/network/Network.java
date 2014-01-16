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
import java.net.SocketAddress;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import com.flowpowered.commons.ticking.TickingElement;
import org.inspirenxe.server.Game;
import org.inspirenxe.server.network.message.ChannelMessage;
import org.inspirenxe.server.network.protocol.ServerProtocol;

public class Network extends TickingElement {
    private static final int TPS = 20;
    private final Game game;
    private final GameNetworkServer server;
    private final AtomicReference<SocketAddress> bound = new AtomicReference<>();
    private final Map<ChannelMessage.Channel, ConcurrentLinkedQueue<ChannelMessage>> messageQueue = new EnumMap<>(ChannelMessage.Channel.class);

    public Network(Game game) {
        super("network", TPS);
        this.game = game;
        this.server = new GameNetworkServer(game);
        messageQueue.put(ChannelMessage.Channel.NETWORK, new ConcurrentLinkedQueue<ChannelMessage>());
    }

    @Override
    public void onStart() {
        if (bound.get() == null) {
            throw new RuntimeException("Attempt to start up networking without a bound address set!");
        }
        game.getLogger().info("Starting network");
        server.bind(bound.get());
        game.getLogger().info("Listening on " + bound.get());
    }

    @Override
    public void onTick(long l) {
    }

    @Override
    public void onStop() {
        game.exit();
    }

    /**
     * Gets the {@link java.util.Iterator} storing the messages for the {@link org.inspirenxe.server.network.message.ChannelMessage.Channel}
     *
     * @param c See {@link org.inspirenxe.server.network.message.ChannelMessage}
     * @return The iterator
     */
    public Iterator<ChannelMessage> getChannel(ChannelMessage.Channel c) {
        return messageQueue.get(c).iterator();
    }

    /**
     * Offers a {@link org.inspirenxe.server.network.message.ChannelMessage} to a queue mapped to {@link org.inspirenxe.server.network.message.ChannelMessage.Channel}
     *
     * @param c See {@link org.inspirenxe.server.network.message.ChannelMessage.Channel}
     * @param m See {@link org.inspirenxe.server.network.message.ChannelMessage}
     */
    public void offer(ChannelMessage.Channel c, ChannelMessage m) {
        messageQueue.get(c).offer(m);
    }

    public void bind(SocketAddress address) {
        if (isRunning()) {
            throw new RuntimeException("Any attempt to rebind address and port the server is listening on needs to have the Network thread stopped first and then restarted!");
        }
        bound.set(address);
    }
}
