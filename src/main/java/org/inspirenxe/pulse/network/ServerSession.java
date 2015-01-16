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

import com.flowpowered.networking.Message;
import com.flowpowered.networking.protocol.AbstractProtocol;
import com.flowpowered.networking.session.BasicSession;
import io.netty.channel.Channel;
import org.inspirenxe.pulse.Game;
import org.inspirenxe.pulse.network.message.ChannelMessage;
import org.inspirenxe.pulse.network.protocol.ServerProtocol;

/**
 * Represents an open connection to a client. All {@link com.flowpowered.networking.Message}s are sent through the session.
 */
public class ServerSession extends BasicSession {
    private final Game game;
    private String uuid;
    private String username;

    /**
     * Constructs a new pulse session from the game, channel and protocol.
     *
     * @param game The game
     * @param channel The network channel
     * @param protocol The abstract protocol
     */
    public ServerSession(Game game, Channel channel, ServerProtocol protocol) {
        super(channel, protocol);
        this.game = game;
    }

    @Override
    public void messageReceived(Message message) {
        final ChannelMessage channelMessage = (ChannelMessage) message;
        channelMessage.setSession(this);
        for (ChannelMessage.Channel channel : channelMessage.getChannels()) {
            game.getNetwork().offer(channel, channelMessage);
        }
    }

    @Override
    public void setProtocol(AbstractProtocol protocol) {
        if (!(protocol instanceof ServerProtocol)) {
            throw new IllegalArgumentException(protocol + " must be an extension of ServerProtocol!");
        }
        super.setProtocol(protocol);
    }

    @Override
    public void onInboundThrowable(Throwable throwable) {
        game.getLogger().fatal("Exception caught on inbound message", throwable);
    }

    @Override
    public void onOutboundThrowable(Throwable throwable) {
        game.getLogger().fatal("Exception caught on outbound message", throwable);
    }

    /**
     * Returns the ID of the session.
     *
     * @return The session's ID
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Sets the session's ID
     *
     * @param uuid The session ID
     */
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the username of the session.
     *
     * @return The session's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the session's username.
     *
     * @param username The session's username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the game for the session.
     *
     * @return The game
     */
    public Game getGame() {
        return game;
    }
}

