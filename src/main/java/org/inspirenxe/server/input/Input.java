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
package org.inspirenxe.server.input;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.flowpowered.commands.CommandException;
import com.flowpowered.commands.CommandManager;
import com.flowpowered.commands.CommandProvider;
import com.flowpowered.commands.annotated.AnnotatedCommandExecutorFactory;
import com.flowpowered.commons.ticking.TickingElement;
import com.github.wolf480pl.jline_log4j2_appender.ConsoleSetupMessage;
import jline.console.ConsoleReader;
import org.inspirenxe.server.Game;
import org.inspirenxe.server.input.command.Commands;
import org.inspirenxe.server.input.command.ConsoleCommandSender;

public class Input extends TickingElement {
    private static final ConsoleReaderThread readerThread = new ConsoleReaderThread();
    private static final int TPS = 5;
    private final Game game;
    private final ConsoleCommandSender sender;

    public Input(Game game) {
        super("input", TPS);
        this.game = game;
        final CommandManager manager = new CommandManager(false);
        final CommandProvider provider = new CommandProvider() {
            @Override
            public String getName() {
                return "server";
            }
        };
        manager.setRootCommand(manager.getCommand(provider, "root"));
        sender = new ConsoleCommandSender(game, manager);
        new AnnotatedCommandExecutorFactory(manager, provider).create(new Commands(game));
    }

    @Override
    public void onStart() {
        game.getLogger().info("Starting input");
        if (!readerThread.isAlive()) {
            if (!readerThread.ranBefore) {
                game.getLogger().info(new ConsoleSetupMessage(readerThread.reader, "Setting up console"));
            }
            readerThread.start();
        } else {
            readerThread.getRawCommandQueue().clear();
        }
    }

    @Override
    public void onTick(long l) {
        final Iterator<String> iterator = readerThread.getRawCommandQueue().iterator();
        while (iterator.hasNext()) {
            final String command = iterator.next();
            try {
                sender.processCommand(command);
            } catch (CommandException e) {
                game.getLogger().error("Exception caught processing command [" + command + "]", e);
            }
            iterator.remove();
        }
    }

    @Override
    public void onStop() {
        game.getLogger().info("Stopping input");
    }

    public Game getGame() {
        return game;
    }

    public void clear() throws IOException {
        readerThread.getConsole().clearScreen();
    }

    private static class ConsoleReaderThread extends Thread {
        private volatile boolean running = false;
        private volatile boolean ranBefore = false;
        private final ConsoleReader reader;
        private final ConcurrentLinkedQueue<String> rawCommandQueue = new ConcurrentLinkedQueue<>();

        public ConsoleReaderThread() {
            super("command");
            setDaemon(true);

            try {
                reader = new ConsoleReader(System.in, System.out);
                reader.setBellEnabled(false);
                reader.setExpandEvents(false);
            } catch (Exception e) {
                throw new RuntimeException("Exception caught creating the console reader!", e);
            }
        }

        @Override
        public void run() {
            ranBefore = true;
            running = true;
            try {
                while (running) {
                    String command = reader.readLine(">");

                    if (command == null || command.trim().length() == 0) {
                        continue;
                    }

                    rawCommandQueue.offer(command);
                }
            } catch (IOException e) {
                reader.shutdown();
            }
        }

        public ConsoleReader getConsole() {
            return reader;
        }

        public ConcurrentLinkedQueue<String> getRawCommandQueue() {
            return rawCommandQueue;
        }
    }
}
