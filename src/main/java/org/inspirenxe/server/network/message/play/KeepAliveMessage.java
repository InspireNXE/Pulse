package org.inspirenxe.server.network.message.play;

import com.flowpowered.networking.Message;

public class KeepAliveMessage implements Message {
    private final int id;

    public KeepAliveMessage(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public String toString() {
        return "KeepAliveMessage{" +
                "id=" + id +
                '}';
    }
}
