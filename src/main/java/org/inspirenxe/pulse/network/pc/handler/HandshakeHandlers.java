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
