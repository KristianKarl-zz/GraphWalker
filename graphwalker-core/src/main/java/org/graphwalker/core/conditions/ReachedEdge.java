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

import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Model;

/**
 * <p>ReachedEdge class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class ReachedEdge implements StopCondition {

    private final String myName;

    /**
     * <p>Constructor for ReachedEdge.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public ReachedEdge(String name) {
        myName = name;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFulfilled(Model model, Element element) {
        return getFulfilment(model, element) >= FULFILLMENT_LEVEL;
    }

    /** {@inheritDoc} */
    @Override
    public double getFulfilment(Model model, Element element) {
        Edge edge = model.getEdgeByName(myName);
        if (null != edge) {
            if (edge.equals(element)) {
                return 1;
            } else {
                int distance = model.getShortestDistance(element, edge);
                int max = model.getMaximumDistance(edge);
                return 1 - (double)distance/max;
            }
        }
        return 0;
    }

}
