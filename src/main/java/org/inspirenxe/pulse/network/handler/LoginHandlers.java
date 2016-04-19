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
package org.inspirenxe.pulse.network.handler;

import org.inspirenxe.pulse.SpongeGame;
import org.inspirenxe.pulse.network.MinecraftSession;
import org.inspirenxe.pulse.network.ProtocolType;
import org.inspirenxe.pulse.network.packet.login.LoginStartPacket;
import org.inspirenxe.pulse.network.packet.login.LoginSuccessPacket;

import java.util.UUID;

public final class LoginHandlers {
    public static final LoginHandlers INSTANCE = new LoginHandlers();

    @AnnotatedMessageHandler.Handle
    public void onLoginStart(MinecraftSession session, LoginStartPacket message) {
        SpongeGame.logger.error(session + " " + message);
        session.send(MinecraftSession.SendType.FORCE, new LoginSuccessPacket(UUID.randomUUID().toString(), message.getUsername()));
        session.switchToProtocol(ProtocolType.PLAY);
    }
}
