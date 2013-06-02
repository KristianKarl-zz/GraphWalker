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
import org.graphwalker.core.model.status.ElementStatus;

import java.util.*;

/**
 * <p>DepthFirstSearch class.</p>
 */
public final class DepthFirstSearch {

    private final List<ModelElement> connectedComponent;

    /**
     * <p>Constructor for DepthFirstSearch.</p>
     *
     * @param model a {@link org.graphwalker.core.model.Model} object.
     */
    public DepthFirstSearch(Model model) {
        Map<ModelElement, ElementStatus> elementStatus = new HashMap<ModelElement, ElementStatus>();
        for (ModelElement element: model.getModelElements()) {
            elementStatus.put(element, ElementStatus.UNREACHABLE);
            if (element.isBlocked()) {
                elementStatus.put(element, ElementStatus.BLOCKED);
            } else if (element instanceof Edge) {
                Edge edge = (Edge)element;
                if (edge.getSource().isBlocked() || edge.getTarget().isBlocked()) {
                    elementStatus.put(element, ElementStatus.BLOCKED);
                }
            }
        }
        this.connectedComponent = Collections.unmodifiableList(createConnectedComponent(model, elementStatus, model.getStartVertex()));
    }

    private List<ModelElement> createConnectedComponent(Model model, Map<ModelElement, ElementStatus> elementStatus, ModelElement root) {
        List<ModelElement> connectedComponent = new ArrayList<ModelElement>();
        if (null != root) {
            Deque<ModelElement> stack = new ArrayDeque<ModelElement>();
            stack.push(root);
            while (!stack.isEmpty()) {
                ModelElement element = stack.pop();
                if (ElementStatus.UNREACHABLE.equals(elementStatus.get(element))) {
                    connectedComponent.add(element);
                    elementStatus.put(element, ElementStatus.REACHABLE);
                    if (element instanceof Vertex) {
                        Vertex vertex = (Vertex)element;
                        for (Edge edge: model.getEdges(vertex)) {
                            stack.push(edge);
                        }
                    } else if (element instanceof Edge) {
                        stack.push(((Edge) element).getTarget());
                    }
                }
            }
        }
        return connectedComponent;
    }

    /**
     * <p>getConnectedComponent.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<ModelElement> getConnectedComponent() {
        return connectedComponent;
    }

}
