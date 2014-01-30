/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2013 GraphWalker
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
package org.graphwalker.core.condition;

import org.graphwalker.core.machine.ExecutionContext;

/**
 * @author Nils Olsson
 */
public final class TimeDuration extends BaseStopCondition {

    private long duration;
    private long timestamp;

  public TimeDuration() {
  }

  public TimeDuration(String value) {
        super(value);
        this.timestamp = System.currentTimeMillis();
        this.duration = Long.parseLong(value) * SECOND_SCALE;
    }

    public boolean isFulfilled(ExecutionContext context) {
        return getFulfilment(context) >= FULFILLMENT_LEVEL;
    }

    public double getFulfilment(ExecutionContext context) {
        return (double) (System.currentTimeMillis() - timestamp) / duration;
    }

  public void setValue( String value) {
    super.setValue(value);
    this.timestamp = System.currentTimeMillis();
    this.duration = Long.parseLong(value) * SECOND_SCALE;
  }
}
