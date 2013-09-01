/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2012 GraphWalker
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
package org.graphwalker.core.conditions.support;

import org.graphwalker.core.conditions.StopCondition;
import org.graphwalker.core.machine.ExecutionContext;

/**
 * @author Nils Olsson
 */
public final class TimeDuration implements StopCondition {

    private final String value;
    private final long duration;
    private final long timestamp;

    /**
     * <p>Constructor for TimeDuration.</p>
     *
     * @param value a {@link java.lang.String} object.
     */
    public TimeDuration(String value) {
        this.value = value;
        this.timestamp = System.currentTimeMillis();
        this.duration = Long.parseLong(value) * SECOND_SCALE;
    }

    public String getValue() {
        return value;
    }

    /** {@inheritDoc} */
    public boolean isFulfilled(ExecutionContext executionContext) {
        return getFulfilment(executionContext) >= FULFILLMENT_LEVEL;
    }

    /** {@inheritDoc} */
    public double getFulfilment(ExecutionContext executionContext) {
        return (double) (System.currentTimeMillis() - timestamp) / duration;
    }
}
