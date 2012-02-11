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
package org.graphwalker.core.statistics;

import org.graphwalker.core.model.Requirement;

import java.util.List;

/**
 * <p>RequirementStatistics class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class RequirementStatistics {

    private long myRequirementCount = 0;
    private long myPassedRequirementCount = 0;
    private long myFailedRequirementCount = 0;
    private long myNotCoveredRequirementCount = 0;

    /**
     * <p>Constructor for RequirementStatistics.</p>
     *
     * @param requirements a {@link java.util.List} object.
     */
    public RequirementStatistics(List<Requirement> requirements) {
        for (Requirement requirement: requirements) {
            myRequirementCount++;
            switch (requirement.getStatus()) {
                case NOT_COVERED: myNotCoveredRequirementCount++; break;
                case PASSED: myPassedRequirementCount++; break;
                case FAILED: myFailedRequirementCount++; break;
            }
        }
    }

    /**
     * <p>getRequirementCount.</p>
     *
     * @return a long.
     */
    public long getRequirementCount() {
        return myRequirementCount;
    }

    /**
     * <p>getPassedRequirementCount.</p>
     *
     * @return a long.
     */
    public long getPassedRequirementCount() {
        return myPassedRequirementCount;
    }

    /**
     * <p>getFailedRequirementCount.</p>
     *
     * @return a long.
     */
    public long getFailedRequirementCount() {
        return myFailedRequirementCount;
    }

    /**
     * <p>getNotCoveredRequirementCount.</p>
     *
     * @return a long.
     */
    public long getNotCoveredRequirementCount() {
        return myNotCoveredRequirementCount;
    }
}
