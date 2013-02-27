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

import org.graphwalker.core.model.Edge;

import java.util.List;

/**
 * <p>EdgeStatistics class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class EdgeStatistics {
    
    private long edgeCount = 0;
    private long visitedEdgeCount = 0;
    private long blockedEdgeCount = 0;
    private long unreachableEdgeCount = 0;
    //private long 
    
    /**
     * <p>Constructor for EdgeStatistics.</p>
     *
     * @param edges a {@link java.util.List} object.
     */
    public EdgeStatistics(List<Edge> edges) {
        for (Edge edge: edges) {
            edgeCount++;
            switch (edge.getStatus()) {
                case UNREACHABLE: unreachableEdgeCount++; break;
                case VISITED: visitedEdgeCount++; break;
                case BLOCKED: blockedEdgeCount++; break;
            }
        }
    }

    /**
     * <p>getEdgeCount.</p>
     *
     * @return a long.
     */
    public long getEdgeCount() {
        return edgeCount;
    }

    /**
     * <p>getVisitedEdgeCount.</p>
     *
     * @return a long.
     */
    public long getVisitedEdgeCount() {
        return visitedEdgeCount;
    }

    /**
     * <p>getBlockedEdgeCount.</p>
     *
     * @return a long.
     */
    public long getBlockedEdgeCount() {
        return blockedEdgeCount;
    }

    /**
     * <p>getUnreachableEdgeCount.</p>
     *
     * @return a long.
     */
    public long getUnreachableEdgeCount() {
        return unreachableEdgeCount;
    }
}
