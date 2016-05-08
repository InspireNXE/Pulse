package org.inspirenxe.pulse.entity;

import java.util.UUID;

public class Entity {
    public static int lastId = -1;
    public final int id = ++lastId;
    public final UUID uniqueId;
    public double x, y, z, yaw, headYaw, pitch;

    public Entity() {
        this.uniqueId = UUID.randomUUID();
    }

    public Entity(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
}
