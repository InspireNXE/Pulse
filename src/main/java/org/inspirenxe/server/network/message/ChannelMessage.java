/**
 * This file is part of Pulse, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2014 InspireNXE <http://inspirenxe.org/>
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
package org.inspirenxe.server.network.message;

import com.flowpowered.networking.AsyncableMessage;
import org.inspirenxe.server.network.ServerSession;

public abstract class ChannelMessage implements AsyncableMessage {
    private final Channel[] channels;
    private ServerSession session;

    public ChannelMessage() {
        this.channels = new Channel[0];
    }

    public ChannelMessage(Channel[] channels) {
        this.channels = channels;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    public void setSession(ServerSession session) {
        if (this.session != null) {
            throw new IllegalArgumentException("Attempt made to set session twice on message!");
        }
        this.session = session;
    }

    public ServerSession getSession() {
        return session;
    }

    public Channel[] getChannels() {
        return channels;
    }

    /**
     * An enum of all the message channels.
     */
    public static enum Channel {
        UNIVERSE,
        INTERFACE,
        NETWORK,
        PHYSICS
    }
}


