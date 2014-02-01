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
package org.inspirenxe.server.network.codec.handshake;

import java.io.IOException;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import org.inspirenxe.server.network.message.handshake.HandshakeMessage;
import org.inspirenxe.server.network.message.handshake.HandshakeMessage.HandshakeState;

public class HandshakeCodec implements Codec<HandshakeMessage> {
    @Override
    public HandshakeMessage decode(ByteBuf buf) throws IOException {
        final int version = ByteBufUtils.readVarInt(buf);
        final String address = ByteBufUtils.readUTF8(buf);
        final short port = (short) buf.readUnsignedShort();
        final HandshakeState state = HandshakeState.get(ByteBufUtils.readVarInt(buf));
        return new HandshakeMessage(version, address, port, state);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, HandshakeMessage message) throws IOException {
        throw new IOException("The Minecraft client should not receive a handshake from the server!");
    }
}

