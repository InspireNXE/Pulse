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

import jline.console.ConsoleReader;
import org.inspirenxe.pulse.console.TerminalConsoleAppender;
import org.inspirenxe.pulse.network.Network;
import org.inspirenxe.pulse.util.TickingElement;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.ChunkTicketManager;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.storage.ChunkLayout;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class SpongeServer extends TickingElement implements Server, ConsoleSource {
    private final SpongeGame game;
    private final Network network;
    private final ConsoleReader reader;

    public SpongeServer(SpongeGame game) {
        super("server", game.getConfiguration().getTickRate());
        this.game = game;
        this.network = new Network(this);
        this.reader = TerminalConsoleAppender.getReader();
    }

    public void onStart() {
        SpongeGame.logger.info("Starting server, running version " + SpongeGame.VERSION + ", please wait a moment.");
        network.start();
    }

    public void onTick(long dt) {
        // TODO Split off into another thread that runs less than the server tick rate?
        String line = null;
        try {
            line = reader.readLine("> ");
        } catch (IOException ex) {
            SpongeGame.logger.error("Exception handling console input", ex);
        }

        if (line != null) {
            line = line.trim();

            if (!line.isEmpty()) {
                SpongeGame.logger.info("Testing console input. Entered [{}]", line);
            }
        }
    }

    public void onStop() {
        SpongeGame.logger.info("Stopping game, please wait a moment");
        network.stop();
    }

    public SpongeGame getGame() {
        return this.game;
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
        return this.game.getConfiguration().isAuthenticateSessions();
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
        return this;
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

    @Override
    public String getName() {
        return SpongeGame.ECOSYSTEM_NAME;
    }

    @Override public String getIdentifier() {
        return SpongeGame.ECOSYSTEM_IDENTIFIER;
    }

    @Override public Set<Context> getActiveContexts() {
        return null;
    }

    @Override public void sendMessage(Text message) {

    }

    @Override public MessageChannel getMessageChannel() {
        return null;
    }

    @Override public void setMessageChannel(MessageChannel channel) {

    }

    @Override public Optional<CommandSource> getCommandSource() {
        return null;
    }

    @Override public SubjectCollection getContainingCollection() {
        return null;
    }

    @Override public SubjectData getSubjectData() {
        return null;
    }

    @Override public SubjectData getTransientSubjectData() {
        return null;
    }

    @Override public boolean hasPermission(Set<Context> contexts, String permission) {
        return false;
    }

    @Override public Tristate getPermissionValue(Set<Context> contexts, String permission) {
        return null;
    }

    @Override public boolean isChildOf(Set<Context> contexts, Subject parent) {
        return false;
    }

    @Override public List<Subject> getParents(Set<Context> contexts) {
        return null;
    }
}
