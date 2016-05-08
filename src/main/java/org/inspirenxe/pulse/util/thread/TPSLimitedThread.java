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
package org.inspirenxe.pulse.util.thread;

import org.inspirenxe.pulse.util.TickingElement;
import org.slf4j.Logger;

/**
 * Represents a thread that runs at a specific TPS until terminated.
 */
public final class TPSLimitedThread extends Thread {
    private final Logger logger;
    private final TickingElement element;
    private final Timer timer;
    private volatile boolean running = false;

    public TPSLimitedThread(Logger logger, String name, TickingElement element, int tps) {
        this(logger, null, name, element, tps);
    }

    public TPSLimitedThread(Logger logger, ThreadGroup group, String name, TickingElement element, int tps) {
        super(group, name);
        this.logger = logger;
        this.element = element;
        this.timer = new Timer(tps);
    }

    @Override
    public void run() {
        running = true;
        element.onStart();
        timer.start();
        long lastTime = getTime() - (long) (1f / timer.getTicksPerSecond() * 1000000000), currentTime;
        while (running) {
            try {
                element.onTick((currentTime = getTime()) - lastTime);
                lastTime = currentTime;
                timer.sync();
            } catch (Exception ex) {
                this.logger.error("Exception in thread [{}], attempting to stop normally.", this.getName(), ex);
                element.onStop();
                return;
            }
        }
        element.onStop();
    }

    public void terminate() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    private static long getTime() {
        return System.nanoTime();
    }
}
