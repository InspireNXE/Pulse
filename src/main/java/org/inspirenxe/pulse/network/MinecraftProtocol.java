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
package org.inspirenxe.pulse.network;

import com.flowpowered.network.Codec;
import com.flowpowered.network.Message;
import com.flowpowered.network.MessageHandler;
import com.flowpowered.network.exception.IllegalOpcodeException;
import com.flowpowered.network.exception.UnknownPacketException;
import com.flowpowered.network.protocol.Protocol;
import com.flowpowered.network.service.CodecLookupService;
import com.flowpowered.network.service.HandlerLookupService;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.inspirenxe.pulse.SpongeGame;
import org.inspirenxe.pulse.network.handler.AnnotatedMessageHandler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface MinecraftProtocol extends Protocol {
    /**
     * From Client
     */
    String INBOUND = "INBOUND";
    /**
     * To Client
     */
    String OUTBOUND = "OUTBOUND";

    CodecLookupService getInboundCodecLookupService();

    CodecLookupService getOutboundCodecLookupService();

    HandlerLookupService getInboundHandlerLookupService();

    AnnotatedMessageHandler getHandlerManager();

    default  <M extends Message, C extends Codec<? super M>> void inbound(int opcode, Class<M> message, Class<C> codec) {
        registerMessage(INBOUND, message, codec, null, opcode);
    }

    default  <M extends Message, C extends Codec<? super M>> void outbound(int opcode, Class<M> message, Class<C> codec) {
        registerMessage(OUTBOUND, message, codec, null, opcode);
    }

    default <M extends Message, C extends Codec<? super M>, H extends MessageHandler<?, ? super M>> Codec.CodecRegistration registerMessage(String
            serviceKey, Class<M> message, Class<C> codec, Class<H> handler, Integer opcode) {
        CodecLookupService codecLookupService;
        HandlerLookupService handlerLookupService = null;

        switch (serviceKey) {
            case INBOUND:
                codecLookupService = getInboundCodecLookupService();
                handlerLookupService = getInboundHandlerLookupService();
                break;
            case OUTBOUND:
                codecLookupService = getOutboundCodecLookupService();
                break;
            default:
                throw new RuntimeException("Invalid service key provided! Options available are [" + INBOUND + ", " + OUTBOUND + "].");
        }
        try {
            Codec.CodecRegistration bind = codecLookupService.bind(message, codec, opcode);
            if (bind != null && handlerLookupService != null && handler != null) {
                handlerLookupService.bind(message, handler);
            }
            return bind;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            SpongeGame.logger.error("Error registering codec [{}] for opcode [{}] under service key [{}]", codec, opcode, serviceKey);
            return null;
        }
    }

    default <M extends Message> Codec.CodecRegistration getCodecRegistration(String serviceKey, Class<M> clazz) {
        switch (serviceKey) {
            case INBOUND:
                return getCodecRegistration(clazz);
            case OUTBOUND:
                return getOutboundCodecLookupService().find(clazz);
            default:
                throw new RuntimeException("Invalid service key provided! Options available are [" + INBOUND + ", " + OUTBOUND + "].");
        }
    }

    @Override
    default <M extends Message> Codec.CodecRegistration getCodecRegistration(Class<M> message) {
        return getInboundCodecLookupService().find(message);
    }

    @Override
    default Codec<?> readHeader(ByteBuf buf) throws UnknownPacketException {
        int length = -1;
        int opcode = -1;
        try {
            length = ByteBufUtils.readVarInt(buf);
            buf.markReaderIndex();
            opcode = ByteBufUtils.readVarInt(buf);
            return getInboundCodecLookupService().find(opcode);
        } catch (IOException e) {
            throw new UnknownPacketException("Failed to read packet data (corrupt?)", opcode, length);
        } catch (IllegalOpcodeException e) {
            buf.resetReaderIndex();
            throw new UnknownPacketException("Packet header contains opcode unknown to this server!", opcode, length);
        }
    }

    @Override
    default ByteBuf writeHeader(ByteBuf out, Codec.CodecRegistration codec, ByteBuf data) {
        final int length = data.readableBytes();
        final ByteBuf opcodeBuffer = Unpooled.buffer();
        ByteBufUtils.writeVarInt(opcodeBuffer, codec.getOpcode());
        ByteBufUtils.writeVarInt(out, length + opcodeBuffer.readableBytes());
        ByteBufUtils.writeVarInt(out, codec.getOpcode());
        return out;
    }
}

