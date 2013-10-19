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
import org.graphwalker.core.model.Action;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Vertex;
import org.graphwalker.core.script.ScriptContext;

import javax.script.ScriptException;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public final class SimpleMachine extends EventSource<MachineSink> implements Machine {

    private final ExecutionContext context;

    public SimpleMachine(ExecutionContext context) {
        this.context = context;
    }

    public Element getNextStep() {
        Element currentStep = context.getCurrentElement();
        if (currentStep instanceof Vertex) {
            Vertex vertex = (Vertex)currentStep;
            execute(vertex.getExitActions());
        }
        currentStep = context.getPathGenerator().getNextStep(context);
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
                context.getScriptEngine().eval(action.getScript());
            } catch (ScriptException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public Element getCurrentStep() {
        return context.getCurrentElement();
    }

    public Boolean hasNextStep() {
        return !context.getStopCondition().isFulfilled(context);
    }

    public ScriptContext getScriptContext() {
        return context.getScriptContext();
    }

    public void restart() {
        context.setCurrentElement(null);
    }

    public ExecutionContext getExecutionContext() {
        return context;
    }

}
