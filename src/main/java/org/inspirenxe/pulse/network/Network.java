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

import com.flowpowered.network.BasicChannelInitializer;
import com.flowpowered.network.ConnectionManager;
import com.flowpowered.network.session.Session;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.inspirenxe.pulse.SpongeGame;
import org.inspirenxe.pulse.util.TickingElement;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class Network extends TickingElement implements ConnectionManager {
    private static final int TPS = 20;

    private final Set<MinecraftSession> sessions = new HashSet<>();
    private final ServerBootstrap bootstrap = new ServerBootstrap();
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private InetSocketAddress boundAddress;

    public Network() {
        super("network", TPS);
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new BasicChannelInitializer(this))
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public void onStart() {
        SpongeGame.logger.info("Starting network");
        boundAddress = new InetSocketAddress("localhost", 25565);
        bootstrap.bind(boundAddress).addListener(future -> {
            if (future.isSuccess()) {
                SpongeGame.logger.info("Listening on " + boundAddress);
            } else {
                SpongeGame.logger.error("Failed to bound server to " + boundAddress + "!");
            }
        });
    }

    @Override
    public void onTick(long l) {
        sessions.forEach(MinecraftSession::pulse);
    }

    @Override
    public void onStop() {
        SpongeGame.logger.info("Stopping network");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    @Override
    public Session newSession(Channel c) {
        final MinecraftSession session = new MinecraftSession(c);
        sessions.add(session);
        return session;
    }

    @Override
    public void sessionInactivated(Session session) {
        sessions.remove(session);
    }

    @Override
    public void shutdown() {
        throw new RuntimeException("Network thread should not be stopped via shutdown!");
    }
}

