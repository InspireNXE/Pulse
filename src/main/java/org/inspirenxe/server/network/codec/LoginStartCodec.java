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
package org.inspirenxe.server.network.codec;

import java.io.IOException;

import com.flowpowered.networking.ByteBufUtils;
import com.flowpowered.networking.Codec;
import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.session.Session;
import io.netty.buffer.ByteBuf;
import org.inspirenxe.server.network.ServerSession;
import org.inspirenxe.server.network.message.LoginStartMessage;

public class LoginStartCodec extends Codec<LoginStartMessage> implements MessageHandler<LoginStartMessage> {
    private static final int OPCODE = 0;

    public LoginStartCodec() {
        super(LoginStartMessage.class, OPCODE);
    }

    @Override
    public LoginStartMessage decode(ByteBuf buf) throws IOException {
        final String username = ByteBufUtils.readUTF8(buf);
        return new LoginStartMessage(username);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, LoginStartMessage message) throws IOException {
        throw new IOException("The Minecraft client should not receive a login start from the server!");
    }

    @Override
    public void handle(Session session, LoginStartMessage message) {
        ((ServerSession) session).getGame().getLogger().info(message);
    }
}
