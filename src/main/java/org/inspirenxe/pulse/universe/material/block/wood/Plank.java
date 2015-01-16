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
package org.inspirenxe.pulse.universe.material.block.wood;

import org.inspirenxe.pulse.Game;
import org.inspirenxe.pulse.universe.block.BlockMaterial;

public class Plank extends BlockMaterial {
    private static final float HARDNESS = 2.0f;
    private static final float RESISTANCE = 10.0f;
    private static final short ID = 5;
    private static final String NAME = "plank.oak";

    public Plank(Game game) {
        this(game, NAME);
    }

    protected Plank(Game game, String name) {
        super(game, name);
        setHardness(HARDNESS);
        setResistance(RESISTANCE);
    }

    @Override
    public short getId() {
        return ID;
    }

    public static class AcaciaPlank extends Plank {
        private static final short ID = 4;
        private static final String NAME = "plank.acacia";

        public AcaciaPlank(Game game, String name) {
            super(game, NAME);
        }

        @Override
        public short getChildId() {
            return ID;
        }
    }

    public static class BirchPlank extends Plank {
        private static final short ID = 4;
        private static final String NAME = "plank.birch";

        public BirchPlank(Game game, String name) {
            super(game, NAME);
        }

        @Override
        public short getChildId() {
            return ID;
        }
    }

    public static class DarkPlank extends Plank {
        private static final short ID = 5;
        private static final String NAME = "plank.darkoak";

        public DarkPlank(Game game, String name) {
            super(game, NAME);
        }

        @Override
        public short getChildId() {
            return ID;
        }
    }

    public static class JunglePlank extends Plank {
        private static final short ID = 3;
        private static final String NAME = "plank.jungle";

        public JunglePlank(Game game, String name) {
            super(game, NAME);
        }

        @Override
        public short getChildId() {
            return ID;
        }
    }

    public static class SprucePlank extends Plank {
        private static final short ID = 1;
        private static final String NAME = "plank.spruce";

        public SprucePlank(Game game, String name) {
            super(game, NAME);
        }

        @Override
        public short getChildId() {
            return ID;
        }
    }
}
