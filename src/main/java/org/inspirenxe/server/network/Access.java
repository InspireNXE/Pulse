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
package org.inspirenxe.server.network;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.inspirenxe.server.Game;

import com.flowpowered.cerealization.config.ConfigurationException;
import com.flowpowered.cerealization.config.yaml.YamlConfiguration;

public final class Access {
    private static final Path CONFIG_PATH = Paths.get("config");
    private static final Path ACCESS_PATH = Paths.get(CONFIG_PATH.toString(), "access.yml");
    private static List<String> BANLIST;
    private static List<String> WHITELIST;
    private static final String BANLIST_KEY = "banlist";
    private static final String ENABLED_KEY = "enabled";
    private static final String LIST_KEY = "list";
    private static final String WHITELIST_KEY = "whitelist";
    private static final YamlConfiguration CONFIGURATION = new YamlConfiguration(ACCESS_PATH.toFile());
    private final Game game;

    protected Access(Game game) {
        this.game = game;
    }

    protected synchronized void load() {
        try {
            if (Files.notExists(CONFIG_PATH)) {
                Files.createDirectories(CONFIG_PATH);
            }
            if (Files.notExists(ACCESS_PATH)) {
                Files.copy(getClass().getResourceAsStream("/config/access.yml"), ACCESS_PATH);
            }
            CONFIGURATION.load();
        } catch (IOException | ConfigurationException e) {
            game.getLogger().fatal(e);
        }
        BANLIST = CONFIGURATION.getChild(BANLIST_KEY).getChild(LIST_KEY).getStringList();
        WHITELIST = CONFIGURATION.getChild(WHITELIST_KEY).getChild(LIST_KEY).getStringList();
    }

    /**
     * Checks if the banlist is enabled.
     *
     * @return true if the banlist is enabled, false if not.
     */
    public synchronized boolean isBanlistEnabled() {
        return CONFIGURATION.getChild(BANLIST_KEY).getChild(ENABLED_KEY).getBoolean();
    }

    /**
     * Checks if the whitelist is enabled.
     *
     * @return true if the whitelist is enabled, false if not.
     */
    public synchronized boolean isWhitelistEnabled() {
        return CONFIGURATION.getChild(WHITELIST_KEY).getChild(ENABLED_KEY).getBoolean();
    }

    /**
     * Sets the enabled status of the banlist.
     *
     * @param enabled True to enable the banlist, false to disable it.
     */
    public synchronized void setBanlistEnabled(boolean enabled) {
        CONFIGURATION.getChild(BANLIST_KEY).getChild(ENABLED_KEY).setValue(boolean.class, enabled);
    }

    /**
     * Sets the enabled status of the whitelist.
     *
     * @param enabled True to enable the whitelist, false to disable it.
     */
    public synchronized void setWhitelistEnabled(boolean enabled) {
        CONFIGURATION.getChild(WHITELIST_KEY).getChild(ENABLED_KEY).setValue(boolean.class, enabled);
    }

    /**
     * Gets the current banlist.
     *
     * @return The list of players in the banlist.
     */
    public synchronized List<String> getBanlist() {
        return Collections.unmodifiableList(BANLIST);
    }

    /**
     * Gets the current whitelist.
     *
     * @return The list of players in the whitelist.
     */
    public synchronized List<String> getWhitelist() {
        return Collections.unmodifiableList(WHITELIST);
    }

    /**
     * Sets the current banlist list.
     *
     * @param list The list to save to the configuration.
     */
    public synchronized void setBanlist(List<String> list) {
        CONFIGURATION.getChild(BANLIST_KEY).getChild(LIST_KEY).setValue(List.class, list);
    }

    /**
     * Sets the current whitelist list.
     *
     * @param list The list to save to the configuration.
     */
    public synchronized void setWhitelist(List<String> list) {
        CONFIGURATION.getChild(WHITELIST_KEY).getChild(LIST_KEY).setValue(List.class, list);
    }

    /**
     * Checks if a name is in the banlist.
     *
     * @param name The name to check.
     * @return True if banned, false if not.
     */
    public synchronized boolean isBanned(String name) {
        for (String entry : BANLIST) {
            if (entry.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a name is in the whitelist.
     *
     * @param name The name to check.
     * @return True if whitelisted, false if not.
     */
    public synchronized boolean isWhitelisted(String name) {
        for (String entry : WHITELIST) {
            if (entry.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clears the banlist
     */
    public synchronized void clearBanlist() {
        BANLIST.clear();
    }

    /**
     * Clears the whitelist
     */
    public synchronized void clearWhitelist() {
        WHITELIST.clear();
    }

    /**
     * Adds or removes a player to/from the banlist.
     *
     * @param name The player name.
     * @param add If true it will add the player to the banlist, otherwise it will remove them.
     * @return If the player was successfully added or removed.
     */
    public synchronized boolean ban(String name, boolean add) {
        if (add) {
            if (!isBanned(name) && BANLIST.add(name)) {
                setBanlist(BANLIST);
                return true;
            }
        } else {
            if (BANLIST.remove(name)) {
                setBanlist(BANLIST);
                return true;
            }
        }
        return false;
    }

    /**
     * Adds or removes a player to/from the whitelist.
     *
     * @param name The player name.
     * @param add If true it will add the player to the whitelist, otherwise it will remove them.
     * @return If the player was successfully added or removed.
     */
    public synchronized boolean whitelist(String name, boolean add) {
        if (add) {
            if (!isWhitelisted(name) && WHITELIST.add(name)) {
                setWhitelist(WHITELIST);
                return true;
            }
        } else {
            if (WHITELIST.remove(name)) {
                setWhitelist(WHITELIST);
                return true;
            }
        }
        return false;
    }

    /**
     * Saves the configuration object to file
     */
    public synchronized void save() {
        try {
            CONFIGURATION.save();
        } catch (ConfigurationException e) {
            game.getLogger().fatal(e);
        }
    }
}
