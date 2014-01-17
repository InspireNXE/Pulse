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

import com.flowpowered.commands.CommandException;
import com.flowpowered.commands.CommandManager;
import com.flowpowered.commands.CommandProvider;
import com.flowpowered.commands.annotated.AnnotatedCommandExecutorFactory;
import com.flowpowered.commons.console.CommandCallback;
import com.flowpowered.commons.ticking.TickingElement;
import jline.console.ConsoleReader;
import org.inspirenxe.server.Game;
import org.inspirenxe.server.input.command.Commands;
import org.inspirenxe.server.input.command.ConsoleCommandSender;

public class Input extends TickingElement {
    private static final int TPS = 20;
    private final Game game;
    private final ConsoleReader reader;
    private final ConsoleCommandSender sender;
    private final GameCommandCallback callback;

    public Input(Game game) {
        super("input", TPS);
        this.game = game;
        try {
            reader = new ConsoleReader(System.in, System.out);
        } catch (IOException e) {
            throw new RuntimeException("Exception caught creating the console reader!", e);
        }
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
        callback = new GameCommandCallback();
    }

    @Override
    public void onStart() {
        game.getLogger().info("Starting input");
    }

    @Override
    public void onTick(long l) {
        String command;
        try {
            command = reader.readLine();

            if (command == null || command.trim().length() == 0) {
                return;
            }
        } catch (IOException e) {
            game.getLogger().fatal("Failed to read console input!", e);
            return;
        }
        callback.handleCommand(command);
    }

    @Override
    public void onStop() {
        reader.shutdown();
        game.getLogger().info("Stopping input");
    }

    private class GameCommandCallback implements CommandCallback {
        @Override
        public void handleCommand(String command) {
            try {
                sender.processCommand(command);
            } catch (CommandException e) {
                game.getLogger().error("Exception caught handling command [" + command + "]!", e);
            }
        }
    }
}
