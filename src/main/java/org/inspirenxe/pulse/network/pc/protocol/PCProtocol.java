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
package org.inspirenxe.pulse.network.pc.protocol;

import static com.google.common.base.Preconditions.checkNotNull;

import org.inspirenxe.pulse.network.pc.protocol.ingame.v190.server.ServerJoinGame190Packet;
import org.spacehq.mc.protocol.packet.handshake.client.HandshakePacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientRequestPacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientResourcePackStatusPacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientSettingsPacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerAbilitiesPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerInteractEntityPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerStatePacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import org.spacehq.mc.protocol.packet.ingame.client.window.ClientCloseWindowPacket;
import org.spacehq.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket;
import org.spacehq.mc.protocol.packet.ingame.client.window.ClientCreativeInventoryActionPacket;
import org.spacehq.mc.protocol.packet.ingame.client.window.ClientEnchantItemPacket;
import org.spacehq.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import org.spacehq.mc.protocol.packet.ingame.client.world.ClientSpectatePacket;
import org.spacehq.mc.protocol.packet.ingame.client.world.ClientSteerBoatPacket;
import org.spacehq.mc.protocol.packet.ingame.client.world.ClientSteerVehiclePacket;
import org.spacehq.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import org.spacehq.mc.protocol.packet.ingame.client.world.ClientUpdateSignPacket;
import org.spacehq.mc.protocol.packet.ingame.client.world.ClientVehicleMovePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerBossBarPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerCombatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerDifficultyPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPlayerListDataPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerResourcePackSendPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerRespawnPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerSetCooldownPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerStatisticsPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerSwitchCameraPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerTabCompletePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerTitlePacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityAnimationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityAttachPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityCollectItemPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityEffectPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityEquipmentPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityHeadLookPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityMovementPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityPositionRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityPropertiesPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityRemoveEffectPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntitySetPassengersPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityStatusPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerVehicleMovePacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerAbilitiesPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerChangeHeldItemPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerSetExperiencePacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerUseBedPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnExpOrbPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnGlobalEntityPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnObjectPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPaintingPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import org.spacehq.mc.protocol.packet.ingame.server.scoreboard.ServerDisplayScoreboardPacket;
import org.spacehq.mc.protocol.packet.ingame.server.scoreboard.ServerScoreboardObjectivePacket;
import org.spacehq.mc.protocol.packet.ingame.server.scoreboard.ServerTeamPacket;
import org.spacehq.mc.protocol.packet.ingame.server.scoreboard.ServerUpdateScorePacket;
import org.spacehq.mc.protocol.packet.ingame.server.window.ServerCloseWindowPacket;
import org.spacehq.mc.protocol.packet.ingame.server.window.ServerConfirmTransactionPacket;
import org.spacehq.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import org.spacehq.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import org.spacehq.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import org.spacehq.mc.protocol.packet.ingame.server.window.ServerWindowPropertyPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerBlockBreakAnimPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerBlockValuePacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerExplosionPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerMapDataPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerMultiBlockChangePacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerNotifyClientPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerOpenTileEntityEditorPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerPlayBuiltinSoundPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerPlayEffectPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerPlaySoundPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerSpawnParticlePacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerSpawnPositionPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerUnloadChunkPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerUpdateSignPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerUpdateTileEntityPacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerUpdateTimePacket;
import org.spacehq.mc.protocol.packet.ingame.server.world.ServerWorldBorderPacket;
import org.spacehq.mc.protocol.packet.login.client.EncryptionResponsePacket;
import org.spacehq.mc.protocol.packet.login.client.LoginStartPacket;
import org.spacehq.mc.protocol.packet.login.server.EncryptionRequestPacket;
import org.spacehq.mc.protocol.packet.login.server.LoginDisconnectPacket;
import org.spacehq.mc.protocol.packet.login.server.LoginSetCompressionPacket;
import org.spacehq.mc.protocol.packet.login.server.LoginSuccessPacket;
import org.spacehq.mc.protocol.packet.status.client.StatusPingPacket;
import org.spacehq.mc.protocol.packet.status.client.StatusQueryPacket;
import org.spacehq.mc.protocol.packet.status.server.StatusPongPacket;
import org.spacehq.mc.protocol.packet.status.server.StatusResponsePacket;
import org.spacehq.packetlib.Client;
import org.spacehq.packetlib.Server;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.crypt.AESEncryption;
import org.spacehq.packetlib.crypt.PacketEncryption;
import org.spacehq.packetlib.packet.DefaultPacketHeader;
import org.spacehq.packetlib.packet.PacketHeader;
import org.spacehq.packetlib.packet.PacketProtocol;

import java.security.GeneralSecurityException;
import java.security.Key;

public final class PCProtocol extends PacketProtocol {

    private ProtocolPhase protocolPhase;
    private PacketHeader header;
    private AESEncryption encrypt;

    // TODO Protocol versioning
    public boolean is19 = false;

    private PCProtocol() {
        this.protocolPhase = ProtocolPhase.HANDSHAKE;
        this.header = new DefaultPacketHeader();
    }

    @Override
    public String getSRVRecordPrefix() {
        return "_minecraft";
    }

    @Override
    public PacketHeader getPacketHeader() {
        return this.header;
    }

    @Override
    public PacketEncryption getEncryption() {
        return this.encrypt;
    }

    @Override
    public void newClientSession(Client client, Session session) {
        // We don't do client
    }

    @Override
    public void newServerSession(Server server, Session session) {
        this.setProtocolPhase(ProtocolPhase.HANDSHAKE);
    }

    public ProtocolPhase getProtocolPhase() {
        return this.protocolPhase;
    }

    public void setProtocolPhase(ProtocolPhase protocolPhase) {
        checkNotNull(protocolPhase);
        this.clearPackets();
        switch (protocolPhase) {
            case HANDSHAKE:
                this.initServerHandshake();
                break;
            case LOGIN:
                this.initServerLogin();
                break;
            case INGAME:
                this.initServerGame();
                break;
            case STATUS:
                this.initServerStatus();
                break;
        }
        this.protocolPhase = protocolPhase;
    }

    private void initServerHandshake() {
        this.registerIncoming(0, HandshakePacket.class);
    }

    private void initServerLogin() {
        this.registerIncoming(0, LoginStartPacket.class);
        this.registerIncoming(1, EncryptionResponsePacket.class);
        this.registerOutgoing(0, LoginDisconnectPacket.class);
        this.registerOutgoing(1, EncryptionRequestPacket.class);
        this.registerOutgoing(2, LoginSuccessPacket.class);
        this.registerOutgoing(3, LoginSetCompressionPacket.class);
    }

    private void initServerGame() {
        this.registerIncoming(0, ClientTeleportConfirmPacket.class);
        this.registerIncoming(1, ClientTabCompletePacket.class);
        this.registerIncoming(2, ClientChatPacket.class);
        this.registerIncoming(3, ClientRequestPacket.class);
        this.registerIncoming(4, ClientSettingsPacket.class);
        this.registerIncoming(5, ClientConfirmTransactionPacket.class);
        this.registerIncoming(6, ClientEnchantItemPacket.class);
        this.registerIncoming(7, ClientWindowActionPacket.class);
        this.registerIncoming(8, ClientCloseWindowPacket.class);
        this.registerIncoming(9, ClientPluginMessagePacket.class);
        this.registerIncoming(10, ClientPlayerInteractEntityPacket.class);
        this.registerIncoming(11, ClientKeepAlivePacket.class);
        this.registerIncoming(12, ClientPlayerPositionPacket.class);
        this.registerIncoming(13, ClientPlayerPositionRotationPacket.class);
        this.registerIncoming(14, ClientPlayerRotationPacket.class);
        this.registerIncoming(15, ClientPlayerMovementPacket.class);
        this.registerIncoming(16, ClientVehicleMovePacket.class);
        this.registerIncoming(17, ClientSteerBoatPacket.class);
        this.registerIncoming(18, ClientPlayerAbilitiesPacket.class);
        this.registerIncoming(19, ClientPlayerActionPacket.class);
        this.registerIncoming(20, ClientPlayerStatePacket.class);
        this.registerIncoming(21, ClientSteerVehiclePacket.class);
        this.registerIncoming(22, ClientResourcePackStatusPacket.class);
        this.registerIncoming(23, ClientPlayerChangeHeldItemPacket.class);
        this.registerIncoming(24, ClientCreativeInventoryActionPacket.class);
        this.registerIncoming(25, ClientUpdateSignPacket.class);
        this.registerIncoming(26, ClientPlayerSwingArmPacket.class);
        this.registerIncoming(27, ClientSpectatePacket.class);
        this.registerIncoming(28, ClientPlayerPlaceBlockPacket.class);
        this.registerIncoming(29, ClientPlayerUseItemPacket.class);
        this.registerOutgoing(0, ServerSpawnObjectPacket.class);
        this.registerOutgoing(1, ServerSpawnExpOrbPacket.class);
        this.registerOutgoing(2, ServerSpawnGlobalEntityPacket.class);
        this.registerOutgoing(3, ServerSpawnMobPacket.class);
        this.registerOutgoing(4, ServerSpawnPaintingPacket.class);
        this.registerOutgoing(5, ServerSpawnPlayerPacket.class);
        this.registerOutgoing(6, ServerEntityAnimationPacket.class);
        this.registerOutgoing(7, ServerStatisticsPacket.class);
        this.registerOutgoing(8, ServerBlockBreakAnimPacket.class);
        this.registerOutgoing(9, ServerUpdateTileEntityPacket.class);
        this.registerOutgoing(10, ServerBlockValuePacket.class);
        this.registerOutgoing(11, ServerBlockChangePacket.class);
        this.registerOutgoing(12, ServerBossBarPacket.class);
        this.registerOutgoing(13, ServerDifficultyPacket.class);
        this.registerOutgoing(14, ServerTabCompletePacket.class);
        this.registerOutgoing(15, ServerChatPacket.class);
        this.registerOutgoing(16, ServerMultiBlockChangePacket.class);
        this.registerOutgoing(17, ServerConfirmTransactionPacket.class);
        this.registerOutgoing(18, ServerCloseWindowPacket.class);
        this.registerOutgoing(19, ServerOpenWindowPacket.class);
        this.registerOutgoing(20, ServerWindowItemsPacket.class);
        this.registerOutgoing(21, ServerWindowPropertyPacket.class);
        this.registerOutgoing(22, ServerSetSlotPacket.class);
        this.registerOutgoing(23, ServerSetCooldownPacket.class);
        this.registerOutgoing(24, ServerPluginMessagePacket.class);
        this.registerOutgoing(25, ServerPlaySoundPacket.class);
        this.registerOutgoing(26, ServerDisconnectPacket.class);
        this.registerOutgoing(27, ServerEntityStatusPacket.class);
        this.registerOutgoing(28, ServerExplosionPacket.class);
        this.registerOutgoing(29, ServerUnloadChunkPacket.class);
        this.registerOutgoing(30, ServerNotifyClientPacket.class);
        this.registerOutgoing(31, ServerKeepAlivePacket.class);
        this.registerOutgoing(32, ServerChunkDataPacket.class);
        this.registerOutgoing(33, ServerPlayEffectPacket.class);
        this.registerOutgoing(34, ServerSpawnParticlePacket.class);
        if (is19) {
            this.registerOutgoing(35, ServerJoinGame190Packet.class);
        } else {
            this.registerOutgoing(35, ServerJoinGamePacket.class);
        }
        this.registerOutgoing(36, ServerMapDataPacket.class);
        this.registerOutgoing(37, ServerEntityPositionPacket.class);
        this.registerOutgoing(38, ServerEntityPositionRotationPacket.class);
        this.registerOutgoing(39, ServerEntityRotationPacket.class);
        this.registerOutgoing(40, ServerEntityMovementPacket.class);
        this.registerOutgoing(41, ServerVehicleMovePacket.class);
        this.registerOutgoing(42, ServerOpenTileEntityEditorPacket.class);
        this.registerOutgoing(43, ServerPlayerAbilitiesPacket.class);
        this.registerOutgoing(44, ServerCombatPacket.class);
        this.registerOutgoing(45, ServerPlayerListEntryPacket.class);
        this.registerOutgoing(46, ServerPlayerPositionRotationPacket.class);
        this.registerOutgoing(47, ServerPlayerUseBedPacket.class);
        this.registerOutgoing(48, ServerEntityDestroyPacket.class);
        this.registerOutgoing(49, ServerEntityRemoveEffectPacket.class);
        this.registerOutgoing(50, ServerResourcePackSendPacket.class);
        this.registerOutgoing(51, ServerRespawnPacket.class);
        this.registerOutgoing(52, ServerEntityHeadLookPacket.class);
        this.registerOutgoing(53, ServerWorldBorderPacket.class);
        this.registerOutgoing(54, ServerSwitchCameraPacket.class);
        this.registerOutgoing(55, ServerPlayerChangeHeldItemPacket.class);
        this.registerOutgoing(56, ServerDisplayScoreboardPacket.class);
        this.registerOutgoing(57, ServerEntityMetadataPacket.class);
        this.registerOutgoing(58, ServerEntityAttachPacket.class);
        this.registerOutgoing(59, ServerEntityVelocityPacket.class);
        this.registerOutgoing(60, ServerEntityEquipmentPacket.class);
        this.registerOutgoing(61, ServerPlayerSetExperiencePacket.class);
        this.registerOutgoing(62, ServerPlayerHealthPacket.class);
        this.registerOutgoing(63, ServerScoreboardObjectivePacket.class);
        this.registerOutgoing(64, ServerEntitySetPassengersPacket.class);
        this.registerOutgoing(65, ServerTeamPacket.class);
        this.registerOutgoing(66, ServerUpdateScorePacket.class);
        this.registerOutgoing(67, ServerSpawnPositionPacket.class);
        this.registerOutgoing(68, ServerUpdateTimePacket.class);
        this.registerOutgoing(69, ServerTitlePacket.class);
        this.registerOutgoing(70, ServerUpdateSignPacket.class);
        this.registerOutgoing(71, ServerPlayBuiltinSoundPacket.class);
        this.registerOutgoing(72, ServerPlayerListDataPacket.class);
        this.registerOutgoing(73, ServerEntityCollectItemPacket.class);
        this.registerOutgoing(74, ServerEntityTeleportPacket.class);
        this.registerOutgoing(75, ServerEntityPropertiesPacket.class);
        this.registerOutgoing(76, ServerEntityEffectPacket.class);
    }

    private void initServerStatus() {
        this.registerIncoming(0, StatusQueryPacket.class);
        this.registerIncoming(1, StatusPingPacket.class);
        this.registerOutgoing(0, StatusResponsePacket.class);
        this.registerOutgoing(1, StatusPongPacket.class);
    }

    public void enableEncryption(Key key) {
        try {
            this.encrypt = new AESEncryption(key);
        } catch (GeneralSecurityException var3) {
            throw new Error("Failed to enable protocol encryption.", var3);
        }
    }
}
