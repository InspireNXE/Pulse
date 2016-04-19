package org.inspirenxe.pulse.network;

import com.flowpowered.network.service.CodecLookupService;
import com.flowpowered.network.service.HandlerLookupService;
import org.inspirenxe.pulse.network.handler.AnnotatedMessageHandler;
import org.inspirenxe.pulse.network.handler.HandshakeHandlers;
import org.inspirenxe.pulse.network.handler.LoginHandlers;
import org.inspirenxe.pulse.network.handler.PlayHandlers;
import org.inspirenxe.pulse.network.packet.handshake.HandshakePacket;
import org.inspirenxe.pulse.network.packet.login.LoginStartPacket;
import org.inspirenxe.pulse.network.packet.login.LoginSuccessPacket;
import org.inspirenxe.pulse.network.packet.play.JoinGamePacket;
import org.inspirenxe.pulse.network.packet.play.KeepAlivePacket;
import org.inspirenxe.pulse.network.packet.text.DisconnectPacket;

public enum ProtocolType implements MinecraftProtocol {

    HANDSHAKE("handshake", 0x01, new AnnotatedMessageHandler(HandshakeHandlers.instance))
    {
        {
            inbound(0x00, HandshakePacket.class, HandshakePacket.class);
        }
    },

    LOGIN("login", 0x03, new AnnotatedMessageHandler(LoginHandlers.instance))
    {
        {
            inbound(0x00, LoginStartPacket.class, LoginStartPacket.class);

            outbound(0x00, DisconnectPacket.class, DisconnectPacket.class);
            outbound(0x02, LoginSuccessPacket.class, LoginSuccessPacket.class);
        }
    },

    PLAY("play", 0xFF, new AnnotatedMessageHandler(PlayHandlers.instance)) {
        {
            inbound(0x0B, KeepAlivePacket.class, KeepAlivePacket.class);

            outbound(0x1F, KeepAlivePacket.class, KeepAlivePacket.class);
            outbound(0x23, JoinGamePacket.class, JoinGamePacket.class);
            outbound(0x1A, DisconnectPacket.class, DisconnectPacket.class);
        }
    };

    final String name;
    final CodecLookupService inboundCodecLookupService, outboundCodecLookupService;
    final HandlerLookupService inboundHandlerLookupService;
    final AnnotatedMessageHandler handlerManager;

    ProtocolType(String name, int maxPackets, AnnotatedMessageHandler handlerManager) {
        this.name = name;
        this.inboundCodecLookupService = new CodecLookupService(maxPackets);
        this.outboundCodecLookupService = new CodecLookupService(maxPackets);
        this.inboundHandlerLookupService = new HandlerLookupService();
        this.handlerManager = handlerManager;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CodecLookupService getInboundCodecLookupService() {
        return inboundCodecLookupService;
    }

    @Override
    public CodecLookupService getOutboundCodecLookupService() {
        return outboundCodecLookupService;
    }

    @Override
    public HandlerLookupService getInboundHandlerLookupService() {
        return inboundHandlerLookupService;
    }

    @Override
    public AnnotatedMessageHandler getHandlerManager() {
        return handlerManager;
    }
}
