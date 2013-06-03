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
package org.graphwalker.core.machine;

import org.graphwalker.core.Bundle;
import org.graphwalker.core.filter.EdgeFilter;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelElement;
import org.graphwalker.core.model.Vertex;
import org.graphwalker.core.utils.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>MachineImpl class.</p>
 */
public final class Machine {

    private final List<Context> contexts;
    private Context currentContext;

    /**
     * <p>Constructor for Machine.</p>
     *
     * @param contexts a {@link java.util.List} object.
     */
    public Machine(List<Context> contexts) {
        this.contexts = Collections.unmodifiableList(contexts);
    }

    /**
     * <p>Setter for the field <code>currentContext</code>.</p>
     *
     * @param context a {@link Context} object.
     */
    public void setCurrentContext(Context context) {
        if (!contexts.contains(context)) {
            throw new MachineException(Resource.getText(Bundle.NAME, "exception.context.unknown"));
        }
        this.currentContext = context;
    }

    /**
     * <p>Getter for the field <code>currentContext</code>.</p>
     *
     * @return a {@link Context} object.
     */
    public Context getCurrentContext() {
        return currentContext;
    }

    /**
     * <p>hasMoreSteps.</p>
     *
     * @return a boolean.
     */
    public boolean hasMoreSteps() {
        return !currentContext.getPathGenerator().getStopCondition().isFulfilled(currentContext);
    }

    /**
     * <p>getCurrentStep.</p>
     *
     * @return a {@link org.graphwalker.core.model.ModelElement} object.
     */
    public ModelElement getCurrentStep() {
        return currentContext.getCurrentElement();
    }

    /**
     * <p>getNextStep.</p>
     *
     * @return a {@link org.graphwalker.core.model.ModelElement} object.
     */
    public ModelElement getNextStep() {
        ModelElement nextElement = currentContext.getPathGenerator().getNextStep(currentContext, getPossibleSteps());
        currentContext.setCurrentElement(nextElement);
        currentContext.visit(nextElement);
        if (nextElement instanceof Edge) {
            currentContext.getEdgeFilter().executeActions(currentContext, (Edge)nextElement);
        }
        return nextElement;
    }

    /**
     * <p>getPossibleSteps.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<ModelElement> getPossibleSteps() {
        List<ModelElement> elements = new ArrayList<ModelElement>();
        if (currentContext.getCurrentElement() instanceof Vertex) {
            Vertex vertex = (Vertex)currentContext.getCurrentElement() ;
            EdgeFilter edgeFilter = currentContext.getEdgeFilter();
            Model model = currentContext.getModel();
            for (Edge edge: model.getEdges(vertex)) {
                if (!edge.isBlocked() && edgeFilter.acceptEdge(currentContext, edge)) {
                    elements.add(edge);
                }
            }
        } else if (currentContext.getCurrentElement() instanceof Edge) {
            elements.add(((Edge)currentContext.getCurrentElement()).getTarget());
        }
        return elements;
    }
}
