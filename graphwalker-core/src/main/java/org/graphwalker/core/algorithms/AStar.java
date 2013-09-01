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
package org.graphwalker.core.algorithms;

import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelElement;

import java.util.*;

/**
 * @author Nils Olsson
 */
public final class AStar {

    /**
     * <p>getPath.</p>
     *
     * @param executionContext a {@link org.graphwalker.core.machine.ExecutionContext} object.
     * @param source a {@link org.graphwalker.core.model.ModelElement} object.
     * @param target a {@link org.graphwalker.core.model.ModelElement} object.
     * @return a {@link java.util.List} object.
     */
    public List<ModelElement> getPath(ExecutionContext executionContext, ModelElement source, ModelElement target) {
        Map<ModelElement, AStarNode> openSet = new HashMap<ModelElement, AStarNode>();
        PriorityQueue<AStarNode> queue = new PriorityQueue<AStarNode>(10, new AStarNodeComparator());
        Map<ModelElement, AStarNode> closeSet = new HashMap<ModelElement, AStarNode>();
        Model model = executionContext.getModel();
        AStarNode sourceNode = new AStarNode(source, 0, model.getShortestDistance(source, target));
        openSet.put(source, sourceNode);
        queue.add(sourceNode);
        AStarNode targetNode = null;
        while(openSet.size() > 0) {
            AStarNode node = queue.poll();
            openSet.remove(node.getElement());
            if(node.getElement().equals(target)){
                targetNode = node;
                break;
            }else{
                closeSet.put(node.getElement(), node);
                List<ModelElement> neighbors = model.getModelElements(node.getElement());
                for (ModelElement neighbor : neighbors) {
                    AStarNode visited = closeSet.get(neighbor);
                    if (visited == null) {
                        double g = node.getG() + model.getShortestDistance(node.getElement(), neighbor);
                        AStarNode neighborNode = openSet.get(neighbor);
                        if (null == neighborNode) {
                            neighborNode = new AStarNode(neighbor, g, model.getShortestDistance(neighbor, target));
                            neighborNode.setParent(node);
                            openSet.put(neighbor, neighborNode);
                            queue.add(neighborNode);
                        } else if (g < neighborNode.getG()) {
                            neighborNode.setParent(node);
                            neighborNode.setG(g);
                            neighborNode.setH(model.getShortestDistance(neighbor, target));
                        }
                    }
                }
            }
        }
        if (null != targetNode) {
            List<ModelElement> path = new ArrayList<ModelElement>();
            path.add(targetNode.getElement());
            AStarNode node = targetNode.getParent();
            while(null != node) {
                path.add(node.getElement());
                node = node.getParent();
            }
            Collections.reverse(path);
            return path;
        }
        throw new NoPathFoundException();
    }

    private class AStarNode {

        private final ModelElement element;
        private AStarNode parent;
        private double g;
        private double h;

        AStarNode(ModelElement element, double g, double h) {
            this.element = element;
            this.g = g;
            this.h = h;
        }

        private ModelElement getElement() {
            return element;
        }

        private AStarNode getParent() {
            return parent;
        }

        private void setParent(AStarNode parent) {
            this.parent = parent;
        }

        private double getG() {
            return g;
        }

        private void setG(double g) {
            this.g = g;
        }

        private double getH() {
            return h;
        }

        private void setH(double h) {
            this.h = h;
        }

        public double getF() {
            return g+h;
        }
    }

    private class AStarNodeComparator implements Comparator<AStarNode> {

        public int compare(AStarNode first, AStarNode second) {
            if (first.getF() < second.getF()){
                return -1;
            } else if(first.getF() > second.getF()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
