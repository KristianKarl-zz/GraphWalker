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

import org.graphwalker.core.model.Vertex;

import java.util.List;

/**
 * <p>VertexStatistics class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class VertexStatistics {

    private long vertexCount = 0;
    private long visitedVertexCount = 0;
    private long blockedVertexCount = 0;
    private long unreachableVertexCount = 0;

    /**
     * <p>Constructor for VertexStatistics.</p>
     *
     * @param vertices a {@link java.util.List} object.
     */
    public VertexStatistics(List<Vertex> vertices) {
        for (Vertex vertex: vertices) {
            vertexCount++;
            switch (vertex.getStatus()) {
                case UNREACHABLE: unreachableVertexCount++; break;
                case VISITED: visitedVertexCount++; break;
                case BLOCKED: blockedVertexCount++; break;
            }
        }
    }

    /**
     * <p>getVertexCount.</p>
     *
     * @return a long.
     */
    public long getVertexCount() {
        return vertexCount;
    }

    /**
     * <p>getVisitedVertexCount.</p>
     *
     * @return a long.
     */
    public long getVisitedVertexCount() {
        return visitedVertexCount;
    }

    /**
     * <p>getBlockedVertexCount.</p>
     *
     * @return a long.
     */
    public long getBlockedVertexCount() {
        return blockedVertexCount;
    }

    /**
     * <p>getUnreachableVertexCount.</p>
     *
     * @return a long.
     */
    public long getUnreachableVertexCount() {
        return unreachableVertexCount;
    }
}
