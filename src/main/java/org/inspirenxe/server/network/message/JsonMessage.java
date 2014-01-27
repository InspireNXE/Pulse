package org.inspirenxe.server.network.message;

import com.google.gson.JsonObject;
import org.inspirenxe.server.network.ChannelMessage;

public abstract class JsonMessage extends ChannelMessage {
    private final JsonObject message;

    public JsonMessage(String property, String raw) {
        message = new JsonObject();
        message.addProperty(property, raw);
    }

    public JsonObject getJson() {
        return message;
    }

    public String getJsonString() {
        return message.toString();
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "message=" + message +
                '}';
    }
}
