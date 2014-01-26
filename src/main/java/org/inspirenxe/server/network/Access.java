package org.inspirenxe.server.network;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.spout.cereal.config.ConfigurationException;
import org.spout.cereal.config.yaml.YamlConfiguration;

public class Access {
    private final Network network;
    private final YamlConfiguration configuration = new YamlConfiguration(Paths.get("config/access.yml").toFile());

    protected Access(Network network) {
        this.network = network;
    }

    public void load() {
        final Path accessPath = Paths.get("config/access.yml");
        if (Files.notExists(accessPath)) {
            try {
                Files.copy(getClass().getResourceAsStream("/config/access.yml"), accessPath);
            } catch (IOException e) {
                network.getGame().getLogger().fatal(e);
            }
        }
        try {
            configuration.load();
        } catch (ConfigurationException e) {
            network.getGame().getLogger().fatal(e);
        }
    }

    /**
     * @return true if the banlist is enabled, false if not
     */
    public boolean isBanlistEnabled() {
        return configuration.getChild("banlist.enabled").getBoolean();
    }

    /**
     * @return true if the whitelist is enabled, false if not.
     */
    public boolean isWhitelistEnabled() {
        return configuration.getChild("whitelist.enabled").getBoolean();
    }

    /**
     * @param enabled true to enable the banlist, false to disable it
     */
    public void setBanlistEnabled(boolean enabled) {
        configuration.getChild("banlist.enabled").setValue(boolean.class, enabled);
    }

    /**
     * @param enabled true to enable the whitelist, false to disable it.
     */
    public void setWhitelistEnabled(boolean enabled) {
        configuration.getChild("whitelist.enabled").setValue(boolean.class, enabled);
    }

    /**
     * @return the list of players in the banlist
     */
    public List<String> getBanlist() {
        return configuration.getChild("banlist.list").getStringList();
    }

    /**
     * @return the list of players in the whitelist
     */
    public List<String> getWhitelist() {
        return configuration.getChild("whitelist.list").getStringList();
    }

    /**
     * @param name the player name
     * @param add if true it will add the player to the banlist, otherwise it will remove them
     * @return if the player was successfully added or removed
     */
    public boolean ban(String name, boolean add) {
        if (add) {
            return getBanlist().add(name);
        } else {
            return getBanlist().remove(name);
        }
    }

    /**
     * @param name the player name
     * @param add if true it will add the player to the whitelist, otherwise it will remove them
     * @return if the player was successfully added or removed
     */
    public boolean whitelist(String name, boolean add) {
        if (add) {
            return getWhitelist().add(name);
        } else {
            return getWhitelist().remove(name);
        }
    }
}
