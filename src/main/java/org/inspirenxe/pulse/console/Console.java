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
package org.inspirenxe.pulse.console;

import jline.console.ConsoleReader;
import org.inspirenxe.pulse.SpongeGame;

import java.io.IOException;

public final class Console {
    private final ReaderThread readerThread = new ReaderThread();
    private ConsoleReader reader;

    public void start() {
        SpongeGame.logger.info("Starting console.");
        this.reader = TerminalConsoleAppender.getReader();
        this.readerThread.start();
    }

    public void stop() {
        SpongeGame.logger.info("Stopping console.");
        this.reader = TerminalConsoleAppender.getReader();
        this.readerThread.interrupt();
    }

    private class ReaderThread extends Thread {
        protected ReaderThread() {
            setName("console");
            setDaemon(true);
        }

        @Override
        public void run() {
            String line = null;
            while (!this.isInterrupted()) {
                try {
                    line = Console.this.reader.readLine("\r> ");
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
        }
    }
}
