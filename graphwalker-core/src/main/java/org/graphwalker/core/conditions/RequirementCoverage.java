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

import org.graphwalker.core.Util;

/**
 * <p>RequirementCoverage class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class RequirementCoverage extends AbstractStopCondition {

    private double limit;

    /**
     * <p>Constructor for RequirementCoverage.</p>
     */
    public RequirementCoverage() {
        this(1);
    }

    /**
     * <p>Constructor for RequirementCoverage.</p>
     *
     * @param limit a double.
     */
    public RequirementCoverage(double limit) {
        Util.AbortIf((limit > 1 || limit < 0), "Requirement coverage must be between 0 and 100");
        this.limit = limit;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFulfilled() {
        int stats[] = getMachine().getStatistics();
        double requirements = stats[5];
        double covered = stats[6];
        return (covered / requirements) >= limit;
    }

    /** {@inheritDoc} */
    @Override
    public double getFulfilment() {
        int stats[] = getMachine().getStatistics();
        double requirements = stats[5];
        double covered = stats[6];
        return (covered / requirements) / limit;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "RC>=" + (int) (100 * limit);
    }

}
