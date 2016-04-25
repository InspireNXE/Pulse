package org.inspirenxe.pulse.network.pc.handler;

import org.inspirenxe.pulse.SpongeGame;
import org.inspirenxe.pulse.network.PacketHandler;
import org.inspirenxe.pulse.network.pc.PCSession;
import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.MinecraftConstants;
import org.spacehq.mc.protocol.data.game.MessageType;
import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.packetlib.Session;

public final class IngameHandlers {

    @PacketHandler
    public void onKeepAlive(PCSession session, ClientKeepAlivePacket packet) {
        if (packet.getPingId() == session.getLastPingId()) {
            session.setFlag(MinecraftConstants.PING_KEY, System.currentTimeMillis() - session.getLastPingTime());
        }
    }

    @PacketHandler
    public void onServerChat(PCSession session, ClientChatPacket packet) {
        final String mesaage = packet.getMessage();
        final GameProfile profile = session.getFlag(MinecraftConstants.PROFILE_KEY);
        boolean command = mesaage.startsWith("/");
        final String outgoing = "<" + profile.getName() + "> " + mesaage;
        if (!command) {
            for (Session activeSession : SpongeGame.instance.getServer().getNetwork().getSessions()) {
                activeSession.send(new ServerChatPacket(outgoing, MessageType.CHAT));
            }
        } else {
            session.send(new ServerChatPacket(outgoing, MessageType.CHAT));
        }

        if (command) {
            SpongeGame.logger.info("{} issued command: {}", profile.getName(), mesaage);
        } else {
            SpongeGame.logger.info(outgoing);
        }
    }
}
