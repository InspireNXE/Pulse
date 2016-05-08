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

import com.flowpowered.math.GenericMath;
import org.inspirenxe.pulse.SpongeGame;
import org.inspirenxe.pulse.entity.Entity;
import org.inspirenxe.pulse.network.PacketHandler;
import org.inspirenxe.pulse.network.pc.PCSession;
import org.inspirenxe.pulse.network.pc.protocol.ProtocolPhase;
import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.MinecraftConstants;
import org.spacehq.mc.protocol.data.game.MessageType;
import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityHeadLookPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityPositionRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.ServerEntityRotationPacket;
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

    @PacketHandler
    public void onClientPlayerPosition(PCSession session, ClientPlayerPositionPacket packet) {
        final Entity entity = session.getEntity();
        final double deltaX = packet.getX() - entity.x;
        final double deltaY = packet.getY() - entity.y;
        final double deltaZ = packet.getZ() - entity.z;

        entity.x = packet.getX();
        entity.y = packet.getY();
        entity.z = packet.getZ();
        entity.yaw = packet.getYaw();
        entity.headYaw = entity.yaw;
        entity.pitch = packet.getPitch();

        SpongeGame.instance.getServer().getNetwork().getSessions().stream().filter(otherSession -> otherSession.isConnected()
                && ((PCSession) otherSession).getPacketProtocol().getProtocolPhase() == ProtocolPhase.INGAME).forEach(otherSession -> {
                    if (otherSession != session) {
                        otherSession.send(new ServerEntityPositionRotationPacket(entity.id, deltaX, deltaY, deltaZ, GenericMath.floor(packet.getYaw()
                                * 256.0F / 360.0F), GenericMath.floor(packet.getPitch() * 256.0F / 360.0F), false));

                        otherSession.send(new ServerEntityHeadLookPacket(entity.id, GenericMath.floor(entity.headYaw * 256.0F / 360.0F)));
                    }
                }
        );
    }

    @PacketHandler
    public void onClientPlayerRotation(PCSession session, ClientPlayerRotationPacket packet) {
        final Entity entity = session.getEntity();
        entity.yaw = packet.getYaw();
        entity.headYaw = entity.yaw;
        entity.pitch = packet.getPitch();

        SpongeGame.instance.getServer().getNetwork().getSessions().stream().filter(otherSession -> otherSession.isConnected()
                && ((PCSession) otherSession).getPacketProtocol().getProtocolPhase() == ProtocolPhase.INGAME).forEach(otherSession -> {
                    if (otherSession != session) {
                        otherSession.send(new ServerEntityRotationPacket(entity.id, GenericMath.floor(packet.getYaw() * 256.0F / 360.0F),
                                GenericMath.floor(packet.getPitch() * 256.0F / 360.0F), false));

                        otherSession.send(new ServerEntityHeadLookPacket(entity.id, GenericMath.floor(entity.headYaw * 256.0F / 360.0F)));
                    }
                }
        );
    }

    @PacketHandler
    public void onClientPlayerPositionRotation(PCSession session, ClientPlayerPositionRotationPacket packet) {
        final Entity entity = session.getEntity();

        final double deltaX = packet.getX() - entity.x;
        final double deltaY = packet.getY() - entity.y;
        final double deltaZ = packet.getZ() - entity.z;

        entity.x = packet.getX();
        entity.y = packet.getY();
        entity.z = packet.getZ();
        entity.yaw = packet.getYaw();
        entity.headYaw = entity.yaw;
        entity.pitch = packet.getPitch();

        SpongeGame.instance.getServer().getNetwork().getSessions().stream().filter(otherSession -> otherSession.isConnected()
                && ((PCSession) otherSession).getPacketProtocol().getProtocolPhase() == ProtocolPhase.INGAME).forEach(otherSession -> {
                    if (otherSession != session) {
                        otherSession.send(new ServerEntityPositionRotationPacket(entity.id, deltaX, deltaY, deltaZ, GenericMath.floor(packet.getYaw()
                                * 256.0F / 360.0F), GenericMath.floor(packet.getPitch() * 256.0F / 360.0F), false));

                        otherSession.send(new ServerEntityHeadLookPacket(entity.id, GenericMath.floor(entity.headYaw * 256.0F / 360.0F)));
                    }
                }
        );
    }
}
