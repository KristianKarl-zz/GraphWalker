/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 GraphWalker
 * %%
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
 * #L%
 */

package org.graphwalker.core;

import org.apache.log4j.Logger;

public class Status {
    static private Logger log = Util.setupLogger(Status.class);
    private int state = stopped;

    public static final int initial = 1;
    public static final int running = 2;
    public static final int previous = 4;
    public static final int next = 8;
    public static final int paused = 16;
    public static final int stopped = 32;
    public static final int executingJavaTest = 64;
    public static final int executingSoapTest = 128;
    public static final int executingLogTest = 256;

    public boolean isExecutingLogTest() {
        return (state & executingLogTest) == executingLogTest;
    }

    public boolean isExecutingSoapTest() {
        return (state & executingSoapTest) == executingSoapTest;
    }

    public boolean isExecutingJavaTest() {
        return (state & executingJavaTest) == executingJavaTest;
    }

    public boolean isInitial() {
        return (state & initial) == initial;
    }

    public boolean isStopped() {
        return (state & stopped) == stopped;
    }

    public boolean isPrevious() {
        return (state & previous) == previous;
    }

    public boolean isNext() {
        return (state & next) == next;
    }

    public boolean isRunning() {
        return (state & running) == running;
    }

    public boolean isPaused() {
        return (state & paused) == paused;
    }

    public void setState(int state) {
        log.debug("Set the state with: " + toStr(state));
        this.state |= state;
        log.debug("State: " + toStr(this.state));
    }

    public void unsetState(int state) {
        log.debug("Unset the state with: " + toStr(state));
        if ((this.state & state) == state) {
            this.state ^= state;
        }
        log.debug("State: " + toStr(this.state));
    }

    public void setStopped() {
        log.debug("Set the state to stopped");
        state = stopped;
    }

    public void reset() {
        log.debug("Reset the state to initial");
        state = initial;
    }

    public String toStr(int state) {
        String str = "";
        if ((state & initial) == initial) {
            str += "inital";
        }
        if ((state & running) == running) {
            if (str.isEmpty()) {
                str += "running";
            } else {
                str += " | running";
            }
        }
        if ((state & previous) == previous) {
            if (str.isEmpty()) {
                str += "previous";
            } else {
                str += " | previous";
            }
        }
        if ((state & next) == next) {
            if (str.isEmpty()) {
                str += "next";
            } else {
                str += " | next";
            }
        }
        if ((state & paused) == paused) {
            if (str.isEmpty()) {
                str += "paused";
            } else {
                str += " | paused";
            }
        }
        if ((state & stopped) == stopped) {
            if (str.isEmpty()) {
                str += "stopped";
            } else {
                str += " | stopped";
            }
        }
        if ((state & executingJavaTest) == executingJavaTest) {
            if (str.isEmpty()) {
                str += "executingJavaTest";
            } else {
                str += " | executingJavaTest";
            }
        }
        if ((state & executingSoapTest) == executingSoapTest) {
            if (str.isEmpty()) {
                str += "executingSoapTest";
            } else {
                str += " | executingSoapTest";
            }
        }
        if ((state & executingLogTest) == executingLogTest) {
            if (str.isEmpty()) {
                str += "executingLogTest";
            } else {
                str += " | executingLogTest";
            }
        }
        return str;
    }
}
