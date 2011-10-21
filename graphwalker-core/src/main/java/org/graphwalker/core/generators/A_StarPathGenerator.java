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

import org.graphwalker.core.Util;
import org.graphwalker.core.conditions.StopCondition;
import org.graphwalker.core.exceptions.FoundNoEdgeException;
import org.graphwalker.core.graph.Edge;
import org.graphwalker.core.graph.Vertex;
import org.graphwalker.core.machines.FiniteStateMachine;

import java.util.*;

/**
 * <p>A_StarPathGenerator class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class A_StarPathGenerator extends AbstractPathGenerator {

    /**
     * <p>Constructor for A_StarPathGenerator.</p>
     *
     * @param stopCondition a {@link org.graphwalker.core.conditions.StopCondition} object.
     */
    public A_StarPathGenerator(StopCondition stopCondition) {
        super(stopCondition);
    }

    private Stack<Edge> preCalculatedPath = null;
    private Vertex lastVertex;

    /** {@inheritDoc} */
    @Override
    public void setMachine(FiniteStateMachine machine) {
        super.setMachine(machine);
    }

    /**
     * <p>Constructor for A_StarPathGenerator.</p>
     */
    public A_StarPathGenerator() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public String[] getNext() throws InterruptedException {
        Util.AbortIf(!hasNext(), "Finished");
        if (lastVertex == null || lastVertex != getMachine().getCurrentVertex() || preCalculatedPath == null || preCalculatedPath.size() == 0) {
            boolean oldCalculatingPathValue = getMachine().isCalculatingPath();
            getMachine().setCalculatingPath(true);

            preCalculatedPath = a_star();

            getMachine().setCalculatingPath(oldCalculatingPathValue);

            if (preCalculatedPath == null) {
                throw new RuntimeException("No path found to " + this.getStopCondition());
            }

            // reverse path
            Stack<Edge> temp = new Stack<Edge>();
            while (preCalculatedPath.size() > 0) {
                temp.push(preCalculatedPath.pop());
            }
            preCalculatedPath = temp;
        }

        Edge edge = preCalculatedPath.pop();
        getMachine().walkEdge(edge);
        lastVertex = getMachine().getCurrentVertex();
        String[] retur = {getMachine().getEdgeName(edge), getMachine().getCurrentVertexName()};
        return retur;
    }

    @SuppressWarnings("unchecked")
    private Stack<Edge> a_star() throws InterruptedException {
        Vector<String> closed = new Vector<String>();

        PriorityQueue<WeightedPath> a_starPath = new PriorityQueue<WeightedPath>(10, new Comparator<WeightedPath>() {
            @Override
            public int compare(WeightedPath arg0, WeightedPath arg1) {
                int retur = Double.compare(arg0.getWeight(), arg1.getWeight());
                if (retur == 0)
                    retur = arg0.getPath().size() - arg1.getPath().size();
                return retur;
            }
        });

        Set<Edge> availableOutEdges;
        try {
            availableOutEdges = getMachine().getCurrentOutEdges();
        } catch (FoundNoEdgeException e) {
            throw new RuntimeException("No available edges found at " + getMachine().getCurrentVertexName(), e);
        }
        for (Edge edge : availableOutEdges) {
            Stack<Edge> path = new Stack<Edge>();
            path.push(edge);
            a_starPath.add(getWeightedPath(path));
        }
        double maxWeight = 0;
        while (a_starPath.size() > 0) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            WeightedPath path = a_starPath.poll();
            if (path.getWeight() > maxWeight)
                maxWeight = path.getWeight();
            if (path.getWeight() > 0.99999) // are we done yet?
                return path.getPath();

            Edge possibleDuplicate = path.getPath().peek();

            // have we been here before?
            if (closed.contains(possibleDuplicate.hashCode() + "." + path.getSubState().hashCode() + "." + path.getWeight()))
                continue; // ignore this and move on

            // We don't want to use this edge again as this path is
            // the fastest, and if we come here again we have used more
            // steps to get here than we used this time.
            closed.add(possibleDuplicate.hashCode() + "." + path.getSubState().hashCode() + "." + path.getWeight());

            availableOutEdges = getPathOutEdges(path.getPath());
            if (availableOutEdges != null && availableOutEdges.size() > 0) {
                for (Edge edge : availableOutEdges) {
                    Stack<Edge> newStack = (Stack<Edge>) path.getPath().clone();
                    newStack.push(edge);
                    a_starPath.add(getWeightedPath(newStack));
                }
            }
        }
        throw new RuntimeException("No path found to satisfy stop condition " + getStopCondition() + ", best path satified only "
                + (int) (maxWeight * 100) + "% of condition.");
    }

    private WeightedPath getWeightedPath(Stack<Edge> path) {
        double weight = 0;
        String subState = "";

        getMachine().storeVertex();
        getMachine().walkPath(path);
        weight = getConditionFulfilment();
        String currentState = getMachine().getCurrentVertexName();
        if (currentState.contains("/")) {
            subState = currentState.split("/", 2)[1];
        }
        getMachine().restoreVertex();

        return new WeightedPath(path, weight, subState);
    }

    private Set<Edge> getPathOutEdges(Stack<Edge> path) {
        Set<Edge> retur = null;
        getMachine().storeVertex();
        getMachine().walkPath(path);
        try {
            retur = getMachine().getCurrentOutEdges();
        } catch (FoundNoEdgeException e) {
            // no edges found? degrade gracefully and return the default value of
            // null.
        }
        getMachine().restoreVertex();
        return retur;
    }

    /**
     * {@inheritDoc}
     *
     * Will reset the generator to its initial vertex.
     */
    @Override
    public void reset() {
        preCalculatedPath = null;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "A_STAR{" + super.toString() + "}";
    }

    private static class WeightedPath {
        private double weight;
        private Stack<Edge> path;
        private String subState;

        public String getSubState() {
            return subState;
        }

        public void setSubState(String subState) {
            this.subState = subState;
        }

        public Stack<Edge> getPath() {
            return path;
        }

        public void setPath(Stack<Edge> path) {
            this.path = path;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public WeightedPath(Stack<Edge> path, double weight, String subState) {
            setPath(path);
            setWeight(weight);
            setSubState(subState);
        }
    }
}
