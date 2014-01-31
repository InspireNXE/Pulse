package org.inspirenxe.server.universe.block.material;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.inspirenxe.server.Game;
import org.inspirenxe.server.universe.world.Chunk;

public class MaterialRegistry {
    private final Game game;
    // Name -> Material
    private final Map<String, Material> materialsByName = new ConcurrentHashMap<>();
    // Id -> Material - INTERNAL ONLY
    private final Map<Short, Material> materialsById = new ConcurrentHashMap<>();
    // Id -> (Material with non zero child id) - INTERNAL ONLY
    private final Map<Short, CopyOnWriteArrayList<Material>> childMaterialsById = new ConcurrentHashMap<>();

    public MaterialRegistry(Game game) {
        this.game = game;
    }

    public void put(Material material) {
        final Material previous = materialsByName.put(material.getName(), material);
        if (previous != null) {
            game.getLogger().warn("New material has conflicting name, previous material was overwritten: " + previous + " => " + material);
        }
        if (material.getChildId() != 0) {
            CopyOnWriteArrayList<Material> childMaterialsForId = childMaterialsById.get(material.getId());
            if (childMaterialsForId == null) {
                childMaterialsForId = new CopyOnWriteArrayList<>();
                childMaterialsById.put(material.getId(), childMaterialsForId);
            }
            childMaterialsForId.add(material);
        } else {
            materialsById.put(material.getId(), material);
        }
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
            for (Object obj : childMaterialsById.values()) {
                final Material other = (Material) obj;
                if (other.getId() == id && other.getChildId() == childId) {
                    return other;
                }
            }
            return null;
        }
    }
}
