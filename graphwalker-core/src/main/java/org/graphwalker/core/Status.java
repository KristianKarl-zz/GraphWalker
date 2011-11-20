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

/**
 * <p>Status class.</p>
 */
public class Status {
    static private Logger log = Util.setupLogger(Status.class);
    private int state = stopped;

    /** Constant <code>initial=1</code> */
    public static final int initial = 1;
    /** Constant <code>running=2</code> */
    public static final int running = 2;
    /** Constant <code>previous=4</code> */
    public static final int previous = 4;
    /** Constant <code>next=8</code> */
    public static final int next = 8;
    /** Constant <code>paused=16</code> */
    public static final int paused = 16;
    /** Constant <code>stopped=32</code> */
    public static final int stopped = 32;
    /** Constant <code>executingJavaTest=64</code> */
    public static final int executingJavaTest = 64;
    /** Constant <code>executingSoapTest=128</code> */
    public static final int executingSoapTest = 128;
    /** Constant <code>executingLogTest=256</code> */
    public static final int executingLogTest = 256;

    /**
     * <p>isExecutingLogTest.</p>
     *
     * @return a boolean.
     */
    public boolean isExecutingLogTest() {
        return (state & executingLogTest) == executingLogTest;
    }

    /**
     * <p>isExecutingSoapTest.</p>
     *
     * @return a boolean.
     */
    public boolean isExecutingSoapTest() {
        return (state & executingSoapTest) == executingSoapTest;
    }

    /**
     * <p>isExecutingJavaTest.</p>
     *
     * @return a boolean.
     */
    public boolean isExecutingJavaTest() {
        return (state & executingJavaTest) == executingJavaTest;
    }

    /**
     * <p>isInitial.</p>
     *
     * @return a boolean.
     */
    public boolean isInitial() {
        return (state & initial) == initial;
    }

    /**
     * <p>isStopped.</p>
     *
     * @return a boolean.
     */
    public boolean isStopped() {
        return (state & stopped) == stopped;
    }

    /**
     * <p>isPrevious.</p>
     *
     * @return a boolean.
     */
    public boolean isPrevious() {
        return (state & previous) == previous;
    }

    /**
     * <p>isNext.</p>
     *
     * @return a boolean.
     */
    public boolean isNext() {
        return (state & next) == next;
    }

    /**
     * <p>isRunning.</p>
     *
     * @return a boolean.
     */
    public boolean isRunning() {
        return (state & running) == running;
    }

    /**
     * <p>isPaused.</p>
     *
     * @return a boolean.
     */
    public boolean isPaused() {
        return (state & paused) == paused;
    }

    /**
     * <p>Setter for the field <code>state</code>.</p>
     *
     * @param state a int.
     */
    public void setState(int state) {
        log.debug("Set the state with: " + toStr(state));
        this.state |= state;
        log.debug("State: " + toStr(this.state));
    }

    /**
     * <p>unsetState.</p>
     *
     * @param state a int.
     */
    public void unsetState(int state) {
        log.debug("Unset the state with: " + toStr(state));
        if ((this.state & state) == state) {
            this.state ^= state;
        }
        log.debug("State: " + toStr(this.state));
    }

    /**
     * <p>Setter for the field <code>stopped</code>.</p>
     */
    public void setStopped() {
        log.debug("Set the state to stopped");
        state = stopped;
    }

    /**
     * <p>reset.</p>
     */
    public void reset() {
        log.debug("Reset the state to initial");
        state = initial;
    }

    /**
     * <p>toStr.</p>
     *
     * @param state a int.
     * @return a {@link java.lang.String} object.
     */
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
