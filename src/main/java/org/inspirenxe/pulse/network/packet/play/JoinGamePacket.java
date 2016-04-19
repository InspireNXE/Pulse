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
package org.inspirenxe.pulse.network.packet.play;

import com.flowpowered.network.Codec;
import com.flowpowered.network.Message;
import com.google.common.base.Objects;
import io.netty.buffer.ByteBuf;
import org.inspirenxe.pulse.network.packet.util.ByteBufUtils;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.difficulty.Difficulty;

import java.io.IOException;

public class JoinGamePacket implements Message, Codec<JoinGamePacket> {
    private int playerId;
    private GameMode gameMode;
    private boolean hardcore;
    private DimensionType dimensionType;
    private Difficulty difficulty;
    private short maxPlayers;
    private GeneratorType generatorType;

    public JoinGamePacket() {}

    public JoinGamePacket(int playerId, GameMode gameMode, boolean hardcore, DimensionType dimensionType, Difficulty difficulty, short maxPlayers,
            GeneratorType generatorType) {
        this.playerId = playerId;
        this.gameMode = gameMode;
        this.hardcore = hardcore;
        this.dimensionType = dimensionType;
        this.difficulty = difficulty;
        this.maxPlayers = maxPlayers;
        this.generatorType = generatorType;
    }

    public int getPlayerId() {
        return playerId;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public boolean isHardcore() {
        return hardcore;
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public short getMaxPlayers() {
        return maxPlayers;
    }

    public GeneratorType getGeneratorType() {
        return generatorType;
    }

    @Override
    public JoinGamePacket decode(ByteBuf buf) throws IOException {
        throw new IOException("The server should not receive a join game from the Minecraft client!");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, JoinGamePacket message) throws IOException {
        buf.writeInt(1);
        short gameMode = (short) 0;
        if (message.isHardcore()) {
            gameMode |= 8;
        }
        buf.writeByte(gameMode);
        buf.writeByte(0);
        buf.writeByte(0);
        buf.writeByte(1);
        ByteBufUtils.writeUTF8(buf, "default");
        buf.writeBoolean(false);
        return buf;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("playerId", playerId)
                .add("gamemode", gameMode)
                .add("hardcore", hardcore)
                .add("dimensionType", dimensionType)
                .add("difficulty", difficulty)
                .add("maxPlayers", maxPlayers)
                .add("generatorType", generatorType)
                .toString();
    }
}

