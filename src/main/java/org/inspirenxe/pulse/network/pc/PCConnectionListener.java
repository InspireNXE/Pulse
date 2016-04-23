package org.inspirenxe.pulse.network.pc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import org.inspirenxe.pulse.SpongeGame;
import org.spacehq.packetlib.ConnectionListener;
import org.spacehq.packetlib.Server;
import org.spacehq.packetlib.tcp.TcpPacketCodec;
import org.spacehq.packetlib.tcp.TcpPacketEncryptor;
import org.spacehq.packetlib.tcp.TcpPacketSizer;

import java.net.InetSocketAddress;

public final class PCConnectionListener implements ConnectionListener {

    private final Server server;
    private final String host;
    private final int port;
    private Channel channel;
    private EventLoopGroup group;

    public PCConnectionListener(Server server, String host, int port) {
        this.server = server;
        this.host = host;
        this.port = port;
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
    public boolean isListening() {
        return this.channel != null && this.channel.isOpen();
    }

    @Override
    public void bind() {
        this.bind(true);
    }

    @Override
    public void bind(boolean wait) {
        this.bind(wait, null);
    }

    @Override
    public void bind(boolean wait, Runnable callback) {
        if (this.group == null && this.channel == null) {
            this.group = new NioEventLoopGroup();
            ChannelFuture
                    future = (new ServerBootstrap()).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        public void initChannel(Channel channel) throws Exception {
                            final InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
                            final PCProtocol protocol = (PCProtocol) PCConnectionListener.this.server.createPacketProtocol();
                            final PCSession session = new PCSession(PCConnectionListener.this.server, address.getHostName(), address
                                    .getPort(), protocol);
                            session.getPacketProtocol().newServerSession(PCConnectionListener.this.server, session);
                            channel.config().setOption(ChannelOption.IP_TOS, 24);
                            channel.config().setOption(ChannelOption.TCP_NODELAY, false);

                            final ChannelPipeline pipeline = channel.pipeline();
                            session.refreshReadTimeoutHandler(channel);
                            session.refreshWriteTimeoutHandler(channel);
                            pipeline.addLast("encryption", new TcpPacketEncryptor(session));
                            pipeline.addLast("sizer", new TcpPacketSizer(session));
                            pipeline.addLast("codec", new TcpPacketCodec(session));
                            pipeline.addLast("manager", session);
                        }
                    }).group(this.group).localAddress(this.host, this.port).bind();
            if (wait) {
                try {
                    future.sync();
                } catch (InterruptedException ignored) {
                }

                this.channel = future.channel();
                if (callback != null) {
                    callback.run();
                }
            } else {
                future.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            PCConnectionListener.this.channel = future.channel();
                            if (callback != null) {
                                callback.run();
                            }
                        } else {
                            SpongeGame.logger.error("Failed to asynchronously bind connection listener!", future.cause());
                        }
                    }
                });
            }
        }
    }

    @Override
    public void close() {
        this.close(false);
    }

    @Override
    public void close(boolean wait) {
        this.close(false, null);
    }

    @Override
    public void close(boolean wait, Runnable callback) {
        if (this.channel != null) {
            if (this.channel.isOpen()) {
                final ChannelFuture future = this.channel.close();
                if (wait) {
                    try {
                        future.sync();
                    } catch (InterruptedException ignored) {
                    }

                    if (callback != null) {
                        callback.run();
                    }
                } else {
                    future.addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (future.isSuccess()) {
                                if (callback != null) {
                                    callback.run();
                                }
                            } else {
                                SpongeGame.logger.error("Failed to asynchronously close connection listener!", future.cause());
                            }

                        }
                    });
                }
            }

            this.channel = null;
        }

        if (this.group != null) {
            final Future<?> future1 = this.group.shutdownGracefully();
            if (wait) {
                try {
                    future1.sync();
                } catch (InterruptedException ignored) {}
            } else {
                future1.addListener(future -> {
                    if (!future.isSuccess()) {
                        SpongeGame.logger.error("Failed to asynchronously close connection listener!", future.cause());
                    }

                });
            }

            this.group = null;
        }
    }
}
