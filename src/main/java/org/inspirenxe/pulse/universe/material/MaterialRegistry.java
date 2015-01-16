/**
 * This file is part of Pulse, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2014-2015 InspireNXE <http://inspirenxe.org/>
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
package org.inspirenxe.pulse.universe.material;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.inspirenxe.pulse.Game;
import org.inspirenxe.pulse.universe.world.Chunk;

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
        // TODO Enforce Universe-only access for puts?
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
