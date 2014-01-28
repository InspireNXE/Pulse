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
package org.inspirenxe.server.universe.snapshot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.flowpowered.commons.map.TripleIntObjectMap;
import com.flowpowered.commons.map.impl.TTripleInt21ObjectHashMap;
import com.flowpowered.math.vector.Vector3i;
import org.inspirenxe.server.universe.world.Chunk;
import org.inspirenxe.server.universe.world.World;

/**
 *
 */
public class WorldSnapshot {
    private final TripleIntObjectMap<ChunkSnapshot> chunks = new TTripleInt21ObjectHashMap<>();
    private final UUID id;
    private final String name;
    private long updateNumber = 0;
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    public WorldSnapshot(World world) {
        this.id = world.getID();
        this.name = world.getName();
    }

    public UUID getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasChunk(Vector3i position) {
        return hasChunk(position.getX(), position.getY(), position.getZ());
    }

    public boolean hasChunk(int x, int y, int z) {
        final Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return chunks.containsKey(x, y, z);
        } finally {
            lock.unlock();
        }
    }

    public ChunkSnapshot getChunk(Vector3i position) {
        return getChunk(position.getX(), position.getY(), position.getZ());
    }

    public ChunkSnapshot getChunk(int x, int y, int z) {
        final Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return chunks.get(x, y, z);
        } finally {
            lock.unlock();
        }
    }

    public Map<Vector3i, ChunkSnapshot> getChunks() {
        final Lock lock = this.lock.readLock();
        lock.lock();
        try {
            final Map<Vector3i, ChunkSnapshot> map = new HashMap<>(chunks.size());
            for (ChunkSnapshot chunk : chunks.valueCollection()) {
                map.put(chunk.getPosition(), chunk);
            }
            return map;
        } finally {
            lock.unlock();
        }
    }

    public long getUpdateNumber() {
        final Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return updateNumber;
        } finally {
            lock.unlock();
        }
    }

    public void update(World current) {
        if (!current.getID().equals(id)) {
            throw new IllegalArgumentException("Cannot update from a world with another ID");
        }
        final Lock lock = this.lock.writeLock();
        lock.lock();
        try {
            final Set<Vector3i> validChunks = new HashSet<>();
            boolean changed = false;
            for (Chunk chunk : current.getChunks().values()) {
                final Vector3i position = chunk.getPosition();
                ChunkSnapshot chunkSnapshot = chunks.get(position.getX(), position.getY(), position.getZ());
                if (chunkSnapshot == null) {
                    chunkSnapshot = new ChunkSnapshot(this, position);
                    chunks.put(position.getX(), position.getY(), position.getZ(), chunkSnapshot);
                }
                if (chunkSnapshot.update(chunk)) {
                    changed = true;
                }
                validChunks.add(position);
            }
            for (Iterator<ChunkSnapshot> iterator = chunks.valueCollection().iterator(); iterator.hasNext(); ) {
                final Vector3i position = iterator.next().getPosition();
                if (!validChunks.contains(position)) {
                    iterator.remove();
                    changed = true;
                }
            }
            if (changed) {
                updateNumber++;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final WorldSnapshot that = (WorldSnapshot) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "WorldSnapshot{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", updateNumber=" + updateNumber +
                '}';
    }
}
