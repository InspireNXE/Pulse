package org.inspirenxe.server.network.protocol;

import org.inspirenxe.server.Game;
import org.inspirenxe.server.network.codec.play.KeepAliveCodec;

public class PlayProtocol extends ServerProtocol {
    public PlayProtocol(Game game) {
        super(game, "play", 64);
        registerMessage(INBOUND, KeepAliveCodec.class, KeepAliveCodec.class);
        registerMessage(OUTBOUND, KeepAliveCodec.class, null);
    }
}
