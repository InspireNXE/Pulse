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
package org.inspirenxe.server.network.codec.play;

import java.io.IOException;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.MessageHandler;
import io.netty.buffer.ByteBuf;
import org.inspirenxe.server.network.ServerSession;
import org.inspirenxe.server.network.message.play.KeepAliveMessage;

public class KeepAliveCodec extends Codec<KeepAliveMessage> implements MessageHandler<ServerSession, KeepAliveMessage> {
    public KeepAliveCodec() {
        super(KeepAliveMessage.class);
    }

    @Override
    public KeepAliveMessage decode(ByteBuf buf) throws IOException {
        final int id = buf.readInt();
        return new KeepAliveMessage(id);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, KeepAliveMessage message) throws IOException {
        buf.writeInt(message.getId());
        return buf;
    }

    @Override
    public void handle(ServerSession session, KeepAliveMessage message) {
        session.getGame().getLogger().info(message);
    }
}
