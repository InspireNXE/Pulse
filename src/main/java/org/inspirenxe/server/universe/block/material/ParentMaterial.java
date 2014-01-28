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
package org.inspirenxe.server.universe.block.material;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TShortObjectHashMap;
import org.inspirenxe.server.Game;

/**
 * Represents a {@link org.inspirenxe.server.universe.block.material.Material} which is the parent of {@link ChildMaterial}s.
 */
public abstract class ParentMaterial extends Material {
    private final Map<String, ChildMaterial> childMaterials = new ConcurrentHashMap<>();

    public ParentMaterial(Game game, String name) {
        super(game, name);
    }

    public ChildMaterial getChildMaterial(String name) {
        return childMaterials.get(name);
    }

    protected void addChild(ChildMaterial childMaterial) {
        final ChildMaterial previous = childMaterials.put(childMaterial.getName(), childMaterial);
        if (previous != null) {
            getGame().getLogger().warn("New child material has conflicting name, previous child material was overwritten: " + previous + " => " + childMaterial);
        }
    }
}
