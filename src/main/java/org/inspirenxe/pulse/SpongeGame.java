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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.property.PropertyRegistry;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.world.TeleportHelper;

import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SpongeGame implements Game {
    public static final String ECOSYSTEM_NAME = Sponge.class.getSimpleName();
    public static final String ECOSYSTEM_IDENTIFIER = ECOSYSTEM_NAME.toLowerCase(Locale.ENGLISH);
    public static final String METADATA_VERSION = SpongeGame.class.getPackage().getImplementationVersion();
    public static final String VERSION = METADATA_VERSION == null ? "dev" : METADATA_VERSION;
    public static final Logger logger = LoggerFactory.getLogger(ECOSYSTEM_NAME);
    public static SpongeGame instance;
    private final Configuration configuration;
    private final SpongeServer server;
    // A semaphore with no permits, so that the first acquire() call blocks
    private final Semaphore semaphore = new Semaphore(0);
    private final AtomicBoolean running = new AtomicBoolean(false);

    public SpongeGame(Configuration configuration) {
        this.configuration = configuration;
        this.server = new SpongeServer(this);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Starts the game and causes the current thread to wait until the {@link #close()} method is called. When this happens, the thread resumes and
     * the game is stopped. Interrupting the thread will not cause it to close, only calling {@link #close()} will. Calls to {@link #close()}
     * before open() are not counted.
     */
    public void launch() {
        // Only start the game if running has a value of false, in which case it's set to true and the if statement passes
        if (running.compareAndSet(false, true)) {
            instance = this;
            Runtime.getRuntime().addShutdownHook(new Thread(this::close, ECOSYSTEM_IDENTIFIER + " - shutdown"));
            // Start the threads, which might release permits by calling close() before all are started
            server.start();
            // Attempts to acquire a permit, but since none are available (except for the situation stated above), the thread blocks
            semaphore.acquireUninterruptibly();
            // A permit was acquired, which means close() was called; so we stop game. The available permit count returns to zero
            server.stop();
        }
    }

    /**
     * Wakes up the thread that has opened the game (by having called {@link #launch()}) and allows it to resume it's activity to trigger the end
     * of the game.
     */
    public void close() {
        // Only stop the game if running has a value of true, in which case it's set to false and the if statement passes
        if (running.compareAndSet(true, false)) {
            // Release a permit (which doesn't need to be held by the thread in the first place),
            // allowing the main thread to acquire one and resume to close the game
            semaphore.release();
            // The available permit count is now non-zero
        }
    }

    @Override
    public Platform getPlatform() {
        return null;
    }

    @Override
    public SpongeServer getServer() {
        return server;
    }

    @Override
    public PluginManager getPluginManager() {
        return null;
    }

    @Override
    public EventManager getEventManager() {
        return null;
    }

    @Override
    public AssetManager getAssetManager() {
        return null;
    }

    @Override
    public GameRegistry getRegistry() {
        return null;
    }

    @Override
    public GameDictionary getGameDictionary() {
        return null;
    }

    @Override
    public ServiceManager getServiceManager() {
        return null;
    }

    @Override
    public Scheduler getScheduler() {
        return null;
    }

    @Override
    public DataManager getDataManager() {
        return null;
    }

    @Override
    public PropertyRegistry getPropertyRegistry() {
        return null;
    }

    @Override
    public CommandManager getCommandManager() {
        return null;
    }

    @Override
    public TeleportHelper getTeleportHelper() {
        return null;
    }

    @Override
    public ConfigManager getConfigManager() {
        return null;
    }

    @Override
    public Path getSavesDirectory() {
        return null;
    }

    @Override
    public GameState getState() {
        return null;
    }

    @Override
    public ChannelRegistrar getChannelRegistrar() {
        return null;
    }
}
