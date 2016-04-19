package org.inspirenxe.pulse.network.packet.util;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

public class ByteBufUtils {
    public static String readUTF8(ByteBuf buf, int maxLength) {
        int i = readVarInt(buf);
        if(i > maxLength * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")");
        } else if(i < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            String s = new String(buf.readBytes(i).array(), Charsets.UTF_8);
            if(s.length() > maxLength) {
                throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
            } else {
                return s;
            }
        }
    }

    public static void writeUTF8(ByteBuf buf, String string) {
        byte[] bytes = string.getBytes(Charsets.UTF_8);
        if(bytes.length > 32767) {
            throw new EncoderException("String too big (was " + string.length() + " bytes encoded, max " + 32767 + ")");
        } else {
            writeVarInt(buf, bytes.length);
            buf.writeBytes(bytes);
        }
    }

    public static int readVarInt(ByteBuf buf) {
        int i = 0;
        int j = 0;

        byte b0;
        do {
            b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;
            if(j > 5) {
                throw new RuntimeException("VarInt too big!");
            }
        } while((b0 & 128) == 128);

        return i;
    }

    public static void writeVarInt(ByteBuf buf, int input) {
        while((input & -128) != 0) {
            buf.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        buf.writeByte(input);
    }
}
