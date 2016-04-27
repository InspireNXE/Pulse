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

import org.inspirenxe.pulse.SpongeGame;
import org.inspirenxe.pulse.network.PacketHandler;
import org.inspirenxe.pulse.network.pc.PCSession;
import org.inspirenxe.pulse.network.pc.protocol.ProtocolPhase;
import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.MinecraftConstants;
import org.spacehq.mc.protocol.data.message.TextMessage;
import org.spacehq.mc.protocol.data.status.PlayerInfo;
import org.spacehq.mc.protocol.data.status.ServerStatusInfo;
import org.spacehq.mc.protocol.data.status.VersionInfo;
import org.spacehq.mc.protocol.packet.status.client.StatusPingPacket;
import org.spacehq.mc.protocol.packet.status.client.StatusQueryPacket;
import org.spacehq.mc.protocol.packet.status.server.StatusPongPacket;
import org.spacehq.mc.protocol.packet.status.server.StatusResponsePacket;
import org.spacehq.packetlib.Session;

import java.util.ArrayList;
import java.util.List;

public final class StatusHandlers {

    @PacketHandler
    public void onStatusRequest(PCSession session, StatusQueryPacket packet) {
        final List<Session> sessions = SpongeGame.instance.getServer().getNetwork().getSessions();
        final List<GameProfile> profiles = new ArrayList<>();

        sessions.stream().filter(activeSession -> activeSession.isConnected() && activeSession instanceof PCSession
                && ((PCSession) activeSession).getPacketProtocol()
                .getProtocolPhase() == ProtocolPhase.INGAME).forEach(activeSession -> profiles.add(activeSession.getFlag(MinecraftConstants.
                PROFILE_KEY)));

        final VersionInfo versionInfo = new VersionInfo("1.9.x", MinecraftConstants.PROTOCOL_VERSION);
        final PlayerInfo playerInfo = new PlayerInfo(SpongeGame.instance.getServer().getMaxPlayers(), profiles.size(), profiles.toArray(new
                GameProfile[profiles.size()]));
        final ServerStatusInfo info = new ServerStatusInfo(versionInfo, playerInfo,  new TextMessage(SpongeGame.instance.getConfiguration().getMotd
                ()), null);
        session.send(new StatusResponsePacket(info));
    }

    @PacketHandler
    public void onStatusPing(PCSession session, StatusPingPacket packet) {
        session.send(new StatusPongPacket(packet.getPingTime()));
    }
}
