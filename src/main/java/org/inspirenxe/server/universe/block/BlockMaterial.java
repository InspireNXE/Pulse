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

import org.inspirenxe.server.Game;
import org.inspirenxe.server.universe.material.Material;

public abstract class BlockMaterial extends Material {
    private float hardness = 0.0f;
    private float resistance = 0.0f;

    public BlockMaterial(Game game, String name) {
        super(game, name);
    }

    /**
     * Gets the hardness value of the block.
     *
     * @return Hardness of the block.
     */
    public float getHardness() {
        return hardness;
    }

    /**
     * Determines how many hits it will take to destroy the block.
     * This should be called before setResistance if both are used on a block.
     *
     * @param value The float value to use for hardness.
     * @return BlockMaterial for chaining.
     */
    public BlockMaterial setHardness(float value) {
        hardness = hardness < value ? value * 5.0f : value;
        return this;
    }

    /**
     * Gets the explosion resistance value of the block.
     *
     * @return Resistance of the block.
     */
    public float getResistance() {
        return resistance;
    }

    /**
     * Sets the resistances to explosions.
     *
     * @param value The float value to use for resistance.
     * @return BlockMaterial for chaining.
     */
    public BlockMaterial setResistance(float value) {
        resistance = value * 3.0f;
        return this;
    }

    /**
     * Check if a block is unbreakable.
     *
     * @return True if unbreakable, false if breakable.
     */
    public boolean isUnbreakable() {
        return hardness == -1.0f;
    }

    /**
     * Sets the block as unbreakable.
     *
     * @return BlockMaterial for chaining.
     */
    public BlockMaterial setUnbreakable() {
        hardness = -1.0f;
        return this;
    }
}
