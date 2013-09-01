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

import org.graphwalker.core.annotations.AfterElement;
import org.graphwalker.core.annotations.AfterExecution;
import org.graphwalker.core.annotations.BeforeElement;
import org.graphwalker.core.annotations.BeforeExecution;
import org.graphwalker.core.common.AnnotationUtils;
import org.graphwalker.core.common.ReflectionUtils;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.ModelElement;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public final class Machine implements Runnable {

    private final Set<ExecutionContext> executionContexts = new HashSet<ExecutionContext>();
    private ExecutionContext executionContext;

    public Machine(Set<Execution> executions) {
        for (Execution execution: executions) {
            executionContexts.add(new ExecutionContext(execution));
        }
    }

    public void run() {
        try {
            getExecutionContext().setExecutionStatus(ExecutionStatus.EXECUTING);
            AnnotationUtils.execute(BeforeExecution.class, getExecutionContext());
            while (hasMoreSteps()) {
                ModelElement element = getNextStep();
                AnnotationUtils.execute(BeforeElement.class, getExecutionContext());
                executeElement(getExecutionContext(), element);
                AnnotationUtils.execute(AfterElement.class, getExecutionContext());
            }
            AnnotationUtils.execute(AfterExecution.class, getExecutionContext());
        } catch (Throwable throwable) {
            getExecutionContext().setExecutionStatus(ExecutionStatus.FAILED);
            AnnotationUtils.execute(getExecutionContext(), throwable);
        }
    }

    private void executeElement(ExecutionContext context, ModelElement element) {
        context.getExecutionProfiler().start(element);
        ReflectionUtils.execute(context.getImplementation(), element.getName(), context.getEdgeFilter().getScriptContext());
        context.getExecutionProfiler().stop(element);
    }

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
        boolean isFulfilled = getExecutionContext().getPathGenerator().getStopCondition().isFulfilled(getExecutionContext());
        if (isFulfilled) {
            getExecutionContext().setExecutionStatus(ExecutionStatus.COMPLETED);
        }
        return !isFulfilled;
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
        getExecutionContext().visit(getCurrentStep());
        ModelElement nextElement = getExecutionContext().getPathGenerator().getNextStep(getExecutionContext(), getExecutionContext().getPossibleSteps());
        getExecutionContext().setCurrentElement(nextElement);
        if (nextElement instanceof Edge) {
            getExecutionContext().getEdgeFilter().executeActions(getExecutionContext(), (Edge)nextElement);
        }
        return nextElement;
    }

    public Collection<ExecutionContext> getExecutionContexts() {
        return Collections.unmodifiableCollection(executionContexts);
    }
}
