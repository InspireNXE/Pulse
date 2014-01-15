package org.inspirenxe.server.network.protocol;

import org.inspirenxe.server.Game;
import org.inspirenxe.server.network.codec.HandshakeCodec;

public class HandshakeProtocol extends ServerProtocol {
    public HandshakeProtocol(Game game) {
        super(game, "handshake", 0);
        registerMessage(INBOUND, HandshakeCodec.class, HandshakeCodec.class);
    }
}
