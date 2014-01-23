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

import com.flowpowered.networking.ByteBufUtils;
import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import org.inspirenxe.server.network.message.play.JoinGameMessage;

public class JoinGameCodec implements Codec<JoinGameMessage> {
    @Override
    public JoinGameMessage decode(ByteBuf buf) throws IOException {
        throw new IOException("The server should not receive a join game from the Minecraft client!");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, JoinGameMessage message) throws IOException {
        buf.writeInt(message.getPlayerId());
        short gameMode = (short) message.getGameMode().value();
        if (message.isHardcore()) {
            gameMode |= 8;
        }
        buf.writeShort(gameMode);
        buf.writeByte(message.getDimension().value());
        buf.writeShort(message.getDifficulty().value());
        buf.writeShort(message.getMaxPlayers());
        ByteBufUtils.writeUTF8(buf, message.getLevelType().name());
        return buf;
    }
}

