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
package org.graphwalker.core;

import org.graphwalker.core.event.EventSource;
import org.graphwalker.core.event.MachineSink;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.machine.ExecutionStatus;
import org.graphwalker.core.model.Action;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Vertex;
import org.graphwalker.core.script.ScriptContext;

import javax.script.ScriptException;
import java.util.*;

/**
 * @author Nils Olsson
 */
public final class SimpleMachine extends EventSource<MachineSink> implements Machine {

    private final List<ExecutionContext> executionContexts;
    private ExecutionContext currentExecutionContext;

    public SimpleMachine(ExecutionContext executionContext) {
        this(new ArrayList<>(Arrays.asList(executionContext)));
    }

    public SimpleMachine(List<ExecutionContext> executionContexts) {
        this.executionContexts = Collections.unmodifiableList(executionContexts);
    }

    @Override
    public Element getNextStep() {
        if (ExecutionStatus.NOT_EXECUTED.equals(getCurrentExecutionContext().getExecutionStatus())) {
            getCurrentExecutionContext().setExecutionStatus(ExecutionStatus.EXECUTING);
        }
        Element currentStep = getCurrentExecutionContext().getCurrentElement();
        if (currentStep instanceof Vertex) {
            Vertex vertex = (Vertex)currentStep;
            execute(vertex.getExitActions());
        }
        currentStep = getCurrentExecutionContext().getPathGenerator().getNextStep(getCurrentExecutionContext());
        if (currentStep instanceof Vertex) {
            Vertex vertex = (Vertex)currentStep;
            execute(vertex.getEntryActions());
        } else {
            Edge edge = (Edge)currentStep;
            execute(edge.getActions());
        }
        return currentStep;
    }

    private void execute(Set<Action> actions) {
        for (Action action: actions) {
            try {
                getCurrentExecutionContext().getScriptEngine().eval(action.getScript());
            } catch (ScriptException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    @Override
    public Element getCurrentStep() {
        return getCurrentExecutionContext().getCurrentElement();
    }

    @Override
    public Boolean hasNextStep() {
        boolean isFulfilled = getCurrentExecutionContext().getStopCondition().isFulfilled(getCurrentExecutionContext());
        if (isFulfilled && ExecutionStatus.EXECUTING.equals(getCurrentExecutionContext().getExecutionStatus())) {
            getCurrentExecutionContext().setExecutionStatus(ExecutionStatus.COMPLETED);
        }
        return !isFulfilled;
    }

    @Override
    public ScriptContext getScriptContext() {
        return getCurrentExecutionContext().getScriptContext();
    }

    @Override
    public void failCurrentStep() {
        getCurrentExecutionContext().setExecutionStatus(ExecutionStatus.FAILED);
    }

    @Override
    public void restart() {
        getCurrentExecutionContext().setCurrentElement(null);
    }

    @Override
    public ExecutionContext getCurrentExecutionContext() {
        if (null == currentExecutionContext) {
            currentExecutionContext = getExecutionContexts().get(0);
        }
        return currentExecutionContext;
    }

    @Override
    public List<ExecutionContext> getExecutionContexts() {
        return executionContexts;
    }
}
