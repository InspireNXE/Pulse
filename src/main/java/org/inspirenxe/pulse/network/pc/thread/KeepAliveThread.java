package org.inspirenxe.pulse.network.pc.thread;

import org.inspirenxe.pulse.SpongeGame;
import org.inspirenxe.pulse.network.pc.PCSession;
import org.spacehq.mc.protocol.data.SubProtocol;
import org.spacehq.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import org.spacehq.packetlib.Session;

public final class KeepAliveThread extends Thread {

    @Override
    public void run() {
        while (true) {
            SpongeGame.instance.getServer().getNetwork().getSessions().stream().filter(Session::isConnected).forEach
                    (session -> {
                final PCSession pcSession = (PCSession) session;
                if (pcSession.getPacketProtocol().getSubProtocol() == SubProtocol.GAME) {
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