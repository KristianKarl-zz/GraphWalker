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
package org.graphwalker.core.filter;

import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Model;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * <p>EdgeFilterImpl class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class EdgeFilter {

    private final ScriptEngine scriptEngine;
    private final String scriptEngineName;

    /**
     * <p>Constructor for EdgeFilterImpl.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public EdgeFilter(String name) {
        scriptEngineName = name;
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        scriptEngine = scriptEngineManager.getEngineByName(scriptEngineName);
    }

    /**
     * {@inheritDoc}
     *
     * @param model a {@link org.graphwalker.core.model.Model} object.
     * @param edge a {@link org.graphwalker.core.model.Edge} object.
     */
    public void executeActions(Model model, Edge edge) {
        /*
        if (null == scriptEngine) {
            throw new EdgeFilterException(Resource.getText(Bundle.NAME, "exception.script.engine.missing", scriptEngineName));
        }
        if (null != edge.getEdgeActions() && 0 < edge.getEdgeActions().size()) {
            addImplementationToFilter(model);
            for (Action action : edge.getEdgeActions()) {
                try {
                    scriptEngine.eval(action.getScript());
                } catch (ScriptException e) {
                    removeImplementationFromFilter();
                    throw new EdgeFilterException(Resource.getText(Bundle.NAME, "exception.script.error", e.getMessage()));
                }
            }
            removeImplementationFromFilter();
        }
        */
    }

    /**
     * {@inheritDoc}
     *
     * @param model a {@link org.graphwalker.core.model.Model} object.
     * @param edge a {@link org.graphwalker.core.model.Edge} object.
     * @return a boolean.
     */
    public boolean acceptEdge(Model model, Edge edge) {
        /*
        if (null == scriptEngine) {
            throw new EdgeFilterException(Resource.getText(Bundle.NAME, "exception.script.engine.missing", scriptEngineName));
        }
        boolean isEdgeAccepted = true;
        if (edge.hasEdgeGuard()) {
            addImplementationToFilter(model);
            try {
                isEdgeAccepted = (Boolean) scriptEngine.eval(edge.getEdgeGuard().getScript());
            } catch (ScriptException e) {
                removeImplementationFromFilter();
                throw new EdgeFilterException(Resource.getText(Bundle.NAME, "exception.script.error", e.getMessage()));
            }
            removeImplementationFromFilter();
        }
        return isEdgeAccepted;
        */
        return false;
    }
    /*
    private void addImplementationToFilter(Model model) {
        if (null != model && model.hasImplementation()) {
            scriptEngine.put(Resource.getText(Bundle.NAME, "script.implementation.name"), model.getImplementation());
        }
    }

    private void removeImplementationFromFilter() {
        if (null != scriptEngine) {
            scriptEngine.put(Resource.getText(Bundle.NAME, "script.implementation.name"), null);
        }
    }
    */
}
