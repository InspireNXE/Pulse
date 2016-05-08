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
package org.inspirenxe.pulse.util;

import org.inspirenxe.pulse.util.thread.TPSLimitedThread;
import org.slf4j.Logger;

/**
 * Represents an element that ticks at a specific TPS.
 */
public abstract class TickingElement {
    private final Logger logger;
    private final String name;
    private final int tps;
    private final ThreadGroup group;
    private volatile TPSLimitedThread thread;

    public TickingElement(Logger logger, String name, int tps) {
        this.logger = logger;
        this.name = name;
        this.tps = tps;
        this.group = new ThreadGroup(name + " ThreadGroup");
    }

    public final void start() {
        synchronized (this) {
            if (this.thread == null) {
                this.thread = new TPSLimitedThread(this.logger, this.group, this.name, this, this.tps);
                this.thread.start();
            }
        }
    }

    public final void stop() {
        synchronized (this) {
            if (this.thread != null) {
                this.thread.terminate();
                this.thread = null;
            }
        }
    }

    public final boolean isRunning() {
        return this.thread != null && this.thread.isRunning();
    }

    public TPSLimitedThread getThread() {
        return this.thread;
    }

    public ThreadGroup getGroup() {
        return this.group;
    }

    public abstract void onStart();

    public abstract void onTick(long dt);

    public abstract void onStop();
}
