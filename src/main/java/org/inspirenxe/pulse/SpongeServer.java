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
package org.inspirenxe.pulse;

import org.inspirenxe.pulse.console.Console;
import org.inspirenxe.pulse.network.Network;
import org.inspirenxe.pulse.util.TickingElement;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.world.ChunkTicketManager;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.storage.ChunkLayout;
import org.spongepowered.api.world.storage.WorldProperties;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpongeServer extends TickingElement implements Server {
    private static final int TPS = 20;
    private final Console console = new Console();
    private final Network network = new Network();

    public SpongeServer() {
        super("main", TPS);
    }

    public void onStart() {
        SpongeGame.logger.info("Starting game, running version " + SpongeGame.VERSION + ", please wait a moment");
        network.start();
    }

    public void onTick(long dt) {

    }

    public void onStop() {
        SpongeGame.logger.info("Stopping game, please wait a moment");
        network.stop();
    }

    public Network getNetwork() {
        return network;
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return null;
    }

    @Override
    public int getMaxPlayers() {
        return 0;
    }

    @Override
    public Optional<Player> getPlayer(UUID uniqueId) {
        return null;
    }

    @Override
    public Optional<Player> getPlayer(String name) {
        return null;
    }

    @Override
    public Collection<World> getWorlds() {
        return null;
    }

    @Override
    public Collection<WorldProperties> getUnloadedWorlds() {
        return null;
    }

    @Override
    public Collection<WorldProperties> getAllWorldProperties() {
        return null;
    }

    @Override
    public Optional<World> getWorld(UUID uniqueId) {
        return null;
    }

    @Override
    public Optional<World> getWorld(String worldName) {
        return null;
    }

    @Override
    public Optional<WorldProperties> getDefaultWorld() {
        return null;
    }

    @Override
    public String getDefaultWorldName() {
        return null;
    }

    @Override
    public Optional<World> loadWorld(String worldName) {
        return null;
    }

    @Override
    public Optional<World> loadWorld(UUID uniqueId) {
        return null;
    }

    @Override
    public Optional<World> loadWorld(WorldProperties properties) {
        return null;
    }

    @Override
    public Optional<WorldProperties> getWorldProperties(String worldName) {
        return null;
    }

    @Override
    public Optional<WorldProperties> getWorldProperties(UUID uniqueId) {
        return null;
    }

    @Override
    public boolean unloadWorld(World world) {
        return false;
    }

    @Override
    public Optional<WorldProperties> createWorldProperties(WorldCreationSettings settings) {
        return null;
    }

    @Override
    public CompletableFuture<Optional<WorldProperties>> copyWorld(WorldProperties worldProperties, String copyName) {
        return null;
    }

    @Override
    public Optional<WorldProperties> renameWorld(WorldProperties worldProperties, String newName) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> deleteWorld(WorldProperties worldProperties) {
        return null;
    }

    @Override
    public boolean saveWorldProperties(WorldProperties properties) {
        return false;
    }

    @Override
    public Optional<Scoreboard> getServerScoreboard() {
        return null;
    }

    @Override
    public ChunkLayout getChunkLayout() {
        return null;
    }

    @Override
    public int getRunningTimeTicks() {
        return 0;
    }

    @Override
    public MessageChannel getBroadcastChannel() {
        return null;
    }

    @Override
    public void setBroadcastChannel(MessageChannel channel) {

    }

    @Override
    public Optional<InetSocketAddress> getBoundAddress() {
        return null;
    }

    @Override
    public boolean hasWhitelist() {
        return false;
    }

    @Override
    public void setHasWhitelist(boolean enabled) {

    }

    @Override
    public boolean getOnlineMode() {
        return false;
    }

    @Override
    public Text getMotd() {
        return null;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void shutdown(Text kickMessage) {

    }

    @Override
    public ConsoleSource getConsole() {
        return console;
    }

    @Override
    public ChunkTicketManager getChunkTicketManager() {
        return null;
    }

    @Override
    public GameProfileManager getGameProfileManager() {
        return null;
    }

    @Override
    public double getTicksPerSecond() {
        return 0;
    }

    @Override
    public Optional<ResourcePack> getDefaultResourcePack() {
        return null;
    }
}