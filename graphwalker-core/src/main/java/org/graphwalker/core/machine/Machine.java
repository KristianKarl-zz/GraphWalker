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

import org.graphwalker.core.annotations.*;
import org.graphwalker.core.common.AnnotationUtils;
import org.graphwalker.core.common.ReflectionUtils;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelElement;
import org.graphwalker.core.model.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * <p>MachineImpl class.</p>
 */
public final class Machine implements Runnable {

    private final Set<ExecutionContext> executionContexts;
    private ExecutionContext executionContext;

    /**
     * <p>Constructor for Machine.</p>
     *
     * @param executionContexts a {@link java.util.List} object.
     */
    public Machine(Set<ExecutionContext> executionContexts) {
        this.executionContexts = Collections.unmodifiableSet(executionContexts);
    }

    public void run() {
        try {
        AnnotationUtils.execute(BeforeExecution.class, getExecutionContext());
        while (hasMoreSteps()) {
            ModelElement element = getNextStep();
            AnnotationUtils.execute(BeforeElement.class, getExecutionContext());
            getExecutionContext().getExecutionProfiler().start(element);
            ReflectionUtils.execute(getExecutionContext().getImplementation(), element.getName(), getExecutionContext().getEdgeFilter().getScriptContext());
            getExecutionContext().getExecutionProfiler().stop(element);
            AnnotationUtils.execute(AfterElement.class, getExecutionContext());
        }
        AnnotationUtils.execute(AfterExecution.class, getExecutionContext());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            AnnotationUtils.execute(getExecutionContext(), throwable);
            // TODO: cast a new UnhandledException(..)

        }
    }






    /**
     * <p>Getter for the field <code>executionContext</code>.</p>
     *
     * @return a {@link ExecutionContext} object.
     */
    private ExecutionContext getExecutionContext() {
        if (null == executionContext) {
            for (ExecutionContext context: executionContexts) {
                if (ExecutionStatus.NOT_EXECUTED.equals(context.getExecutionStatus())) {
                    executionContext = context;
                    break;
                }
            }
        }
        return executionContext;
    }




    /**
     * <p>hasMoreSteps.</p>
     *
     * @return a boolean.
     */
    public boolean hasMoreSteps() {
        return !getExecutionContext().getPathGenerator().getStopCondition().isFulfilled(getExecutionContext());
    }

    /**
     * <p>getCurrentStep.</p>
     *
     * @return a {@link org.graphwalker.core.model.ModelElement} object.
     */
    public ModelElement getCurrentStep() {
        return getExecutionContext().getCurrentElement();
    }

    /**
     * <p>getNextStep.</p>
     *
     * @return a {@link org.graphwalker.core.model.ModelElement} object.
     */
    public ModelElement getNextStep() {
        ModelElement nextElement = getExecutionContext().getPathGenerator().getNextStep(getExecutionContext(), getPossibleSteps());
        getExecutionContext().setCurrentElement(nextElement);
        getExecutionContext().visit(nextElement);
        if (nextElement instanceof Edge) {
            getExecutionContext().getEdgeFilter().executeActions(getExecutionContext(), (Edge)nextElement);
        }
        return nextElement;
    }

    /**
     * <p>getPossibleSteps.</p>
     *
     * @return a {@link java.util.List} object.
     */
    private List<ModelElement> getPossibleSteps() {
        List<ModelElement> elements = new ArrayList<ModelElement>();
        if (getExecutionContext().getCurrentElement() instanceof Vertex) {
            Vertex vertex = (Vertex) getExecutionContext().getCurrentElement() ;
            Model model = getExecutionContext().getModel();
            for (Edge edge: model.getEdges(vertex)) {
                if (!edge.isBlocked() && getExecutionContext().getEdgeFilter().acceptEdge(getExecutionContext(), edge)) {
                    elements.add(edge);
                }
            }
        } else if (getExecutionContext().getCurrentElement() instanceof Edge) {
            elements.add(((Edge) getExecutionContext().getCurrentElement()).getTarget());
        }
        return elements;
    }
}
