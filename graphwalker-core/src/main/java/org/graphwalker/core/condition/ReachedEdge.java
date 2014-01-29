/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2013 GraphWalker
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
package org.graphwalker.core.condition;

import org.graphwalker.core.Model;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.model.Edge;

/**
 * @author Nils Olsson
 */
public final class ReachedEdge extends BaseStopCondition {

  public ReachedEdge() {
  }

  public ReachedEdge(String value) {
    super(value);
  }

  public boolean isFulfilled(ExecutionContext context) {
        return getFulfilment(context) >= FULFILLMENT_LEVEL;
    }

    public double getFulfilment(ExecutionContext context) {
        if (null == context.getCurrentElement()) {
            return 0;
        } else if (getValue().equals(context.getCurrentElement().getName())) {
            return 1;
        } else {
            double maxFulfilment = 0;
            Model model = context.getModel();
            for (Edge edge : model.getEdges(getValue())) {
                int distance = model.getShortestDistance(context.getCurrentElement(), edge);
                int max = model.getMaximumDistance(edge);
                double fulfilment = 1 - (double) distance / max;
                if (maxFulfilment < fulfilment) {
                    maxFulfilment = fulfilment;
                }
            }
            return maxFulfilment;
        }
    }
}
