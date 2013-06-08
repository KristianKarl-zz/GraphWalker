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
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.machine.ExecutionContext;

import javax.validation.constraints.NotNull;

/**
 * <p>ReachedEdge class.</p>
 */
public final class ReachedEdge implements StopCondition {

    @NotNull
    private final String name;

    /**
     * <p>Constructor for ReachedEdge.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public ReachedEdge(String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    public boolean isFulfilled(ExecutionContext executionContext) {
        return getFulfilment(executionContext) >= FULFILLMENT_LEVEL;
    }

    /** {@inheritDoc} */
    public double getFulfilment(ExecutionContext executionContext) {
        if (name.equals(executionContext.getCurrentElement().getName())) {
            return 1;
        } else {
            double maxFulfilment = 0;
            for (Edge edge: executionContext.getModel().getEdgesByName(name)) {
                int distance = executionContext.getModel().getShortestDistance(executionContext.getCurrentElement(), edge);
                int max = executionContext.getModel().getMaximumDistance(edge);
                double fulfilment = 1 - (double)distance/max;
                if (maxFulfilment < fulfilment) {
                    maxFulfilment = fulfilment;
                }
            }
            return maxFulfilment;
        }
    }

}
