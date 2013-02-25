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
package org.graphwalker.core.algorithms;

import org.graphwalker.core.model.*;
import org.graphwalker.core.model.status.ElementStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>FloydWarshall class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class FloydWarshall implements Algorithm {

    private final Model myModel;
    private List<Element> myModelElements;
    private int[][] myDistances;
    private Element[][] myPredecessors;

    /**
     * <p>Constructor for FloydWarshall.</p>
     *
     * @param model a {@link org.graphwalker.core.model.Model} object.
     */
    public FloydWarshall(Model model) {
        myModel = model;
    }

    /**
     * <p>calculate.</p>
     */
    public void calculate() {
        myModelElements = myModel.getConnectedComponent();
        myDistances = createDistanceMatrix(myModel);
        myPredecessors = createPredecessorMatrix(myModel);
        updateMatrices(myModel);
    }

    private int[][] createDistanceMatrix(Model model) {
        List<Element> elements = model.getConnectedComponent();
        int[][] distances = new int[elements.size()][elements.size()];
        for (int[] row : distances) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        for (Element element : elements) {
            if (element instanceof Edge) {
                Edge edge = (Edge) element;
                Vertex target = edge.getTarget();

                distances[elements.indexOf(edge)][elements.indexOf(target)] = (int) Math.round(100 * edge.getWeight());
            } else if (element instanceof Vertex) {
                Vertex vertex = (Vertex) element;
                for (Edge edge : vertex.getEdges()) {
                    if (!ElementStatus.BLOCKED.equals(edge.getStatus())) {
                        distances[elements.indexOf(vertex)][elements.indexOf(edge)] = 1;
                    }
                }
            }
        }
        return distances;
    }

    private Element[][] createPredecessorMatrix(Model model) {
        return new Element[model.getConnectedComponent().size()][model.getConnectedComponent().size()];
    }

    private void updateMatrices(Model model) {
        int size = model.getConnectedComponent().size();
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (myDistances[i][k] != Integer.MAX_VALUE
                            && myDistances[k][j] != Integer.MAX_VALUE
                            && myDistances[i][k] + myDistances[k][j] < myDistances[i][j]) {
                        myDistances[i][j] = myDistances[i][k] + myDistances[k][j];
                        myPredecessors[i][j] = model.getConnectedComponent().get(k);
                    }
                }
            }
        }
    }

    /**
     * <p>getShortestDistance.</p>
     *
     * @param source a {@link org.graphwalker.core.model.Element} object.
     * @param target a {@link org.graphwalker.core.model.Element} object.
     * @return a int.
     */
    public int getShortestDistance(final Element source, final Element target) {
        return myDistances[myModelElements.indexOf(source)][myModelElements.indexOf(target)];
    }

    /**
     * <p>getMaximumDistance.</p>
     *
     * @param target a {@link org.graphwalker.core.model.Element} object.
     * @return a int.
     */
    public int getMaximumDistance(Element target) {
        int maximumDistance = Integer.MIN_VALUE;
        for (int[] distance : myDistances) {
            int value = distance[myModelElements.indexOf(target)];
            if (value != Integer.MAX_VALUE && value > maximumDistance) {
                maximumDistance = value;
            }
        }
        return maximumDistance;
    }

    /**
     * <p>getShortestPath.</p>
     *
     * @param source a {@link org.graphwalker.core.model.Element} object.
     * @param target a {@link org.graphwalker.core.model.Element} object.
     * @return a {@link java.util.List} object.
     */
    public List<Element> getShortestPath(final Element source, final Element target) {
        if (myDistances[myModelElements.indexOf(source)][myModelElements.indexOf(target)] == Integer.MAX_VALUE) {
            return new ArrayList<Element>();
        }
        final List<Element> path = getIntermediatePath(source, target);
        path.add(0, source);
        path.add(target);
        return path;
    }

    private List<Element> getIntermediatePath(final Element source, final Element target) {
        if (myPredecessors[myModelElements.indexOf(source)][myModelElements.indexOf(target)] == null) {
            return new ArrayList<Element>();
        }
        final List<Element> path = new ArrayList<Element>();
        path.addAll(getIntermediatePath(source, myPredecessors[myModelElements.indexOf(source)][myModelElements.indexOf(target)]));
        path.add(myPredecessors[myModelElements.indexOf(source)][myModelElements.indexOf(target)]);
        path.addAll(getIntermediatePath(myPredecessors[myModelElements.indexOf(source)][myModelElements.indexOf(target)], target));
        return path;
    }

}
