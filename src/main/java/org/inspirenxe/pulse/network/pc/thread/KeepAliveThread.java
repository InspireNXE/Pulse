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
package org.inspirenxe.pulse.network.pc.thread;

import org.inspirenxe.pulse.SpongeGame;
import org.inspirenxe.pulse.network.pc.PCSession;
import org.inspirenxe.pulse.network.pc.protocol.ProtocolPhase;
import org.spacehq.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import org.spacehq.packetlib.Session;

public final class KeepAliveThread extends Thread {
    public KeepAliveThread() {
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            SpongeGame.instance.getServer().getNetwork().getSessions().stream().filter(Session::isConnected).forEach
                    (session -> {
                final PCSession pcSession = (PCSession) session;
                if (pcSession.getPacketProtocol().getProtocolPhase() == ProtocolPhase.INGAME) {
                    pcSession.setLastPingId(pcSession.getLastPingTime());
                    pcSession.setLastPingTime(System.currentTimeMillis());
                    pcSession.send(new ServerKeepAlivePacket((int) pcSession.getLastPingId()));
                }
            });

            try {
                Thread.sleep(2000);
                continue;
            } catch (InterruptedException ignored) {}

            return;
        }
    }
}
