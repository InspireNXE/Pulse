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
package org.inspirenxe.server.universe.material.block.wood;

import org.inspirenxe.server.Game;
import org.inspirenxe.server.universe.block.BlockMaterial;

public class OakLog extends BlockMaterial {
    private static final float HARDNESS = 2.0f;
    private static final short ID = 17;
    private static final String NAME = "log.oak";

    public OakLog(Game game) {
        this(game, NAME);
    }

    public OakLog(Game game, String name) {
        super(game, name);
        setHardness(HARDNESS);
    }

    @Override
    public short getId() {
        return ID;
    }

    public static class BirchLog extends OakLog {
        private static final short ID = 2;
        private static final String NAME = "log.birch";

        public BirchLog(Game game) {
            super(game, NAME);
        }

        @Override
        public short getChildId() {
            return ID;
        }
    }

    public static class JungleLog extends OakLog {
        private static final short ID = 3;
        private static final String NAME = "log.jungle";

        public JungleLog(Game game) {
            super(game, NAME);
        }

        @Override
        public short getChildId() {
            return ID;
        }
    }

    public static class SpruceLog extends OakLog {
        private static final short ID = 1;
        private static final String NAME = "log.spruce";

        public SpruceLog(Game game) {
            super(game, NAME);
        }

        @Override
        public short getChildId() {
            return ID;
        }
    }
}
