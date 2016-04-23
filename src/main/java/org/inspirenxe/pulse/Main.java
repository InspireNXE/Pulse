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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.io.IoBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final Path CONFIG_PATH = Paths.get("config");
    private static final Path SETTINGS_PATH = Paths.get(CONFIG_PATH.toString(), "settings.conf");
    private static final Path WORLDS_PATH = Paths.get("saves");

    public static void main(String[] args) throws Exception {
        System.setOut(IoBuilder.forLogger("STDOUT").setLevel(Level.INFO).buildPrintStream());
        System.setErr(IoBuilder.forLogger("STDERR").setLevel(Level.ERROR).buildPrintStream());

        deploy();
        final SpongeGame game = new SpongeGame();
        game.launch();
    }

    public static void deploy() throws Exception {
        if (Files.notExists(CONFIG_PATH)) {
            Files.createDirectories(CONFIG_PATH);
        }
        if (Files.notExists(SETTINGS_PATH)) {
            Files.copy(Main.class.getResourceAsStream("/config/settings.conf"), SETTINGS_PATH);
        }
        if (Files.notExists(WORLDS_PATH)) {
            Files.createDirectories(WORLDS_PATH);
        }
    }
}
