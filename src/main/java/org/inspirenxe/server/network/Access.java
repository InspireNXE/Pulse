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

import org.spout.cereal.config.ConfigurationException;
import org.spout.cereal.config.yaml.YamlConfiguration;

public final class Access {
    private static final Path CONFIG_PATH = Paths.get("config");
    private static final Path ACCESS_PATH = Paths.get(CONFIG_PATH.toString(), "access.yml");
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
    }

    /**
     * @return true if the banlist is enabled, false if not
     */
    public synchronized boolean isBanlistEnabled() {
        return CONFIGURATION.getChild(BANLIST_KEY).getChild(ENABLED_KEY).getBoolean();
    }

    /**
     * @return true if the whitelist is enabled, false if not.
     */
    public synchronized boolean isWhitelistEnabled() {
        return CONFIGURATION.getChild(WHITELIST_KEY).getChild(ENABLED_KEY).getBoolean();
    }

    /**
     * @param enabled true to enable the banlist, false to disable it
     */
    public synchronized void setBanlistEnabled(boolean enabled) {
        CONFIGURATION.getChild(BANLIST_KEY).getChild(ENABLED_KEY).setValue(boolean.class, enabled);
    }

    /**
     * @param enabled true to enable the whitelist, false to disable it.
     */
    public synchronized void setWhitelistEnabled(boolean enabled) {
        CONFIGURATION.getChild(WHITELIST_KEY).getChild(ENABLED_KEY).setValue(boolean.class, enabled);
    }

    /**
     * @return the list of players in the banlist
     */
    public synchronized List<String> getBanlist() {
        return Collections.unmodifiableList(CONFIGURATION.getChild(BANLIST_KEY).getChild(LIST_KEY).getStringList());
    }

    /**
     * @return the list of players in the whitelist
     */
    public synchronized List<String> getWhitelist() {
        return Collections.unmodifiableList(CONFIGURATION.getChild(WHITELIST_KEY).getChild(LIST_KEY).getStringList());
    }

    /**
     * @param list the list to save to the configuration
     */
    public synchronized void setBanlist(List<String> list) {
        CONFIGURATION.getChild(BANLIST_KEY).getChild(LIST_KEY).setValue(List.class, list);
    }

    /**
     * @param list the list to save to the configuration
     */
    public synchronized void setWhitelist(List<String> list) {
        CONFIGURATION.getChild(WHITELIST_KEY).getChild(LIST_KEY).setValue(List.class, list);
    }

    /**
     * @param name the player name
     * @param add if true it will add the player to the banlist, otherwise it will remove them
     * @return if the player was successfully added or removed
     */
    public synchronized boolean ban(String name, boolean add) {
        if (add) {
            return CONFIGURATION.getChild(BANLIST_KEY).getChild(LIST_KEY).getStringList().add(name);
        } else {
            return CONFIGURATION.getChild(BANLIST_KEY).getChild(LIST_KEY).getStringList().remove(name);
        }
    }

    /**
     * @param name the player name
     * @param add if true it will add the player to the whitelist, otherwise it will remove them
     * @return if the player was successfully added or removed
     */
    public synchronized boolean whitelist(String name, boolean add) {
        if (add) {
            return CONFIGURATION.getChild(WHITELIST_KEY).getChild(LIST_KEY).getStringList().add(name);
        } else {
            return CONFIGURATION.getChild(WHITELIST_KEY).getChild(LIST_KEY).getStringList().remove(name);
        }
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
