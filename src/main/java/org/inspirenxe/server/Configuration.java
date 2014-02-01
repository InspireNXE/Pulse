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
package org.inspirenxe.server;

import java.nio.file.Path;

import com.flowpowered.cerealization.config.yaml.YamlConfiguration;

public class Configuration extends YamlConfiguration {
    private static final String ADDRESS_KEY = "address";
    private static final String NAME_KEY = "name";
    private static final String PORT_KEY = "port";

    public Configuration(Path configPath) {
        super(configPath.toFile());
    }

    public String getName() {
        return getChild(NAME_KEY).getString();
    }

    protected Configuration setName(String name) {
        getChild(NAME_KEY, true).setValue(String.class, name);
        return this;
    }

    public String getAddress() {
        return getChild(ADDRESS_KEY).getString();
    }

    protected Configuration setAddress(String address) {
        getChild(ADDRESS_KEY, true).setValue(String.class, address);
        return this;
    }

    public int getPort() {
        return getChild(PORT_KEY).getInt();
    }

    protected Configuration setPort(int port) {
        getChild(PORT_KEY, true).setValue(int.class, port);
        return this;
    }
}

