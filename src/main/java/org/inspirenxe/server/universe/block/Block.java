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
package org.inspirenxe.server.universe.block;

import com.flowpowered.math.vector.Vector3i;
import org.inspirenxe.server.Game;
import org.inspirenxe.server.universe.material.Material;
import org.inspirenxe.server.universe.world.Chunk;

public class Block {
    private final Game game;
    private final Vector3i position;
    private final Material material;
    private final short blockLight;
    private final short blockSkyLight;

    public Block(Game game, Vector3i position, int packed) {
        this(game, position, (short) (packed >> 16), (short) packed);
    }

    public Block(Game game, Vector3i position, short id, short data) {
        this(game, position, game.getUniverse().getMaterials().get(id, Chunk.SUB_ID_MASK.extract(data)), Chunk.BLOCK_LIGHT_MASK.extract(data), Chunk.BLOCK_SKY_LIGHT_MASK.extract(data));
    }

    public Block(Game game, Vector3i position, Material material, short blockLight, short blockSkyLight) {
        this.game = game;
        this.position = position;
        this.material = material;
        this.blockLight = blockLight;
        this.blockSkyLight = blockSkyLight;
    }

    public Game getGame() {
        return game;
    }

    public Material getMaterial() {
        return material;
    }

    public Vector3i getPosition() {
        return position;
    }

    public short getLight() {
        return blockLight;
    }

    public short getSkyLight() {
        return blockSkyLight;
    }
}
