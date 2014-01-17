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
import java.util.concurrent.atomic.AtomicReference;

import com.flowpowered.commands.CommandException;
import com.flowpowered.commands.CommandManager;
import com.flowpowered.commands.CommandProvider;
import com.flowpowered.commands.annotated.AnnotatedCommandExecutorFactory;
import com.flowpowered.commons.console.CommandCallback;
import jline.console.ConsoleReader;
import org.inspirenxe.server.nterface.Interface;
import org.inspirenxe.server.nterface.command.ConsoleCommandSender;
import org.inspirenxe.server.nterface.command.ServerCommands;

public class Console implements CommandCallback {
    private final Game game;
    private final AtomicReference<ConsoleReader> reader;
    private final CommandManager manager;
    private final ConsoleCommandSender sender;

    public Console(Game game) throws IOException {
        this.game = game;
        reader = new AtomicReference<>(new ConsoleReader(System.in, System.out));
        manager = new CommandManager();
        sender = new ConsoleCommandSender(game, manager);
        final AnnotatedCommandExecutorFactory factory = new AnnotatedCommandExecutorFactory(manager, new CommandProvider() {
            @Override
            public String getName() {
                return "server";
            }
        });
        factory.create(ServerCommands.class);
    }

    public AtomicReference<ConsoleReader> getReader() {
        return reader;
    }

    protected void acceptInput() {
        final Interface nterface = new Interface(game);
        nterface.start();
    }

    //TODO Command handling on another thread besides main?
    @Override
    public void handleCommand(String command) {
        game.getLogger().info(command);
        try {
            manager.executeCommand(sender, command);
        } catch (CommandException e) {
            game.getLogger().error("Exception caught processing command [" + command + "]", e);
        }
    }
}