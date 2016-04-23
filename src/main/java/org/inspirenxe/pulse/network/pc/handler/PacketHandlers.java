package org.inspirenxe.pulse.network.pc.handler;

import org.inspirenxe.pulse.network.PacketHandler;
import org.inspirenxe.pulse.network.SessionFlags;
import org.inspirenxe.pulse.network.pc.PCProtocol;
import org.inspirenxe.pulse.network.pc.PCSession;
import org.inspirenxe.pulse.network.pc.thread.SessionAuthenticatorThread;
import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.MinecraftConstants;
import org.spacehq.mc.protocol.data.SubProtocol;
import org.spacehq.mc.protocol.packet.handshake.client.HandshakePacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import org.spacehq.mc.protocol.packet.login.client.EncryptionResponsePacket;
import org.spacehq.mc.protocol.packet.login.client.LoginStartPacket;
import org.spacehq.mc.protocol.packet.login.server.EncryptionRequestPacket;
import org.spacehq.mc.protocol.packet.login.server.LoginSetCompressionPacket;
import org.spacehq.mc.protocol.packet.login.server.LoginSuccessPacket;
import org.spacehq.mc.protocol.util.CryptUtil;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.SecretKey;

public final class PacketHandlers  {
    private static final KeyPair KEY_PAIR = CryptUtil.generateKeyPair();
    private final byte[] verifyToken = new byte[4];

    @PacketHandler
    public void onHandshake(PCSession session, HandshakePacket packet) {
        final PCProtocol protocol = session.getPacketProtocol();
        switch (packet.getIntent()) {
            case LOGIN:
                protocol.setSubProtocol(SubProtocol.LOGIN);
                if (packet.getProtocolVersion() > 109) {
                    session.disconnect("Outdated server! I am still on 1.9.2.");
                } else if (packet.getProtocolVersion() < 109) {
                    session.disconnect("Outdated client! Please use 1.9.2.");
                }
                break;
            case STATUS:
                protocol.setSubProtocol(SubProtocol.STATUS);
                break;
            default:
                throw new UnsupportedOperationException("Invalid client intent: " + packet.getIntent());

        }
    }

    @PacketHandler
    public void onLoginStart(PCSession session, LoginStartPacket packet) {
        session.setFlag(SessionFlags.USERNAME, packet.getUsername());
        final Boolean verifyUsers = session.getFlag(MinecraftConstants.VERIFY_USERS_KEY);
        if (verifyUsers == null || verifyUsers) {
            session.send(new EncryptionRequestPacket("", KEY_PAIR.getPublic(), this.verifyToken));
        } else {
            final GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(("OfflinePlayer:" + packet.getUsername()).getBytes()),
                    packet.getUsername());
            session.setFlag(MinecraftConstants.PROFILE_KEY, profile);
            if (session.hasFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD)) {
                final int compressionThreshold = session.getFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD);
                session.send(new LoginSetCompressionPacket(compressionThreshold));
                session.setCompressionThreshold(compressionThreshold);
            } else {
                session.setCompressionThreshold(-1);
            }
            session.send(new LoginSuccessPacket(profile));
            session.setFlag(MinecraftConstants.PROFILE_KEY, profile);
            session.getPacketProtocol().setSubProtocol(SubProtocol.GAME);
        }
    }

    @PacketHandler
    public void onEncryptionResponse(PCSession session, EncryptionResponsePacket packet) {
        final PrivateKey time = KEY_PAIR.getPrivate();
        if(!Arrays.equals(this.verifyToken, packet.getVerifyToken(time))) {
            session.disconnect("Invalid nonce!");
            return;
        }

        SecretKey key = packet.getSecretKey(time);
        session.getPacketProtocol().enableEncryption(key);
        new SessionAuthenticatorThread(session, key, KEY_PAIR, "").start();
    }

    @PacketHandler
    public void onKeepAlive(PCSession session, ClientKeepAlivePacket packet) {
        if (packet.getPingId() == session.getLastPingId()) {
            session.setFlag(MinecraftConstants.PING_KEY, System.currentTimeMillis() - session.getLastPingTime());
        }
    }


//        final GameProfile profile = session.getFlag(MinecraftConstants.PROFILE_KEY);
//        session.send(new ServerJoinGamePacket(0, true, GameMode.SURVIVAL, 0, Difficulty.PEACEFUL, 10, WorldType.DEFAULT, false));
//        final ByteBuf buffer = Unpooled.buffer();
//        final ByteBufNetOutput adapter = new ByteBufNetOutput(buffer);
//        try {
//            adapter.writeString(SpongeGame.ECOSYSTEM_NAME);
//        } catch (IOException e) {
//            SpongeGame.logger.error(e.toString());
//        }
//        session.send(new ServerPluginMessagePacket("MC|Brand", buffer.array()));
//        session.send(new ServerSpawnPositionPacket(new Position(0, 64, 0)));
//        session.send(new ServerPlayerAbilitiesPacket(false, false, false, true, 0.05f, 0.1f));
//        session.send(new ServerPlayerPositionRotationPacket(0, 64, 0, 0, 0, 0));
////        for (Session activeSession : sessions) {
////            activeSession
////                    .send(new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, new PlayerListEntry[]{new PlayerListEntry(profile,
////                            GameMode.CREATIVE, 10, new TextMessage(profile.getName()))}));
////
////            if (activeSession != session) {
////                final GameProfile otherProfile = activeSession.getFlag(MinecraftConstants.PROFILE_KEY);
////                session.send(
////                        new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, new PlayerListEntry[]{new PlayerListEntry(otherProfile,
////                                GameMode.CREATIVE, 10, new TextMessage(otherProfile.getName()))}));
////            }
////
////            activeSession.send(new ServerChatPacket(profile.getName() + " has joined the game."));
////        }
//

}
