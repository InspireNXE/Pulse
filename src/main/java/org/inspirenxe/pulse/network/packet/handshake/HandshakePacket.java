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
package org.inspirenxe.pulse.network.packet.handshake;

import com.flowpowered.network.Codec;
import com.flowpowered.network.Message;
import com.flowpowered.network.util.ByteBufUtils;
import com.google.common.base.Objects;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Server-bound packet that initiates the connection process to the pulse
 */
public class HandshakePacket implements Message, Codec<HandshakePacket> {
    private int version;
    private String address;
    private short port;
    private HandshakeState state;

    public HandshakePacket() {}

    /**
     * Constructs a new handshake
     *
     * @param address The address provided by the client
     * @param port The port provided by the client
     * @param state The state of the handshake, see {@link HandshakePacket.HandshakeState}
     */
    public HandshakePacket(int version, String address, short port, HandshakeState state) {
        this.version = version;
        this.address = address;
        this.port = port;
        this.state = state;
    }

    /**
     * Returns the protocol version.
     *
     * @return The protocol version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Returns the client address.
     *
     * @return The client address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the client port.
     *
     * @return The client port
     */
    public short getPort() {
        return port;
    }

    /**
     * Returns the handshake state.
     *
     * @return The handshake state
     */
    public HandshakeState getState() {
        return state;
    }

    @Override
    public HandshakePacket decode(ByteBuf buf) throws IOException {
        final int version = ByteBufUtils.readVarInt(buf);
        final String address = ByteBufUtils.readUTF8(buf);
        final short port = (short) buf.readUnsignedShort();
        final HandshakeState state = HandshakeState.get(ByteBufUtils.readVarInt(buf));
        return new HandshakePacket(version, address, port, state);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, HandshakePacket message) throws IOException {
        throw new IOException("The Minecraft client should not receive a handshake from the server!");
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("version", version)
                .add("address", address)
                .add("port", port)
                .add("state", state)
                .toString();
    }

    /**
     * An enum of the handshake states.
     */
    public enum HandshakeState {
        /**
         * Connecting client is asking for the server's status
         */
        STATUS(1),
        /**
         * Connecting client is attempting a login
         */
        LOGIN(2);
        private final int state;
        private static final Map<Integer, HandshakeState> map = new HashMap<>();

        static {
            for (HandshakeState state : HandshakeState.values()) {
                map.put(state.value(), state);
            }
        }

        HandshakeState(int state) {
            this.state = state;
        }

        /**
         * Returns the numerical value associated to the handshake state.
         *
         * @return The numerical value
         */
        public int value() {
            return state;
        }

        public static HandshakeState get(int value) {
            return map.get(value);
        }
    }
}

