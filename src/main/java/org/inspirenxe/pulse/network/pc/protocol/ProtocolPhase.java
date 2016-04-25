package org.inspirenxe.pulse.network.pc.protocol;

import org.inspirenxe.pulse.network.PacketHandlerInvoker;
import org.inspirenxe.pulse.network.pc.handler.HandshakeHandlers;
import org.inspirenxe.pulse.network.pc.handler.IngameHandlers;
import org.inspirenxe.pulse.network.pc.handler.LoginHandlers;
import org.inspirenxe.pulse.network.pc.handler.StatusHandlers;

public enum ProtocolPhase {
    HANDSHAKE(new HandshakeHandlers()),
    LOGIN(new LoginHandlers()),
    INGAME(new IngameHandlers()),
    STATUS(new StatusHandlers());

    final PacketHandlerInvoker handlerInvoker;

    ProtocolPhase(Object handler) {
        this.handlerInvoker = new PacketHandlerInvoker(handler);
    }

    public PacketHandlerInvoker getHandlerInvoker() {
        return this.handlerInvoker;
    }
}
