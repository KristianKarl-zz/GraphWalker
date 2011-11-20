/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 GraphWalker
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

package org.graphwalker.core.filters;

import bsh.EvalError;
import bsh.Interpreter;
import org.graphwalker.core.graph.Edge;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * <p>AccessableEdgeFilter class.</p>
 */
public class AccessableEdgeFilter {

    private ScriptEngine jsEngine = null;
    private Interpreter beanShellEngine = null;

    /**
     * <p>Constructor for AccessableEdgeFilter.</p>
     *
     * @param sciptEngine a {@link javax.script.ScriptEngine} object.
     */
    public AccessableEdgeFilter(ScriptEngine sciptEngine) {
        this.jsEngine = sciptEngine;
    }

    /**
     * <p>Constructor for AccessableEdgeFilter.</p>
     *
     * @param beanShellEngine a {@link bsh.Interpreter} object.
     */
    public AccessableEdgeFilter(Interpreter beanShellEngine) {
        this.beanShellEngine = beanShellEngine;
    }

    /**
     * <p>acceptEdge.</p>
     *
     * @param graph a {@link org.graphwalker.core.graph.Graph} object.
     * @param edge a {@link org.graphwalker.core.graph.Edge} object.
     * @return a boolean.
     */
    public boolean acceptEdge(org.graphwalker.core.graph.Graph graph, Edge edge) {
        if (edge.getGuardKey().isEmpty()) {
            return true;
        }

        if (jsEngine != null) {
            try {
                return (Boolean) jsEngine.eval(edge.getGuardKey());
            } catch (ScriptException e) {
                throw new RuntimeException("Malformed Edge guard\n\t" + edge + "\n\tGuard: " + edge.getGuardKey()
                        + "\n\tBeanShell error message: '" + e.getMessage() + "'");
            }
        } else if (beanShellEngine != null) {
            try {
                return (Boolean) beanShellEngine.eval(edge.getGuardKey());
            } catch (EvalError e) {
                throw new RuntimeException("Malformed Edge guard\n\t" + edge + "\n\tGuard: " + edge.getGuardKey()
                        + "\n\tBeanShell error message: '" + e.getMessage() + "'");
            }
        }
        return false;
    }

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return "AccessableEdgeFilter";
    }

}
