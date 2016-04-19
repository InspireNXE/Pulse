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

import com.flowpowered.network.Message;
import com.flowpowered.network.exception.ChannelClosedException;
import com.flowpowered.network.processor.MessageProcessor;
import com.flowpowered.network.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.inspirenxe.pulse.SpongeGame;
import org.slf4j.Logger;
import org.spongepowered.api.profile.GameProfile;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class MinecraftSession implements Session {
    /**
     * A queue of incoming and unprocessed messages
     */
    private final Queue<Message> incomingQueue = new ArrayDeque<>();
    /**
     * A queue of outgoing messages that will be sent after the client finishes identification
     */
    private final Queue<Message> outgoingQueue = new ConcurrentLinkedQueue<>();
    /**
     * The current state.
     */
    private State state = State.EXCHANGE_HANDSHAKE;

    private final Channel channel;
    private MinecraftProtocol protocol = ProtocolType.HANDSHAKE;
    private GameProfile profile;

    public MinecraftSession(Channel channel) {
        this.channel = channel;
    }

    /**
     * Gets the state of this session.
     *
     * @return The session's state.
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the state of this session.
     *
     * @param state The new state.
     */
    public void setState(State state) {
        this.state = state;
    }

    public GameProfile getGameProfile() {
        return this.profile;
    }

    public void setGameProfile(GameProfile profile) {
        this.profile = profile;
    }

    public void pulse() {
        Message message;

        while ((message = incomingQueue.poll()) != null) {
            getProtocol().getHandlerManager().handle(this, message);
        }

        if (state == State.OPEN) {
            while ((message = outgoingQueue.poll()) != null) {
                send(message);
            }
        }
    }

    @Override
    public <T extends Message> void messageReceived(T message) {
        getProtocol().getHandlerManager().handle(this, message);
        incomingQueue.add(message);
    }

    @Override
    public MinecraftProtocol getProtocol() {
        return this.protocol;
    }

    public void switchToProtocol(ProtocolType protocol) {
        this.protocol = protocol;
    }

    @Override
    public MessageProcessor getProcessor() {
        return null;
    }

    @Override
    public void send(Message message) throws ChannelClosedException {
        send(SendType.QUEUE, message);
    }

    public void send(SendType type, Message message) throws ChannelClosedException {
        if (message == null) {
            return;
        }
        if (type == SendType.FORCE || this.state == State.OPEN) {
            sendWithFuture(message);
        } else if (type == SendType.QUEUE) {
            outgoingQueue.add(message);
        }
    }

    public ChannelFuture sendWithFuture(Message message) throws ChannelClosedException {
        if (!channel.isActive()) {
            throw new ChannelClosedException("Trying to send a message when a session is inactive!");
        }
        return channel.writeAndFlush(message).addListener(future -> {
            if (future.cause() != null) {
                SpongeGame.logger.error("Failed to flush packet [{}] in protocol [{}] due to [{}]!", message, getProtocol(), future.cause());
            }
        });
    }

    @Override
    public void sendAll(Message... messages) {
        sendAll(SendType.QUEUE, messages);
    }

    public void sendAll(SendType type, Message... messages) {
        for (Message msg : messages) {
            send(type, msg);
        }
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onReady() {

    }

    @Override
    public void onInboundThrowable(Throwable throwable) {
        SpongeGame.logger.error(throwable.toString());
    }

    @Override
    public Logger getLogger() {
        return SpongeGame.logger;
    }

    /**
     * Specifies send behavior
     */
    public enum SendType {
        /**
         * Messages sent with a SendType of OPEN_ONLY will only send if State is OPEN. Messages will not be
         * queued.
         */
        OPEN_ONLY,
        /**
         * Messages sent with a SendType of QUEUE will wait until State is OPEN to send. Messages may be queued.
         */
        QUEUE,
        /**
         * Messages sent with a SendType of FORCE will send as soon as possible regardless of State.
         */
        FORCE
    }

    public enum State {
        /**
         * In the exchange handshake state, the server is waiting for the client to send its initial handshake
         * packet.
         */
        EXCHANGE_HANDSHAKE,
        /**
         * In the exchange identification state, the server is waiting for the client to send its identification
         * packet.
         */
        EXCHANGE_IDENTIFICATION,
        /**
         * In the exchange encryption state, the server is waiting for the client to send its encryption
         * response packet.
         */
        EXCHANGE_ENCRYPTION,
        /**
         * This state is when a critical message has been sent that must be waited for.
         */
        WAITING,
        /**
         * Allows messages to be sent.
         */
        OPEN
    }
}
