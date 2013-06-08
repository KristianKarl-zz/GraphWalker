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
 * <p>EdgeCoverage class.</p>
 */
public final class EdgeCoverage implements StopCondition {

    private final double limit;

    /**
     * <p>Constructor for EdgeCoverage.</p>
     *
     * @param value a {@link java.lang.String} object.
     */
    public EdgeCoverage(String value) {
        this(!"".equals(value)?Long.parseLong(value):100);
    }

    /**
     * <p>Constructor for EdgeCoverage.</p>
     *
     * @param limit a long.
     */
    public EdgeCoverage(long limit) {
        this.limit = (double) limit / PERCENTAGE_SCALE;
    }

    /** {@inheritDoc} */
    public boolean isFulfilled(ExecutionContext executionContext) {
        double totalEdgesCount = executionContext.getModel().getEdges().size();
        double visitedEdgesCount = executionContext.getVisitedEdges().size();
        return (visitedEdgesCount / totalEdgesCount) >= limit;
    }

    /** {@inheritDoc} */
    public double getFulfilment(ExecutionContext executionContext) {
        double totalEdgesCount = executionContext.getModel().getEdges().size();
        double visitedEdgesCount = executionContext.getVisitedEdges().size();
        return (visitedEdgesCount / totalEdgesCount) / limit;
    }
}
