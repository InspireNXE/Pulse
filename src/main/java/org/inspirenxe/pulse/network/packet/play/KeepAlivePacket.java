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
package org.inspirenxe.pulse.network.packet.play;

import com.flowpowered.network.Codec;
import com.flowpowered.network.Message;
import com.google.common.base.Objects;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class KeepAlivePacket implements Message, Codec<KeepAlivePacket> {
    private int id;

    public KeepAlivePacket() {}

    public KeepAlivePacket(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public KeepAlivePacket decode(ByteBuf buf) throws IOException {
        final int id = buf.readInt();
        return new KeepAlivePacket(id);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, KeepAlivePacket message) throws IOException {
        buf.writeInt(message.getId());
        return buf;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .toString();
    }
}

