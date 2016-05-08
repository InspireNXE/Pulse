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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.inspirenxe.pulse.SpongeGame;
import org.inspirenxe.pulse.entity.Entity;
import org.inspirenxe.pulse.network.PacketHandler;
import org.inspirenxe.pulse.network.SessionFlags;
import org.inspirenxe.pulse.network.pc.PCSession;
import org.inspirenxe.pulse.network.pc.protocol.ProtocolPhase;
import org.inspirenxe.pulse.network.pc.protocol.ingame.v190.server.ServerJoinGame190Packet;
import org.inspirenxe.pulse.network.pc.thread.SessionAuthenticatorThread;
import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.MinecraftConstants;
import org.spacehq.mc.protocol.data.game.PlayerListEntry;
import org.spacehq.mc.protocol.data.game.PlayerListEntryAction;
import org.spacehq.mc.protocol.data.game.chunk.Chunk;
import org.spacehq.mc.protocol.data.game.chunk.Column;
import org.spacehq.mc.protocol.data.game.entity.metadata.EntityMetadata;
import org.spacehq.mc.protocol.data.game.entity.metadata.MetadataType;
import org.spacehq.mc.protocol.data.game.entity.metadata.Position;
import org.spacehq.mc.protocol.data.game.entity.player.GameMode;
import org.spacehq.mc.protocol.data.game.setting.Difficulty;
import org.spacehq.mc.protocol.data.game.world.WorldType;
import org.spacehq.mc.protocol.data.game.world.block.BlockState;
import org.spacehq.mc.protocol.data.message.TextMessage;
import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerAbilitiesPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerSpawnPositionPacket;
import org.spacehq.mc.protocol.packet.login.client.EncryptionResponsePacket;
import org.spacehq.mc.protocol.packet.login.client.LoginStartPacket;
import org.spacehq.mc.protocol.packet.login.server.EncryptionRequestPacket;
import org.spacehq.mc.protocol.packet.login.server.LoginSetCompressionPacket;
import org.spacehq.mc.protocol.packet.login.server.LoginSuccessPacket;
import org.spacehq.mc.protocol.util.CryptUtil;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.tcp.io.ByteBufNetOutput;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.crypto.SecretKey;

public final class LoginHandlers {
    private static final KeyPair KEY_PAIR = CryptUtil.generateKeyPair();
    private final byte[] verifyToken = new byte[4];

    @PacketHandler
    public void onLoginStart(PCSession session, LoginStartPacket packet) {
        session.setFlag(SessionFlags.USERNAME, packet.getUsername());
        final Boolean verifyUsers = session.getFlag(MinecraftConstants.VERIFY_USERS_KEY);
        if (verifyUsers == null || verifyUsers) {
            session.send(new EncryptionRequestPacket("", KEY_PAIR.getPublic(), this.verifyToken));
        } else {
            final GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(("OfflinePlayer:" + packet.getUsername()).getBytes()),
                    packet.getUsername());

            for (Session activeSession : SpongeGame.instance.getServer().getNetwork().getSessions()) {
                if (activeSession == session) {
                    continue;
                }
                final GameProfile otherProfile = activeSession.getFlag(MinecraftConstants.PROFILE_KEY);

                // Session is being added before we have a GameProfile (early session initialization). We need to ignore this race.
                if (otherProfile == null) {
                    continue;
                }

                if (profile.getId().equals(otherProfile.getId())) {
                    activeSession.disconnect("Logged in from another location!");
                    break;
                }
            }
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
            session.getPacketProtocol().setProtocolPhase(ProtocolPhase.INGAME);

            final Entity entity = new Entity(profile.getId());
            if (!session.getPacketProtocol().is19) {
                session.send(new ServerJoinGamePacket(entity.id, true, GameMode.CREATIVE, 0, Difficulty.PEACEFUL, 10, WorldType.DEFAULT, false));
            } else {
                session.send(new ServerJoinGame190Packet(entity.id, true, GameMode.CREATIVE, 0, Difficulty.PEACEFUL, 10, WorldType.DEFAULT, false));
            }
            final ByteBuf buffer = Unpooled.buffer();
            final ByteBufNetOutput adapter = new ByteBufNetOutput(buffer);
            try {
                adapter.writeString(SpongeGame.ECOSYSTEM_NAME);
            } catch (IOException e) {
                SpongeGame.logger.error(e.toString());
            }
            session.send(new ServerPluginMessagePacket("MC|Brand", buffer.array()));
            entity.x = 0;
            entity.y = 64;
            entity.z = 0;
            session.send(new ServerSpawnPositionPacket(new Position(0, 64, 0)));
            session.send(new ServerPlayerAbilitiesPacket(false, false, true, true, 0.05f, 0.1f));
            session.send(new ServerPlayerPositionRotationPacket((int) entity.x, (int) entity.y, (int) entity.z, 0, 0, 0));

            SpongeGame.instance.getServer().getNetwork().getSessions().stream()
                    .filter(otherSession -> ((PCSession) otherSession).getPacketProtocol().getProtocolPhase() == ProtocolPhase.INGAME)
                    .forEach(otherSession -> {
                        otherSession
                                .send(new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER,
                                        new PlayerListEntry[]{new PlayerListEntry(profile,
                                                GameMode.CREATIVE, 10, new TextMessage(profile.getName()))}));

                        if (otherSession != session) {
                            final GameProfile otherProfile = otherSession.getFlag(MinecraftConstants.PROFILE_KEY);
                            if (otherProfile.getName() != null) {
                                session.send(
                                        new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER,
                                                new PlayerListEntry[]{new PlayerListEntry(otherProfile,
                                                        GameMode.CREATIVE, 10, new TextMessage(otherProfile.getName()))}));
                            }
                        }

                        otherSession.send(new ServerChatPacket(profile.getName() + " has joined the game."));
                    });

            session.setEntity(entity);

            SpongeGame.logger.info("{} has joined the game.", profile.getName());

            EntityMetadata[] metadata = new EntityMetadata[1];
            metadata[0] = new EntityMetadata(2, MetadataType.STRING, profile.getName());

            // Send your entity to other sessions
            SpongeGame.instance.getServer().getNetwork().getSessions().stream()
                    .filter(otherSession -> ((PCSession) otherSession).getPacketProtocol().getProtocolPhase() == ProtocolPhase.INGAME)
                    .forEach(otherSession -> {
                        if (session != otherSession) {
                            otherSession
                                    .send(new ServerSpawnPlayerPacket(entity.id, entity.uniqueId, (int) entity.x, (int) entity.y, (int) entity.z, 0,
                                            0, metadata));
                        }
                    });

            // Send other session entities to you
            SpongeGame.instance.getServer().getNetwork().getSessions().stream()
                    .filter(otherSession -> ((PCSession) otherSession).getPacketProtocol().getProtocolPhase() == ProtocolPhase.INGAME &&
                            ((PCSession) otherSession).getEntity() != null)
                    .forEach(otherSession -> {
                        if (session != otherSession) {
                            final GameProfile otherProfile = otherSession.getFlag(MinecraftConstants.PROFILE_KEY);
                            if (otherProfile != null) {
                                final Entity otherEntity = ((PCSession) otherSession).getEntity();
                                metadata[0] = new EntityMetadata(2, MetadataType.STRING, otherProfile.getName());
                                session
                                        .send(new ServerSpawnPlayerPacket(otherEntity.id, otherEntity.uniqueId, (int) otherEntity.x, (int) otherEntity.y, (int) otherEntity.z,
                                                0, 0, metadata));
                            }
                        }
                    });

            final List<Column> columns = new ArrayList<>();
            for (int cx = -5; cx < 5; cx++) {
                for (int cz = -5; cz < 5; cz++) {
                    final Chunk[] chunks = new Chunk[16];

                    int baseY = 0;
                    for (int section = 0; section < 16; section++) {
                        final Chunk chunk = new Chunk(true);

                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                for (int y = 0; y < 16; y++) {
                                    if (baseY == 0) {
                                        if (y == 0) {
                                            chunk.getBlocks().set(x, y, z, new BlockState(7, 0));
                                        } else if (y == 1) {
                                            if (new Random().nextFloat() > 0.3) {
                                                chunk.getBlocks().set(x, y, z, new BlockState(7, 0));
                                            }
                                        } else if (y == 2) {
                                            if (new Random().nextFloat() > 0.6) {
                                                chunk.getBlocks().set(x, y, z, new BlockState(7, 0));
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        baseY += 16;


                        chunk.getSkyLight().fill(15);
                        chunk.getBlockLight().fill(15);
                        chunks[section] = chunk;
                    }

                    final byte[] biomeData = new byte[256];
                    Arrays.fill(biomeData, (byte) 0);
                    columns.add(new Column(cx, cz, chunks, biomeData));
                }
            }

            for (Column column : columns) {
                session.send(new ServerChunkDataPacket(column));
            }

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
}
