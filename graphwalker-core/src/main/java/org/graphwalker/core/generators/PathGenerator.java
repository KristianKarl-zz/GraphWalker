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

package org.graphwalker.core.generators;

import org.graphwalker.core.conditions.StopCondition;
import org.graphwalker.core.graph.Edge;
import org.graphwalker.core.machines.FiniteStateMachine;

/**
 * <p>PathGenerator interface.</p>
 */
public interface PathGenerator {

    /**
     * <p>getNext.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     * @throws java.lang.InterruptedException if any.
     */
    String[] getNext() throws InterruptedException;

    /**
     * <p>hasNext.</p>
     *
     * @return a boolean.
     */
    boolean hasNext();

    /**
     * <p>getMachine.</p>
     *
     * @return a {@link org.graphwalker.core.machines.FiniteStateMachine} object.
     */
    FiniteStateMachine getMachine();

    /**
     * <p>setMachine.</p>
     *
     * @param machine a {@link org.graphwalker.core.machines.FiniteStateMachine} object.
     */
    void setMachine(FiniteStateMachine machine);

    /**
     * <p>setStopCondition.</p>
     *
     * @param stopCondition a {@link org.graphwalker.core.conditions.StopCondition} object.
     */
    void setStopCondition(StopCondition stopCondition);

    /**
     * <p>getStopCondition.</p>
     *
     * @return a {@link org.graphwalker.core.conditions.StopCondition} object.
     */
    StopCondition getStopCondition();

    /**
     * <p>getConditionFulfilment.</p>
     *
     * @return the condition fulfilment
     */
    double getConditionFulfilment();

    /**
     * Will reset the generator to its initial vertex.
     */
    void reset();

    /**
     * <p>isEdgeAvailable.</p>
     *
     * @param edge a {@link org.graphwalker.core.graph.Edge} object.
     * @return a boolean.
     */
    boolean isEdgeAvailable(Edge edge);
}
