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
package org.inspirenxe.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.inspirenxe.server.network.Network;

public class Game {
    private volatile boolean running = false;
    private final Object wait = new Object();
    private final Logger logger;
    private final Network network;

    public Game() {
        logger = LogManager.getLogger("Pulse");
        network = new Network(this);
    }

    private void start() {
        logger.info("Starting pulse");
        network.start();
        running = true;
    }

    private void stop() {
        logger.info("Stopping pulse");
        network.stop();
    }

    public Logger getLogger() {
        return logger;
    }

    public Network getNetwork() {
        return network;
    }

    /**
     * Starts the game and causes the current thread to wait until the {@link #exit()} method is called. When this happens, the thread resumes and the game is stopped. Interrupting the thread will not
     * cause it to exit, only calling {@link #exit()} will.
     */
    public void open() {
        start();
        running = true;
        synchronized (wait) {
            while (isRunning()) {
                try {
                    wait.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
        stop();
    }

    /**
     * Wakes up the thread waiting for the game to exit (by having called {@link #open()}) and allows it to resume it's activity to trigger the end of the game.
     */
    public void exit() {
        running = false;
        synchronized (wait) {
            wait.notifyAll();
        }
    }

    /**
     * Returns true if the game is running, false if otherwise.
     *
     * @return Whether or not the game is running
     */
    public boolean isRunning() {
        return running;
    }
}
