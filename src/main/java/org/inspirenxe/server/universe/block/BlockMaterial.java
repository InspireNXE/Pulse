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
package org.inspirenxe.server.universe.block;

import org.inspirenxe.server.Game;
import org.inspirenxe.server.universe.material.Material;

public abstract class BlockMaterial extends Material {
    private float hardness = 0.0f;
    private float resistance = 0.0f;

    public BlockMaterial(Game game, String name) {
        super(game, name);
    }

    public float getHardness() {
        return hardness;
    }

    public BlockMaterial setHardness(float value) {
        hardness = value;
        return this;
    }

    public float getResistance() {
        return resistance;
    }

    public BlockMaterial setResistance(float value) {
        resistance = value;
        return this;
    }

    public boolean isUnbreakable() {
        return hardness == -1.0f;
    }

    public BlockMaterial setUnbreakable() {
        hardness = -1.0f;
        return this;
    }
}
