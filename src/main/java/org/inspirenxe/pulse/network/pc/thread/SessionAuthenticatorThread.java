package org.inspirenxe.pulse.network.pc.thread;

import static com.google.common.base.Preconditions.checkNotNull;

import org.inspirenxe.pulse.network.SessionFlags;
import org.inspirenxe.pulse.network.pc.protocol.PCProtocol;
import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.auth.exception.request.RequestException;
import org.spacehq.mc.auth.service.SessionService;
import org.spacehq.mc.protocol.MinecraftConstants;
import org.spacehq.mc.protocol.data.SubProtocol;
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
        ((PCProtocol)this.session.getPacketProtocol()).setSubProtocol(SubProtocol.GAME);
    }
}
