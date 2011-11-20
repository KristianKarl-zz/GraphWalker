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
import org.graphwalker.core.exceptions.FoundNoEdgeException;
import org.graphwalker.core.graph.Edge;
import org.graphwalker.core.machines.FiniteStateMachine;

import java.util.Set;

/**
 * <p>Abstract AbstractPathGenerator class.</p>
 */
public abstract class AbstractPathGenerator implements PathGenerator {

    private FiniteStateMachine machine;
    private StopCondition stopCondition;

    /**
     * <p>getNext.</p>
     *
     * @return an array of {@link java.lang.String} objects.
     * @throws java.lang.InterruptedException if any.
     */
    public abstract String[] getNext() throws InterruptedException;

    /**
     * <p>Constructor for AbstractPathGenerator.</p>
     */
    public AbstractPathGenerator() {
    }

    /**
     * <p>Constructor for AbstractPathGenerator.</p>
     *
     * @param stopCondition a {@link org.graphwalker.core.conditions.StopCondition} object.
     */
    public AbstractPathGenerator(StopCondition stopCondition) {
        this.stopCondition = stopCondition;
    }

    /**
     * <p>hasNext.</p>
     *
     * @return a boolean.
     */
    public boolean hasNext() {
        return !stopCondition.isFulfilled();
    }

    /**
     * <p>Getter for the field <code>machine</code>.</p>
     *
     * @return a {@link org.graphwalker.core.machines.FiniteStateMachine} object.
     */
    public FiniteStateMachine getMachine() {
        return machine;
    }

    /** {@inheritDoc} */
    public void setMachine(FiniteStateMachine machine) {
        this.machine = machine;
        if (this.stopCondition != null) {
            this.stopCondition.setMachine(machine);
        }
    }

    /** {@inheritDoc} */
    public void setStopCondition(StopCondition stopCondition) {
        this.stopCondition = stopCondition;
        if (this.machine != null) {
            this.stopCondition.setMachine(this.machine);
        }
    }

    /**
     * <p>Getter for the field <code>stopCondition</code>.</p>
     *
     * @return a {@link org.graphwalker.core.conditions.StopCondition} object.
     */
    public StopCondition getStopCondition() {
        return stopCondition;
    }

    /**
     * <p>getConditionFulfilment.</p>
     *
     * @return the condition fulfilment
     */
    public double getConditionFulfilment() {
        return stopCondition.getFulfilment();
    }

    /**
     * Will reset the generator to its initial vertex.
     */
    public void reset() {
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        if (getStopCondition() != null)
            return getStopCondition().toString();
        return "";
    }

    /** {@inheritDoc} */
    public boolean isEdgeAvailable(Edge edge) {
        Set<Edge> availableEdges;
        try {
            availableEdges = getMachine().getCurrentOutEdges();
        } catch (FoundNoEdgeException e) {
            throw new RuntimeException("No possible edges available for path", e);
        }
        return availableEdges.contains(edge);

    }
}
