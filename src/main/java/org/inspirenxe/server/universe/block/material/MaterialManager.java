package org.inspirenxe.server.universe.block.material;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.inspirenxe.server.Game;

public class MaterialManager {
    private final Game game;
    private final Map<String, Material> materials = new ConcurrentHashMap<>();

    public MaterialManager(Game game) {
        this.game = game;
    }

    protected void register(Material material) {
        final Material previous = materials.put(material.getName(), material);
        if (previous != null) {
            game.getLogger().warn("New material has conflicting name, previous material was overwritten: " + previous + " => " + material);
        }
    }
}
