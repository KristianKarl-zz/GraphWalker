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
//This file is part of the GraphWalker java package
//The MIT License
//
//Copyright (c) 2010 graphwalker.org
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

package org.graphwalker.filters;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.graphwalker.graph.Edge;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * @author Johan Tejle
 * 
 */
public class AccessableEdgeFilter {

	private ScriptEngine jsEngine = null;
	private Interpreter beanShellEngine = null;

	public AccessableEdgeFilter(ScriptEngine sciptEngine) {
		this.jsEngine = sciptEngine;
	}

	public AccessableEdgeFilter(Interpreter beanShellEngine) {
		this.beanShellEngine = beanShellEngine;
	}

	public boolean acceptEdge(org.graphwalker.graph.Graph graph, Edge edge) {
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

	public String getName() {
		return "AccessableEdgeFilter";
	}

}
