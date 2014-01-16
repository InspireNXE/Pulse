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
package org.inspirenxe.server.nterface;

import java.util.Collections;

import com.flowpowered.commands.CommandException;
import com.flowpowered.commands.CommandManager;
import com.flowpowered.commands.CommandProvider;
import com.flowpowered.commands.annotated.AnnotatedCommandExecutorFactory;
import com.flowpowered.commons.console.CommandCallback;
import com.flowpowered.commons.console.JLineConsole;
import com.flowpowered.commons.ticking.TickingElement;
import jline.console.completer.Completer;
import org.inspirenxe.server.Game;
import org.inspirenxe.server.nterface.command.ConsoleCommandSender;
import org.inspirenxe.server.nterface.command.ServerCommands;

public class Interface extends TickingElement {
    private static final int TPS = 1;
    private final Game game;
    private JLineConsole console;
    private final CommandManager manager;
    private final AnnotatedCommandExecutorFactory factory;
    private final ConsoleCommandSender sender;

    public Interface(Game game) {
        super("interface", TPS);
        this.game = game;
        manager = new CommandManager();
        factory = new AnnotatedCommandExecutorFactory(manager, new ServerCommandProvider());
        sender = new ConsoleCommandSender(game, manager);
    }

    @Override
    public void onStart() {
        factory.create(ServerCommands.class);
        final CommandCallback callback = new CommandCallback() {
            @Override
            public void handleCommand(String s) {
                try {
                    manager.executeCommand(sender, s);
                } catch (CommandException e) {
                    game.getLogger().info("Exception caught executing command [" + s + "]", e);
                }
            }
        };
        console = new JLineConsole(callback, Collections.<Completer>emptyList());
        try {
            console.getCommandThread().join();
        } catch (InterruptedException e) {
            game.getLogger().info("Could not create console!", e);
            stop();
        }
        game.getLogger().info("Starting interface");
    }

    @Override
    public void onTick(long l) {

    }

    @Override
    public void onStop() {
        game.getLogger().info("Stopping interface");
    }
}

class ServerCommandProvider implements CommandProvider {
    @Override
    public String getName() {
        return "server";
    }
}
