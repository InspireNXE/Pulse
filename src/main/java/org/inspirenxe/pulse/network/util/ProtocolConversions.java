package org.inspirenxe.pulse.network.util;

import org.spacehq.mc.protocol.data.message.Message;
import org.spacehq.mc.protocol.data.message.TextMessage;
import org.spongepowered.api.text.Text;

public final class ProtocolConversions {

    public static Message fromText(Text text) {
        return new TextMessage(text.toPlain());
    }

    private ProtocolConversions() {}
}
