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
package org.inspirenxe.pulse.network.pc.handler;

import org.inspirenxe.pulse.network.PacketHandler;
import org.inspirenxe.pulse.network.pc.PCSession;
import org.inspirenxe.pulse.network.pc.protocol.PCProtocol;
import org.inspirenxe.pulse.network.pc.protocol.ProtocolPhase;
import org.spacehq.mc.protocol.packet.handshake.client.HandshakePacket;

public final class HandshakeHandlers {

    @PacketHandler
    public void onHandshake(PCSession session, HandshakePacket packet) {
        final PCProtocol protocol = session.getPacketProtocol();
        switch (packet.getIntent()) {
            case LOGIN:
                protocol.setProtocolPhase(ProtocolPhase.LOGIN);
                if (packet.getProtocolVersion() > 109) {
                    session.disconnect("Outdated server! I am still on 1.9.x.");
                } else if (packet.getProtocolVersion() < 107) {
                    session.disconnect("Outdated client! Please use 1.9.x.");
                } else if (packet.getProtocolVersion() == 107) {
                    protocol.is19 = true;
                }
                break;
            case STATUS:
                protocol.setProtocolPhase(ProtocolPhase.STATUS);
                break;
            default:
                throw new UnsupportedOperationException("Invalid client intent: " + packet.getIntent());

        }
    }
}
