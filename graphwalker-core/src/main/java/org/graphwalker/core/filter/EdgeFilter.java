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

import org.graphwalker.core.Bundle;
import org.graphwalker.core.model.Action;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.support.ModelContext;
import org.graphwalker.core.utils.Resource;

import javax.script.*;

/**
 * <p>EdgeFilterImpl class.</p>
 */
public final class EdgeFilter {

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
     * <p>executeActions.</p>
     *
     * @param context a {@link org.graphwalker.core.model.support.ModelContext} object.
     * @param edge a {@link org.graphwalker.core.model.Edge} object.
     */
    public void executeActions(ModelContext context, Edge edge) {
        if (null == scriptEngine) {
            throw new EdgeFilterException(Resource.getText(Bundle.NAME, "exception.script.engine.missing", scriptEngineName));
        }
        if (null != edge.getActions() && 0 < edge.getActions().size()) {
            for (Action action: edge.getActions()) {
                try {
                    scriptEngine.eval(action.getScript());
                } catch (ScriptException e) {
                    throw new EdgeFilterException(Resource.getText(Bundle.NAME, "exception.script.error", e.getMessage()));
                }
            }
        }
    }

    /**
     * <p>acceptEdge.</p>
     *
     * @param context a {@link org.graphwalker.core.model.support.ModelContext} object.
     * @param edge a {@link org.graphwalker.core.model.Edge} object.
     * @return a boolean.
     */
    public boolean acceptEdge(ModelContext context, Edge edge) {
        if (null == scriptEngine) {
            throw new EdgeFilterException(Resource.getText(Bundle.NAME, "exception.script.engine.missing", scriptEngineName));
        }
        boolean isEdgeAccepted = true;
        if (edge.hasGuard()) {
            try {
                isEdgeAccepted = (Boolean) scriptEngine.eval(edge.getGuard().getScript());
            } catch (ScriptException e) {
                throw new EdgeFilterException(Resource.getText(Bundle.NAME, "exception.script.error", e.getMessage()));
            }
        }
        return isEdgeAccepted;
    }

    /**
     * <p>getDataValue.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param type a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a T object.
     */
    @SuppressWarnings("unchecked")
    public <T> T getDataValue(String name, Class<T> type) {
        Bindings bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        return type.cast(bindings.get(name));
    }
}
