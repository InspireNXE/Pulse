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
package org.inspirenxe.server.universe;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.flowpowered.commons.ticking.TickingElement;
import com.flowpowered.math.vector.Vector3i;
import org.inspirenxe.server.Game;
import org.inspirenxe.server.universe.block.material.MaterialRegistry;
import org.inspirenxe.server.universe.snapshot.WorldSnapshot;
import org.inspirenxe.server.universe.world.Chunk;
import org.inspirenxe.server.universe.world.World;

/**
 * Contains and manages all the voxel worlds.
 */
public class Universe extends TickingElement {
    private static final int TPS = 20;
    // Chunk data handling
    private static final int MAX_CHUNK_COLUMN_SECTIONS = 16;
    private static final byte[] UNLOAD_CHUNKS_IN_COLUMN = {0x78, (byte) 0x9C, 0x63, 0x64, 0x1C, (byte) 0xD9, 0x00, 0x00, (byte) 0x81, (byte) 0x80, 0x01, 0x01};
    private final Game game;
    private final MaterialRegistry materialRegistry;
    private final Map<UUID, World> worlds = new ConcurrentHashMap<>();
    private final Map<UUID, WorldSnapshot> worldSnapshots = new ConcurrentHashMap<>();
    private final Map<String, UUID> worldIDsByName = new ConcurrentHashMap<>();

    public Universe(Game game) {
        super("universe", TPS);
        this.game = game;
        materialRegistry = new MaterialRegistry(game);
    }

    @Override
    public void onStart() {
        game.getLogger().info("Starting universe");
        // TODO Load Minecraft default materials
    }

    @Override
    public void onTick(long dt) {
        updateSnapshots();
    }

    @Override
    public void onStop() {
        game.getLogger().info("Stopping universe");

        worlds.clear();
        updateSnapshots();
    }

    public Game getGame() {
        return game;
    }

    public MaterialRegistry getMaterials() {
        return materialRegistry;
    }

    public WorldSnapshot getWorldSnapshot(UUID id) {
        return worldSnapshots.get(id);
    }

    public WorldSnapshot getWorldSnapshot(String name) {
        return worldSnapshots.get(worldIDsByName.get(name));
    }

    private World getWorld(String name) {
        return worlds.get(worldIDsByName.get(name));
    }

    private void addWorld(World world) {
        worlds.put(world.getID(), world);
        worldIDsByName.put(world.getName(), world.getID());
    }

    private void updateSnapshots() {
        for (Iterator<WorldSnapshot> iterator = worldSnapshots.values().iterator(); iterator.hasNext(); ) {
            if (!worlds.containsKey(iterator.next().getID())) {
                iterator.remove();
            }
        }
        for (Map.Entry<UUID, World> entry : worlds.entrySet()) {
            final UUID id = entry.getKey();
            final World world = entry.getValue();
            WorldSnapshot worldSnapshot = worldSnapshots.get(id);
            if (worldSnapshot == null) {
                worldSnapshot = new WorldSnapshot(world);
                worldSnapshots.put(id, worldSnapshot);
            }
            worldSnapshot.update(world);
        }
    }
}
