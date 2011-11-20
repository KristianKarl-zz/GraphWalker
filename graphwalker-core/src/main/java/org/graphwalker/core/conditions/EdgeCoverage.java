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

package org.graphwalker.core.conditions;

import org.apache.log4j.Logger;
import org.graphwalker.core.Util;
import org.graphwalker.core.exceptions.StopConditionException;

/**
 * <p>EdgeCoverage class.</p>
 */
public class EdgeCoverage extends AbstractStopCondition {

    private double limit;
    static Logger logger = Util.setupLogger(EdgeCoverage.class);

    /**
     * <p>Constructor for EdgeCoverage.</p>
     *
     * @throws org.graphwalker.core.exceptions.StopConditionException if any.
     */
    public EdgeCoverage() throws StopConditionException {
        this(1);
    }

    /**
     * <p>Constructor for EdgeCoverage.</p>
     *
     * @param limit a double.
     * @throws org.graphwalker.core.exceptions.StopConditionException if any.
     */
    public EdgeCoverage(double limit) throws StopConditionException {
        if (limit > 1 || limit < 0)
            throw new StopConditionException("Excpeted an edge coverage between 0 and 100. Actual: " + limit * 100);
        this.limit = limit;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFulfilled() {
        double edges = getMachine().getAllEdges().size();
        double covered = getMachine().getNumOfCoveredEdges();
        logger.debug("Edges/covered (limit): " + edges + "/" + covered + " (" + limit + ")");
        return (covered / edges) >= limit;
    }

    /** {@inheritDoc} */
    @Override
    public double getFulfilment() {
        double edges = getMachine().getAllEdges().size();
        double covered = getMachine().getNumOfCoveredEdges();
        return (covered / edges) / limit;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "EC>=" + (int) (100 * limit);
    }

}
