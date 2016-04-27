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
package org.inspirenxe.pulse;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Configuration {

    private final String address;
    private final int port, tickRate, maxSessions;
    private final Path configPath, savesPath;
    private final boolean authenticateSessions;
    private final long seed;
    private final String motd;

    public Configuration(CommentedConfigurationNode node) {
        this.address = node.getNode("listener", "pc", "address").getString("0.0.0.0");
        this.port = node.getNode("listener", "pc", "port").getInt(25565);
        this.configPath = Paths.get(node.getNode("config-path").getString("config"));
        this.savesPath = Paths.get(node.getNode("saves-path").getString("saves"));
        this.authenticateSessions = node.getNode("authenticate-sessions").getBoolean(true);
        final ConfigurationNode seedNode = node.getNode("seed");
        if (seedNode.isVirtual()) {
            this.seed = System.currentTimeMillis();
        } else if (seedNode.getString().isEmpty()) {
            this.seed = System.currentTimeMillis();
        } else {
            this.seed = seedNode.getString().hashCode();
        }
        this.tickRate = node.getNode("tick-rate").getInt(20);
        this.maxSessions = node.getNode("max-sessions").getInt(20);
        this.motd = node.getNode("motd").getString("A Pulse Server");
    }

    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    public Path getConfigPath() {
        return this.configPath;
    }

    public Path getSavesPath() {
        return this.savesPath;
    }

    public boolean isAuthenticateSessions() {
        return this.authenticateSessions;
    }

    public long getSeed() {
        return this.seed;
    }

    public int getTickRate() {
        return this.tickRate;
    }

    public int getMaxSessions() {
        return this.maxSessions;
    }

    public String getMotd() {
        return motd;
    }
}
