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

package org.graphwalker.core.statistics;

import org.graphwalker.core.graph.AbstractElement;
import org.graphwalker.core.graph.Graph;
import org.graphwalker.core.graph.Vertex;

import java.util.HashSet;

/**
 * <p>VertexCoverageStatistics class.</p>
 *
 * @author Johan Tejle
 * @version $Id: $
 */
public class VertexCoverageStatistics extends AbstractStatistics {

    private int max;
    private HashSet<String> usedVertices;

    /**
     * <p>Constructor for VertexCoverageStatistics.</p>
     *
     * @param model a {@link org.graphwalker.core.graph.Graph} object.
     */
    public VertexCoverageStatistics(Graph model) {
        max = model.getVertices().size();
        usedVertices = new HashSet<String>();
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * org.graphwalker.statistics.Statistics#addProgress(edu.uci.ics.jung.graph
      * .impl.AbstractElement)
      */
    /** {@inheritDoc} */
    @Override
    public void addProgress(AbstractElement element) {
        if (element instanceof Vertex) {
            usedVertices.add(element.toString());
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see org.graphwalker.statistics.Statistics#getCurrent()
      */
    /** {@inheritDoc} */
    @Override
    public int getCurrent() {
        return usedVertices.size();
    }

    /*
      * (non-Javadoc)
      *
      * @see org.graphwalker.statistics.Statistics#getMax()
      */
    /** {@inheritDoc} */
    @Override
    public int getMax() {
        return max;
    }

}
