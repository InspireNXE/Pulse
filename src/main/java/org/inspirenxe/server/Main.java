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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.GsonBuilder;
import org.inspirenxe.server.util.ReflectionUtil;

public class Main {
    @Parameter (names = {"-name", "--name", "-n", "--n"}, description = "Specify the name to use")
    private static String NAME;
    @Parameter (names = {"-address", "--address", "-a", "--a"}, description = "Specify the address to use")
    private static String ADDRESS;
    @Parameter (names = {"-port", "--port", "-p", "--p"}, description = "Specify the port to use")
    private static Integer PORT;

    public static void main(String[] args) throws Exception {
        new JCommander(new Main()).parse(args);
        deploy();
        final Configuration configuration;
        try (final Reader reader = new InputStreamReader(Files.newInputStream(Paths.get("config.json")))) {
            configuration = new GsonBuilder().create().fromJson(reader, Configuration.class);
        }
        reflectSetParams(configuration);
        final Game game = new Game(configuration);
        game.open();
    }

    public static void deploy() throws IOException {
        final Path configPath = Paths.get("config.json");
        if (Files.notExists(configPath)) {
            Files.copy(Main.class.getResourceAsStream("/config.json"), configPath);
        }
        final Path worldsPath = Paths.get("worlds");
        if (Files.notExists(worldsPath)) {
            Files.createDirectories(worldsPath);
        }
    }

    public static void reflectSetParams(Configuration configuration) throws Exception {
        if (NAME != null && !NAME.isEmpty()) {
            ReflectionUtil.setFinal(Configuration.class, "name", configuration, NAME);
        }
        if (ADDRESS != null && !ADDRESS.isEmpty()) {
            ReflectionUtil.setFinal(Configuration.class, "address", configuration, ADDRESS);
        }
        if (PORT != null && PORT > 0) {
            ReflectionUtil.setFinal(Configuration.class, "port", configuration, PORT);
        }
    }
}