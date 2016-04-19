package org.inspirenxe.pulse.network.handler;

import org.inspirenxe.pulse.SpongeGame;
import org.inspirenxe.pulse.network.MinecraftSession;
import org.inspirenxe.pulse.network.packet.play.KeepAlivePacket;

public final class PlayHandlers {
    public static final PlayHandlers instance = new PlayHandlers();

    @AnnotatedMessageHandler.Handle
    public void onKeepAlive(MinecraftSession session, KeepAlivePacket packet) {
        SpongeGame.logger.error(session + " " + packet);
    }
}
