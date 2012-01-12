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
import org.graphwalker.core.util.Resource;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * <p>EdgeFilterImpl class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class EdgeFilterImpl implements EdgeFilter {

    private final ScriptEngine myScriptEngine;

    /**
     * <p>Constructor for EdgeFilterImpl.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public EdgeFilterImpl(String name) {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        myScriptEngine = scriptEngineManager.getEngineByName(name);
    }

    /** {@inheritDoc} */
    public void executeActions(Edge edge) {
        if (null != edge.getEdgeActions() && 0<edge.getEdgeActions().size()) {
            for (Action action: edge.getEdgeActions()) {
                try {
                    myScriptEngine.eval(action.getScript());
                } catch (ScriptException e) {
                    throw new EdgeFilterException(e);
                }
            }
        }
    }

    /** {@inheritDoc} */
    public boolean acceptEdge(Edge edge) {
        if (null != edge.getEdgeGuard() && !edge.getEdgeGuard().getScript().trim().isEmpty()) {
            try {
                return (Boolean)myScriptEngine.eval(edge.getEdgeGuard().getScript());
            } catch (ScriptException e) {
                throw new EdgeFilterException(Resource.getText(Bundle.NAME, "exception.script.error"));
            } catch (NullPointerException e) {
                throw new EdgeFilterException(Resource.getText(Bundle.NAME, "exception.script.engine.missing"));
            }
        }
        return true;
    }
}
