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
package org.graphwalker.core.algorithm;

import org.graphwalker.core.Model;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Vertex;

import java.util.*;

/**
 * @author Nils Olsson
 */
public final class DepthFirstSearch implements Algorithm {

    private final Model model;

    public DepthFirstSearch(Model model) {
        this.model = model;
    }

    public List<Element> getConnectedComponent(Element root) {
        return createConnectedComponent(createElementStatusMap(model.getElements()), root);
    }

    private Map<Element, ElementStatus> createElementStatusMap(List<Element> elements) {
        Map<Element, ElementStatus> elementStatusMap = new HashMap<Element, ElementStatus>();
        for (Element element: elements) {
            elementStatusMap.put(element, ElementStatus.UNREACHABLE);
            if (element instanceof Edge) {
                Edge edge = (Edge)element;
                if (edge.isBlocked()) {
                    elementStatusMap.put(element, ElementStatus.BLOCKED);
                }
            }
        }
        return elementStatusMap;
    }

    private List<Element> createConnectedComponent(Map<Element, ElementStatus> elementStatusMap, Element root) {
        List<Element> connectedComponent = new ArrayList<Element>();
        Deque<Element> stack = new ArrayDeque<Element>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Element element = stack.pop();
            if (ElementStatus.UNREACHABLE.equals(elementStatusMap.get(element))) {
                connectedComponent.add(element);
                elementStatusMap.put(element, ElementStatus.REACHABLE);
                if (element instanceof Vertex) {
                    Vertex vertex = (Vertex)element;
                    for (Edge edge: model.getEdges(vertex)) {
                        stack.push(edge);
                    }
                } else if (element instanceof Edge) {
                    Edge edge = (Edge)element;
                    stack.push(edge.getTargetVertex());
                }
            }
        }
        return Collections.unmodifiableList(connectedComponent);
    }

    private enum ElementStatus {
        UNREACHABLE, REACHABLE, BLOCKED
    }
}
