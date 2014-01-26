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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import static java.util.Arrays.asList;

public class Main {
    public static void main(String[] args) throws Exception {
        deploy();
        final Configuration configuration = new Configuration(Paths.get("config/settings.yml"));
        configuration.load();
        parseArgs(args, configuration);
        final Game game = new Game(configuration);
        game.open();
    }

    public static void deploy() throws Exception {
        final Path configPath = Paths.get("config/settings.yml");
        if (Files.notExists(configPath)) {
            Files.copy(Main.class.getResourceAsStream("/config/settings.yml"), configPath);
        }
        final Path worldsPath = Paths.get("worlds");
        if (Files.notExists(worldsPath)) {
            Files.createDirectories(worldsPath);
        }
    }

    public static void parseArgs(String[] args, Configuration configuration) throws Exception {
        final OptionParser parser = new OptionParser() {
            {
                acceptsAll(asList("n", "name"))
                        .withOptionalArg()
                        .ofType(String.class);
                acceptsAll(asList("a", "address"))
                        .withOptionalArg()
                        .ofType(String.class);
                acceptsAll(asList("p", "port"))
                        .withOptionalArg()
                        .ofType(Integer.class);
            }
        };

        // Set all configuration options for this game
        final OptionSet options = parser.parse(args);
        if (options.has("n")) { // Name flag
            configuration.setName(options.valueOf("n").toString());
        }
        if (options.has("a")) { // Address flag
            configuration.setAddress(options.valueOf("a").toString());
        }
        if (options.has("p")) { // Port flag
            configuration.setPort(Integer.parseInt(options.valueOf("p").toString()));
        }
    }
}
