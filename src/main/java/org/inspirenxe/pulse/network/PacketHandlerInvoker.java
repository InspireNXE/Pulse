package org.inspirenxe.pulse.network;

import org.inspirenxe.pulse.SpongeGame;
import org.inspirenxe.pulse.network.pc.PCSession;
import org.spacehq.packetlib.packet.Packet;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public final class PacketHandlerInvoker {
    private Object owner;
    private Map<Class<? extends Packet>, Method> methodsByPacket = new HashMap<>();

    public PacketHandlerInvoker(Object owner) {
        this.owner = owner;
        for (Method method : owner.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(PacketHandler.class)) {
                if (Modifier.isPrivate(method.getModifiers())) {
                    SpongeGame.logger.error("Failed to register PacketHandler [{}] as it is not public.", method.getName());
                    continue;
                }
                if (Modifier.isStatic(method.getModifiers())) {
                    SpongeGame.logger.error("Failed to register PacketHandler [{}] as it is static.", method.getName());
                    continue;
                }
                if (method.getParameterCount() == 2) {
                    final Class<?> sessionParamClass = method.getParameterTypes()[0];
                    if (!PCSession.class.isAssignableFrom(sessionParamClass)) {
                        SpongeGame.logger.error("Failed to register PacketHandler method [{}] as the first parameter [{}] is not a PCSession"
                                + ".", method.getName(), sessionParamClass);
                        continue;
                    }

                    final Class<?> packetParamClass = method.getParameterTypes()[1];
                    if (!Packet.class.isAssignableFrom(packetParamClass)) {
                        SpongeGame.logger.error("Failed to register PacketHandler method [{}] as the second parameter [{}] is not a Packet"
                                + ".", method.getName(), packetParamClass);
                        continue;
                    }

                    methodsByPacket.put((Class<? extends Packet>) packetParamClass, method);
                }
            }
        }
    }

    public void handle(PCSession session, Packet packet) {
        final Method method = methodsByPacket.get(packet.getClass());
        if (method != null) {
            try {
                method.invoke(owner, session, packet);
            } catch (Exception ex) {
                SpongeGame.logger.error("Error caught while invoking packet handler [{}] in class [{}]", method.getName(), owner.getClass(), ex);
            }
        }
    }
}
