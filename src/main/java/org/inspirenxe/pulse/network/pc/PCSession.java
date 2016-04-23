package org.inspirenxe.pulse.network.pc;

import static com.google.common.base.Preconditions.checkNotNull;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.inspirenxe.pulse.SpongeGame;
import org.spacehq.packetlib.Server;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.event.session.ConnectedEvent;
import org.spacehq.packetlib.event.session.DisconnectedEvent;
import org.spacehq.packetlib.event.session.DisconnectingEvent;
import org.spacehq.packetlib.event.session.PacketReceivedEvent;
import org.spacehq.packetlib.event.session.PacketSentEvent;
import org.spacehq.packetlib.event.session.SessionEvent;
import org.spacehq.packetlib.event.session.SessionListener;
import org.spacehq.packetlib.packet.Packet;
import org.spacehq.packetlib.tcp.TcpPacketCompression;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class PCSession extends SimpleChannelInboundHandler<Packet> implements Session {

    private final Server server;

    private String host;
    private int port;
    private PCProtocol protocol;
    // TODO Concurrent?
    private Map<String, Object> flags = new HashMap<>();
    private List<SessionListener> listeners = new LinkedList<>();

    private Channel channel;
    private boolean disconnected = false;

    // Attributes
    private int compressionThreshold = -1;
    private int connectTimeout = 30;
    private int readTimeout = 30;
    private int writeTimeout = 0;

    // Ping
    private long lastPingTime = 0L;
    private long lastPingId = 0L;

    public PCSession(Server server, String host, int port, PCProtocol protocol) {
        this.server = server;
        this.host = host;
        this.port = port;
        this.protocol = protocol;
    }

    @Override
    public void connect() {
        this.connect(false);
    }

    @Override
    public void connect(boolean wait) {

    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public PCProtocol getPacketProtocol() {
        return this.protocol;
    }

    @Override
    public Map<String, Object> getFlags() {
        final Map<String, Object> flags = new HashMap<>();
        flags.putAll(this.server.getGlobalFlags());
        flags.putAll(this.flags);
        return flags;
    }

    @Override
    public boolean hasFlag(String flag) {
        checkNotNull(flag);
        return this.flags.containsKey(flag);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getFlag(String s) {
        checkNotNull(s);
        final Object obj = this.getFlags().get(s);
        if (obj == null) {
            return null;
        }

        try {
            return (T) obj;
        } catch (ClassCastException cce) {
            SpongeGame.logger.error("Attempted to get flag [{}] on session [{}] but types are mismatched!", cce);
        }
        return null;
    }

    @Override
    public void setFlag(String s, Object o) {
        this.flags.put(s, o);
    }

    @Override
    public List<SessionListener> getListeners() {
        return new ArrayList<>(listeners);
    }

    @Override
    public void addListener(SessionListener listener) {
        checkNotNull(listener);
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(SessionListener listener) {
        checkNotNull(listener);
        this.listeners.remove(listener);
    }

    @Override
    public void callEvent(SessionEvent event) {
        checkNotNull(event);
        for (SessionListener listener : this.listeners) {
            try {
                event.call(listener);
            } catch (Throwable t) {
                this.exceptionCaught(null, t);
            }
        }
    }

    @Override
    public int getCompressionThreshold() {
        return this.compressionThreshold;
    }

    @Override
    public void setCompressionThreshold(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
        if (this.channel != null) {
            if (this.compressionThreshold >= 0) {
                if (this.channel.pipeline().get("compression") == null) {
                    this.channel.pipeline().addBefore("codec", "compression", new TcpPacketCompression(this));
                }
            } else if (this.channel.pipeline().get("compression") != null) {
                this.channel.pipeline().remove("compression");
            }
        }
    }

    @Override
    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    @Override
    public void setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
    }

    @Override
    public int getReadTimeout() {
        return this.readTimeout;
    }

    @Override
    public void setReadTimeout(int timeout) {
        this.readTimeout = timeout;
        this.refreshReadTimeoutHandler();
    }

    @Override
    public int getWriteTimeout() {
        return this.writeTimeout;
    }

    @Override
    public void setWriteTimeout(int timeout) {
        this.writeTimeout = timeout;
        this.refreshWriteTimeoutHandler();
    }

    @Override
    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen() && !this.disconnected;
    }

    @Override
    public void send(Packet packet) {
        if (this.channel != null) {
            final ChannelFuture future = this.channel.writeAndFlush(packet).addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        PCSession.this.callEvent(new PacketSentEvent(PCSession.this, packet));
                    } else {
                        PCSession.this.exceptionCaught(null, future.cause());
                    }

                }
            });
            if (packet.isPriority()) {
                try {
                    future.await();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    @Override
    public void disconnect(String reason) {
        this.disconnect(reason, false);
    }

    @Override
    public void disconnect(String reason, boolean wait) {
        this.disconnect(reason, null, wait);
    }

    @Override
    public void disconnect(String reason, Throwable cause) {
        this.disconnect(reason, cause, false);
    }

    @Override
    public void disconnect(String reason, Throwable cause, boolean wait) {
        if (!disconnected) {
            disconnected = true;

            if (cause != null) {
                SpongeGame.logger.error("Exception caught for session [{}]: {}", this, reason, cause);
            }


            if (this.channel != null && this.channel.isOpen()) {
                this.callEvent(new DisconnectingEvent(this, reason, cause));
                final ChannelFuture future = this.channel.flush().close().addListener(
                        future1 -> PCSession.this.callEvent(new DisconnectedEvent(PCSession.this, reason != null ? reason :
                                "Connection closed.", cause)));
                if (wait) {
                    try {
                        future.await();
                    } catch (InterruptedException ignored) {
                    }
                }
            } else {
                this.callEvent(new DisconnectedEvent(this, reason != null ? reason : "Connection closed.", cause));
            }

            this.channel = null;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!this.disconnected && this.channel == null) {
            this.channel = ctx.channel();
            this.server.addSession(this);
            this.callEvent(new ConnectedEvent(this));
        } else {
            ctx.channel().close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel() == this.channel) {
            this.server.removeSession(this);
            this.disconnect("Connection closed.");
        }

    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet packet) {
        if (!packet.isPriority()) {
            try {
                this.callEvent(new PacketReceivedEvent(this, packet));
            } catch (Throwable t) {
                this.exceptionCaught(ctx, t);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String message;

        if (!(cause instanceof ConnectTimeoutException) && (!(cause instanceof ConnectException) || !cause.getMessage()
                .contains("connection timed out"))) {
            if (cause instanceof ReadTimeoutException) {
                message = "Read timed out.";
            } else if (cause instanceof WriteTimeoutException) {
                message = "Write timed out.";
            } else {
                message = "Internal network exception.";
            }
        } else {
            message = "Connection timed out.";
        }

        this.disconnect(message, cause);
    }

    private void refreshReadTimeoutHandler() {
        this.refreshReadTimeoutHandler(this.channel);
    }

    public void refreshReadTimeoutHandler(Channel channel) {
        if (channel != null) {
            if (this.readTimeout <= 0) {
                if (channel.pipeline().get("readTimeout") != null) {
                    channel.pipeline().remove("readTimeout");
                }
            } else if (channel.pipeline().get("readTimeout") == null) {
                channel.pipeline().addFirst("readTimeout", new ReadTimeoutHandler(this.readTimeout));
            } else {
                channel.pipeline().replace("readTimeout", "readTimeout", new ReadTimeoutHandler(this.readTimeout));
            }
        }

    }

    private void refreshWriteTimeoutHandler() {
        this.refreshWriteTimeoutHandler(this.channel);
    }

    public void refreshWriteTimeoutHandler(Channel channel) {
        if (channel != null) {
            if (this.writeTimeout <= 0) {
                if (channel.pipeline().get("writeTimeout") != null) {
                    channel.pipeline().remove("writeTimeout");
                }
            } else if (channel.pipeline().get("writeTimeout") == null) {
                channel.pipeline().addFirst("writeTimeout", new WriteTimeoutHandler(this.writeTimeout));
            } else {
                channel.pipeline().replace("writeTimeout", "writeTimeout", new WriteTimeoutHandler(this.writeTimeout));
            }
        }
    }

    public long getLastPingId() {
        return this.lastPingId;
    }

    public void setLastPingId(long lastPingId) {
        this.lastPingId = lastPingId;
    }

    public long getLastPingTime() {
        return this.lastPingTime;
    }

    public void setLastPingTime(long lastPingTime) {
        this.lastPingTime = lastPingTime;
    }
}
