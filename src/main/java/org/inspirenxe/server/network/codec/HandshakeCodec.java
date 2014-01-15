package org.inspirenxe.server.network.codec;

import java.io.IOException;

import com.flowpowered.networking.ByteBufUtils;
import com.flowpowered.networking.Codec;
import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.session.Session;
import io.netty.buffer.ByteBuf;
import org.inspirenxe.server.network.ServerSession;
import org.inspirenxe.server.network.message.HandshakeMessage;

public class HandshakeCodec extends Codec<HandshakeMessage> implements MessageHandler<HandshakeMessage> {
    private static final int OPCODE = 0;

    public HandshakeCodec() {
        super(HandshakeMessage.class, OPCODE);
    }

    @Override
    public HandshakeMessage decode(ByteBuf buf) throws IOException {
        final int version = ByteBufUtils.readVarInt(buf);
        final String address = ByteBufUtils.readUTF8(buf);
        final short port = (short) buf.readUnsignedShort();
        final HandshakeMessage.HandshakeState state = HandshakeMessage.HandshakeState.get(buf.readInt());
        return new HandshakeMessage(version, address, port, state);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, HandshakeMessage message) throws IOException {
        throw new IOException("The Minecraft client should not receive a handshake from the server!");
    }

    @Override
    public void handle(Session session, HandshakeMessage message) {
        ((ServerSession) session).getGame().getLogger().info(message);
    }
}
