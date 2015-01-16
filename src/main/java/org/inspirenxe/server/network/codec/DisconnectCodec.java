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
package org.inspirenxe.server.network.codec;

import java.io.IOException;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import org.inspirenxe.server.network.message.DisconnectMessage;

public class DisconnectCodec implements Codec<DisconnectMessage> {
    @Override
    public DisconnectMessage decode(ByteBuf buffer) throws IOException {
        throw new IOException("The server should not receive a disconnect from the Minecraft client!");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, DisconnectMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getJsonString());
        return buf;
    }
}
