package org.inspirenxe.server.universe.block.material;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.inspirenxe.server.Game;
import org.inspirenxe.server.universe.world.Chunk;

public class MaterialManager {
    private final Game game;
    private final Map<String, Material> materialsByName = new ConcurrentHashMap<>();
    private final TIntObjectHashMap<Material> materialsById = new TIntObjectHashMap<>();

    public MaterialManager(Game game) {
        this.game = game;
    }

    public void register(Material material) {
        final Material previous = materialsByName.put(material.getName(), material);
        if (previous != null) {
            game.getLogger().warn("New material has conflicting name, previous material was overwritten: " + previous + " => " + material);
        }
        materialsById.put(material.getId(), material);
    }

    public Material get(String name) {
        return materialsByName.get(name);
    }

    public Material get(int packed) {
        final short id = (short) (packed >> 16);
        return get(id, (short) packed);
    }

    public Material get(final short id, final short data) {
        final short childId = Chunk.SUB_ID_MASK.extract(data);
        if (childId == 0) {
            return materialsById.get(id);
        } else {
            // TODO Optimize this as children is slower
            for(Object obj : materialsById.values()) {
                final Material other = (Material) obj;
                if (other.getId() == id && other.getChildId() == childId) {
                    return other;
                }
            }
            return null;
        }
    }
}
