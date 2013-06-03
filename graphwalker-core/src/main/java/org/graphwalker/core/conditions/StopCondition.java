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

package org.graphwalker.core.conditions;

import org.graphwalker.core.machine.Context;

/**
 * <p>StopCondition interface.</p>
 */
public interface StopCondition {

    /** Constant <code>FULFILLMENT_LEVEL=0.999999</code> */
    double FULFILLMENT_LEVEL = 0.999999;
    /** Constant <code>PERCENTAGE_SCALE=100</code> */
    int PERCENTAGE_SCALE = 100;
    /** Constant <code>SECOND_SCALE=1000</code> */
    int SECOND_SCALE = 1000;


    /**
     * <p>isFulfilled.</p>
     *
     * @param context a {@link org.graphwalker.core.machine.Context} object.
     * @return a boolean.
     */
    boolean isFulfilled(Context context);


    /**
     * <p>getFulfilment.</p>
     *
     * @param context a {@link org.graphwalker.core.machine.Context} object.
     * @return a double.
     */
    double getFulfilment(Context context);
}
