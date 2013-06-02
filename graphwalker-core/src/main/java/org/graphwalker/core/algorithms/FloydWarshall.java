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

import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelElement;
import org.graphwalker.core.model.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>FloydWarshall class.</p>
 */
public final class FloydWarshall {

    private final List<ModelElement> elements;
    private final int[][] distances;
    private final ModelElement[][] predecessors;

    /**
     * <p>Constructor for FloydWarshall.</p>
     *
     * @param model a {@link org.graphwalker.core.model.Model} object.
     */
    public FloydWarshall(Model model) {
        this.elements = model.getConnectedComponent();
        this.distances = createDistanceMatrix(model, elements);
        this.predecessors = createPredecessorMatrix(elements, distances);
    }

    private int[][] createDistanceMatrix(Model model, List<ModelElement> elements) {
        int[][] distances = new int[elements.size()][elements.size()];
        for (int[] row : distances) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        for (ModelElement element : elements) {
            if (element instanceof Edge) {
                Edge edge = (Edge) element;
                Vertex target = edge.getTarget();
                distances[elements.indexOf(edge)][elements.indexOf(target)] = (int) Math.round(100 * edge.getWeight());
            } else if (element instanceof Vertex) {
                Vertex vertex = (Vertex) element;
                for (Edge edge : model.getEdges(vertex)) {
                    if (!edge.isBlocked()) {
                        distances[elements.indexOf(vertex)][elements.indexOf(edge)] = 1;
                    }
                }
            }
        }
        return distances;
    }

    private ModelElement[][] createPredecessorMatrix(List<ModelElement> elements, int[][] distances) {
        ModelElement[][] predecessors = new ModelElement[elements.size()][elements.size()];
        int size = elements.size();
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (distances[i][k] != Integer.MAX_VALUE
                            && distances[k][j] != Integer.MAX_VALUE
                            && distances[i][k] + distances[k][j] < distances[i][j]) {
                        distances[i][j] = distances[i][k] + distances[k][j];
                        predecessors[i][j] = elements.get(k);
                    }
                }
            }
        }
        return predecessors;
    }

    /**
     * <p>getShortestDistance.</p>
     *
     * @param source a {@link org.graphwalker.core.model.ModelElement} object.
     * @param target a {@link org.graphwalker.core.model.ModelElement} object.
     * @return a int.
     */
    public int getShortestDistance(final ModelElement source, final ModelElement target) {
        return distances[elements.indexOf(source)][elements.indexOf(target)];
    }

    /**
     * <p>getMaximumDistance.</p>
     *
     * @param target a {@link org.graphwalker.core.model.ModelElement} object.
     * @return a int.
     */
    public int getMaximumDistance(ModelElement target) {
        int maximumDistance = Integer.MIN_VALUE;
        for (int[] distance : distances) {
            int value = distance[elements.indexOf(target)];
            if (value != Integer.MAX_VALUE && value > maximumDistance) {
                maximumDistance = value;
            }
        }
        return maximumDistance;
    }

    /**
     * <p>getShortestPath.</p>
     *
     * @param source a {@link org.graphwalker.core.model.ModelElement} object.
     * @param target a {@link org.graphwalker.core.model.ModelElement} object.
     * @return a {@link java.util.List} object.
     */
    public List<ModelElement> getShortestPath(final ModelElement source, final ModelElement target) {
        if (distances[elements.indexOf(source)][elements.indexOf(target)] == Integer.MAX_VALUE) {
            return new ArrayList<ModelElement>();
        }
        final List<ModelElement> path = getIntermediatePath(source, target);
        path.add(0, source);
        path.add(target);
        return path;
    }

    private List<ModelElement> getIntermediatePath(final ModelElement source, final ModelElement target) {
        if (predecessors[elements.indexOf(source)][elements.indexOf(target)] == null) {
            return new ArrayList<ModelElement>();
        }
        final List<ModelElement> path = new ArrayList<ModelElement>();
        path.addAll(getIntermediatePath(source, predecessors[elements.indexOf(source)][elements.indexOf(target)]));
        path.add(predecessors[elements.indexOf(source)][elements.indexOf(target)]);
        path.addAll(getIntermediatePath(predecessors[elements.indexOf(source)][elements.indexOf(target)], target));
        return path;
    }

}
