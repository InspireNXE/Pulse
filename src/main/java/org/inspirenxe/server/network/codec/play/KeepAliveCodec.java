package org.inspirenxe.server.network.codec.play;

import java.io.IOException;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.session.Session;
import io.netty.buffer.ByteBuf;
import org.inspirenxe.server.network.ServerSession;
import org.inspirenxe.server.network.message.play.KeepAliveMessage;

public class KeepAliveCodec extends Codec<KeepAliveMessage> implements MessageHandler<KeepAliveMessage> {
    private static final int OPCODE = 0;

    public KeepAliveCodec() {
        super(KeepAliveMessage.class, OPCODE);
    }

    @Override
    public KeepAliveMessage decode(ByteBuf buf) throws IOException {
        final int id = buf.readInt();
        return new KeepAliveMessage(id);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, KeepAliveMessage message) throws IOException {
        buf.writeInt(message.getId());
        return buf;
    }

    @Override
    public void handle(Session session, KeepAliveMessage message) {
        ((ServerSession) session).getGame().getLogger().info(message);
    }
}
