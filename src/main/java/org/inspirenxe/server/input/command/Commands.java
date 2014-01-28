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
package org.inspirenxe.server.input.command;

import java.io.IOException;
import java.util.List;

import com.flowpowered.commands.CommandArguments;
import com.flowpowered.commands.CommandException;
import com.flowpowered.commands.CommandSender;
import com.flowpowered.commands.annotated.CommandDescription;
import com.flowpowered.commands.annotated.Permissible;
import org.inspirenxe.server.Game;
import org.inspirenxe.server.network.Access;
import org.omg.CORBA.Environment;

public class Commands {
    private final Game game;

    public Commands(Game game) {
        this.game = game;
    }

    @CommandDescription (name = "banlist", usage = "banlist <argument>", desc = "Manages the banlist", help = "Use this command to manage the banlist.")
    @Permissible ("game.command.banlist")
    private void onCommandBanlist(CommandSender sender, CommandArguments args) throws CommandException {
        if (args.remaining() < 1) {
            sender.sendMessage("Invalid arguments.");
            return;
        }
        final Access access = game.getNetwork().getAccess();
        switch (args.popString("ARGUMENT").toUpperCase()) {
            case "ADD":
                if (args.remaining() < 1) {
                    sender.sendMessage("Usage: banlist add <name>");
                    return;
                }
                final String addedName = args.popString("NAME");
                if (access.ban(addedName, true)) {
                    sender.sendMessage(addedName + " was temporarily added to the banlist.");
                } else {
                    sender.sendMessage(addedName + " is already on the banlist.");
                }
                break;
            case "LIST":
                sender.sendMessage("Current banlist: " + access.getBanlist().toString().replace("[", "").replace("]", ""));
                break;
            case "OFF":
                access.setBanlistEnabled(false);
                sender.sendMessage("Banlist has been temporarily turned off.");
                break;
            case "ON":
                access.setBanlistEnabled(true);
                sender.sendMessage("Banlist has been temporarily turned on.");
                break;
            case "REMOVE":
                if (args.remaining() < 1) {
                    sender.sendMessage("Usage: banlist remove <name>");
                    return;
                }
                final String removedName = args.popString("NAME");
                if (access.ban(removedName, false)) {
                    sender.sendMessage(removedName + " was temporarily removed from the banlist.");
                } else {
                    sender.sendMessage(removedName + " was not on the banlist.");
                }
                break;
            case "SAVE":
                access.save();
                sender.sendMessage("All temporary banlist settings have been saved.");
                break;
            default:
                sender.sendMessage("Invalid arguments.");
        }
    }

    @CommandDescription (name = "cls", usage = "cls", desc = "Clears the game console", help = "Use this command to clear the game console. This does not remove the text from the logs.")
    @Permissible ("game.command.cls")
    private void onCommandClear(CommandSender sender, CommandArguments args) throws CommandException {
        try {
            game.getInput().clear();
        } catch (IOException e) {
            throw new CommandException(e);
        }
    }

    @CommandDescription (name = "stop", usage = "stop", desc = "Stops the game", help = "Use this command only when you want to stop the game!")
    @Permissible ("game.command.stop")
    private void onCommandStop(CommandSender sender, CommandArguments args) throws CommandException {
        game.close();
    }

    @CommandDescription (name = "version", usage = "version", desc = "Displays the game version", help = "Use this command to display the game version.")
    @Permissible ("game.command.version")
    private void onCommandVersion(CommandSender sender, CommandArguments args) throws CommandException {
        sender.sendMessage("Running version " + game.getVersion());
    }

    @CommandDescription (name = "whitelist", usage = "whitelist <argument>", desc = "Manages the whitelist", help = "Use this command to manage the whitelist.")
    @Permissible ("game.command.whitelist")
    private void onCommandWhitelist(CommandSender sender, CommandArguments args) throws CommandException {
        if (args.remaining() < 1) {
            sender.sendMessage("Invalid arguments.");
            return;
        }
        final Access access = game.getNetwork().getAccess();
        switch (args.popString("ARGUMENT").toUpperCase()) {
            case "ADD":
                if (args.remaining() < 1) {
                    sender.sendMessage("Usage: whitelist add <name>");
                    return;
                }
                final String addedName = args.popString("NAME");
                if (access.whitelist(addedName, true)) {
                    sender.sendMessage(addedName + " was temporarily added to the whitelist.");
                } else {
                    sender.sendMessage(addedName + " is already on the whitelist.");
                }
                break;
            case "LIST":
                sender.sendMessage("Current whitelist: " + access.getWhitelist().toString().replace("[", "").replace("]", ""));
                break;
            case "OFF":
                access.setWhitelistEnabled(false);
                sender.sendMessage("Whitelist has been temporarily turned off.");
                break;
            case "ON":
                access.setWhitelistEnabled(true);
                sender.sendMessage("Whitelist has been temporarily turned on.");
                break;
            case "REMOVE":
                if (args.remaining() < 1) {
                    sender.sendMessage("Usage: whitelist remove <name>");
                    return;
                }
                final String removedName = args.popString("NAME");
                if (access.whitelist(removedName, false)) {
                    sender.sendMessage(removedName + " was temporarily removed from the whitelist.");
                } else {
                    sender.sendMessage(removedName + " was not on the whitelist.");
                }
                break;
            case "SAVE":
                access.save();
                sender.sendMessage("All temporary whitelist settings have been saved.");
                break;
            default:
                sender.sendMessage("Invalid arguments.");
        }
    }
}

