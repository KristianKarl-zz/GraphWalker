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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * <p>DepthFirstSearch class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class DepthFirstSearch implements Algorithm {

    private final Model myModel;
    private final List<Element> myConnectedComponent = new ArrayList<Element>();

    /**
     * <p>Constructor for DepthFirstSearch.</p>
     *
     * @param model a {@link org.graphwalker.core.model.Model} object.
     */
    public DepthFirstSearch(Model model) {
        myModel = model;
    }

    /**
     * <p>calculate.</p>
     */
    public void calculate() {
        for (Element element : myModel.getElements()) {
            if (!ElementStatus.BLOCKED.equals(element.getStatus())) {
                element.setStatus(ElementStatus.UNREACHABLE);
            }
        }
        createConnectedComponent(myModel.getStartVertex());
    }

    private void createConnectedComponent(Element root) {
        Deque<Element> stack = new ArrayDeque<Element>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Element element = stack.pop();
            if (ElementStatus.UNREACHABLE.equals(element.getStatus())) {
                myConnectedComponent.add(element);
                element.setStatus(ElementStatus.VISITED);
                if (element instanceof Vertex) {
                    for (Edge edge : ((Vertex) element).getEdges()) {
                        stack.push(edge);
                    }
                } else if (element instanceof Edge) {
                    stack.push(((Edge) element).getTarget());
                }
            }
        }
    }

    /**
     * <p>getConnectedComponent.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Element> getConnectedComponent() {
        return myConnectedComponent;
    }

}
