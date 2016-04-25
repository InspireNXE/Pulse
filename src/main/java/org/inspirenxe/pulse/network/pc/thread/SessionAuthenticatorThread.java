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
package org.inspirenxe.pulse.network.pc.thread;

import static com.google.common.base.Preconditions.checkNotNull;

import org.inspirenxe.pulse.network.SessionFlags;
import org.inspirenxe.pulse.network.pc.protocol.PCProtocol;
import org.inspirenxe.pulse.network.pc.protocol.ProtocolPhase;
import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.auth.exception.request.RequestException;
import org.spacehq.mc.auth.service.SessionService;
import org.spacehq.mc.protocol.MinecraftConstants;
import org.spacehq.mc.protocol.packet.login.server.LoginSetCompressionPacket;
import org.spacehq.mc.protocol.packet.login.server.LoginSuccessPacket;
import org.spacehq.mc.protocol.util.CryptUtil;
import org.spacehq.packetlib.Session;

import java.math.BigInteger;
import java.net.Proxy;
import java.security.KeyPair;

import javax.crypto.SecretKey;

public final class SessionAuthenticatorThread extends Thread {
    private final Session session;
    private final SecretKey secretKey;
    private final KeyPair keyPair;
    private final String serverId;

    public SessionAuthenticatorThread(Session session, SecretKey secretKey, KeyPair keyPair, String serverId) {
        checkNotNull(session);
        checkNotNull(secretKey);
        checkNotNull(keyPair);
        checkNotNull(serverId);
        this.session = session;
        this.secretKey = secretKey;
        this.keyPair = keyPair;
        this.serverId = serverId;
        setDaemon(true);
    }

    @Override
    public void run() {
        final String username = this.session.getFlag(SessionFlags.USERNAME);
        Proxy authProxy = this.session.getFlag(MinecraftConstants.AUTH_PROXY_KEY);
        if (authProxy == null) {
            authProxy = Proxy.NO_PROXY;
        }

        GameProfile profile;
        try {
            profile = (new SessionService(authProxy)).getProfileByServer(username, (new BigInteger(
                    CryptUtil.getServerIdHash(this.serverId, this.keyPair.getPublic(), this.secretKey))).toString(16));
        } catch (RequestException ex) {
            this.session.disconnect("Failed to make session service request.", ex);
            return;
        }

        if (profile == null) {
            this.session.disconnect("Failed to verify username.");
            return;
        }

        if (this.session.hasFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD)) {
            final int compressionThreshold = this.session.getFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD);
            this.session.send(new LoginSetCompressionPacket(compressionThreshold));
            this.session.setCompressionThreshold(compressionThreshold);
        } else {
            this.session.setCompressionThreshold(-1);
        }
        this.session.send(new LoginSuccessPacket(profile));
        this.session.setFlag(MinecraftConstants.PROFILE_KEY, profile);
        ((PCProtocol)this.session.getPacketProtocol()).setProtocolPhase(ProtocolPhase.INGAME);
    }
}
