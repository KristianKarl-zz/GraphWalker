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

package org.graphwalker.core.machines;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.graphwalker.core.Util;
import org.graphwalker.core.exceptions.FoundNoEdgeException;
import org.graphwalker.core.exceptions.InvalidDataException;
import org.graphwalker.core.filters.AccessableEdgeFilter;
import org.graphwalker.core.graph.Edge;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.Primitive;
import bsh.UtilEvalError;

public class ExtendedFiniteStateMachine extends FiniteStateMachine {

	private PrintStream Void;

	private static Logger logger = Util.setupLogger(ExtendedFiniteStateMachine.class);

	private Interpreter beanShellEngine = null;
	private AccessableEdgeFilter accessableFilter;

	private ScriptEngineManager mgr = null;
	private ScriptEngine jsEngine = null;

	private Stack<CannedNameSpace> namespaceStack;

	private PrintStream oldPrintStream;

	public ExtendedFiniteStateMachine(boolean usingJsEngine) {
		super();
		namespaceStack = new Stack<CannedNameSpace>();
		if (usingJsEngine) {
			mgr = new ScriptEngineManager();
			jsEngine = mgr.getEngineByExtension("js");
			accessableFilter = new AccessableEdgeFilter(jsEngine);
		} else {
			beanShellEngine = new Interpreter();
			accessableFilter = new AccessableEdgeFilter(beanShellEngine);
		}
		Void = new VoidPrintStream();
	}

	public void eval(String script) {
		if (jsEngine != null) {
			try {
				jsEngine.eval(script);
			} catch (ScriptException e) {
				logger.error("Problem when running: '" + script + "' in BeanShell");
				logger.error("EvalError: " + e);
				logger.error(e.getCause());
				throw new RuntimeException("Execution of startup script generated an error.", e);
			}
		} else if (beanShellEngine != null) {
			try {
				beanShellEngine.eval(script);
			} catch (EvalError e) {
				logger.error("Problem when running: '" + script + "' in BeanShell");
				logger.error("EvalError: " + e);
				logger.error(e.getCause());
				throw new RuntimeException("Execution of startup script generated an error.", e);
			}
		}
	}

	@Override
	public String getCurrentVertexName() {
		return super.getCurrentVertexName() + (hasInternalVariables() ? "/" + getCurrentDataString() : "");
	}

	@Override
	public Set<Edge> getCurrentOutEdges() throws FoundNoEdgeException {
		Set<Edge> retur = super.getCurrentOutEdges();
		for (Iterator<Edge> i = retur.iterator(); i.hasNext();) {
			Edge e = i.next();
			if (!accessableFilter.acceptEdge(getModel(), e)) {
				logger.debug("Not accessable: " + e + " from " + getCurrentVertexName());
				i.remove();
			} else {
				logger.debug("Accessable: " + e + " from " + getCurrentVertexName());
			}
		}
		if (retur.size() == 0) {
			throw new FoundNoEdgeException("Cul-De-Sac, dead end found in '" + getCurrentVertex() + "'");
		}
		return retur;
	}

	@Override
	public boolean hasInternalVariables() {
		if (jsEngine != null) {
			return !jsEngine.getBindings(ScriptContext.ENGINE_SCOPE).isEmpty();
		} else if (beanShellEngine != null) {
			return beanShellEngine.getNameSpace().getVariableNames().length > 1;
		}
		return false;
	}

	public Set<Entry<String, Object>> getCurrentJsEngineData() {
		Bindings bindings = jsEngine.getBindings(ScriptContext.ENGINE_SCOPE);
		return bindings.entrySet();
	}

	public Hashtable<String, Object> getCurrentBeanShellData() {
		Hashtable<String, Object> retur = new Hashtable<String, Object>();
		if (!hasInternalVariables())
			return retur;

		int i = 0;
		NameSpace ns = beanShellEngine.getNameSpace();
		String[] variableNames = beanShellEngine.getNameSpace().getVariableNames();
		try {
			for (; i < variableNames.length; i++) {
				if (!variableNames[i].equals("bsh")) {
					retur.put(variableNames[i], Primitive.unwrap(ns.getVariable(variableNames[i])));
				}
			}
		} catch (UtilEvalError e) {
			throw new RuntimeException("Malformed model data: " + variableNames[i] + "\nBeanShell error message: '" + e.getMessage() + "'");
		} catch (NullPointerException e) {
			logger.warn("Did not get data/value for: " + variableNames[i]);
			return retur;
		}
		return retur;
	}

	/**
	 * Walks the data space, and return the value of the data, if found.
	 * 
	 * @param dataName
	 * @return
	 * @throws InvalidDataException
	 *           is thrown if the data is not found in the data space
	 */
	public String getDataValue(String dataName) {
		if (jsEngine != null) {
			return jsEngine.get(dataName).toString();
		} else if (beanShellEngine != null) {
			Hashtable<String, Object> dataTable = getCurrentBeanShellData();
			if (dataTable.containsKey(dataName)) {
				if (dataTable.get(dataName) instanceof Object[]) {
					return Arrays.deepToString((Object[]) dataTable.get(dataName));
				} else {
					return dataTable.get(dataName).toString();
				}
			}
		}
		return "";
	}

	/**
	 * Executes an action, and returns any outcome as a string.
	 * 
	 * @param action
	 * @return
	 * @throws InvalidDataException
	 *           is thrown if the data is not found in the data space
	 */
	public String execAction(String action) throws InvalidDataException {
		logger.debug("Will try to execute: " + action);
		Object res = null;
		if (jsEngine != null) {
			try {
				res = jsEngine.eval(action);
			} catch (ScriptException e) {
				throw new InvalidDataException("The action: '" + action + "', does not evaluate correctly. Detail: " + e.getMessage());
			}
		} else if (beanShellEngine != null) {
			try {
				res = beanShellEngine.eval(action);
			} catch (EvalError e) {
				throw new InvalidDataException("The action: '" + action + "', does not evaluate correctly. Detail: " + e.getMessage());
			}
		}
		return res.toString();
	}

	@Override
	public String getCurrentDataString() {
		String retur = "";

		if (jsEngine != null) {
			Set<Entry<String, Object>> dataTable = getCurrentJsEngineData();
			for (Entry<String, Object> entry : dataTable) {
				if (!entry.getKey().equals("println") && !entry.getKey().equals("print") && !entry.getKey().equals("context"))
					retur += entry.getKey() + "=" + entry.getValue() + ";";
			}
		} else if (beanShellEngine != null) {
			Hashtable<String, Object> dataTable = getCurrentBeanShellData();
			Enumeration<String> e = dataTable.keys();
			while (e.hasMoreElements()) {
				String key = e.nextElement();
				String data = "";
				if (dataTable.get(key) instanceof Object[]) {
					data = Arrays.deepToString((Object[]) dataTable.get(key));
				} else {
					data = dataTable.get(key).toString();
				}
				retur += key + "=" + data + ";";
			}
		}
		return retur;
	}

	@Override
	public boolean walkEdge(Edge edge) {
		boolean hasWalkedEdge = super.walkEdge(edge);
		if (hasWalkedEdge) {
			if (hasAction(edge)) {
				PrintStream ps = System.out;
				System.setOut(Void);

				if (jsEngine != null) {
					try {
						jsEngine.eval(getAction(edge));
					} catch (ScriptException e) {
						logger.error("Problem when running: '" + getAction(edge) + "' in Java Script engine");
						logger.error("EvalError: " + e);
						logger.error(e.getCause());
						throw new RuntimeException("Malformed action sequence\n\t" + edge + "\n\tAction sequence: " + edge.getActionsKey()
						    + "\n\tJava Script error message: '" + e.getMessage() + "'\nDetails: " + e.getCause());
					} finally {
						System.setOut(ps);
					}
				} else if (beanShellEngine != null) {
					try {
						beanShellEngine.eval(getAction(edge));
					} catch (EvalError e) {
						logger.error("Problem when running: '" + getAction(edge) + "' in BeanShell");
						logger.error("EvalError: " + e);
						logger.error(e.getCause());
						throw new RuntimeException("Malformed action sequence\n\t" + edge + "\n\tAction sequence: " + edge.getActionsKey()
						    + "\n\tBeanShell error message: '" + e.getMessage() + "'\nDetails: " + e.getCause());
					} finally {
						System.setOut(ps);
					}
				}
			}
		}
		return hasWalkedEdge;
	}

	private String getAction(Edge edge) {
		return (edge == null ? "" : edge.getActionsKey());
	}

	private boolean hasAction(Edge edge) {
		return (edge == null ? false : !edge.getActionsKey().isEmpty());
	}

	@Override
	protected void track() {
		super.track();
		if (jsEngine != null) {
		} else if (beanShellEngine != null) {
			namespaceStack.push(new CannedNameSpace(beanShellEngine.getNameSpace()));
		}
	}

	@Override
	protected void popVertex() {
		super.popVertex();
		if (jsEngine != null) {
		} else if (beanShellEngine != null) {
			beanShellEngine.setNameSpace((namespaceStack.pop()).unpack());
		}
	}

	private class CannedNameSpace {
		byte[] store;

		public CannedNameSpace(NameSpace objNameSpace) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(objNameSpace);
			} catch (IOException e) {
				throw new RuntimeException("Unable to store backtrack information due to a IOException.", e);
			}
			store = baos.toByteArray();
		}

		public NameSpace unpack() {
			ByteArrayInputStream bais = new ByteArrayInputStream(store);
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(bais);
				return (NameSpace) ois.readObject();
			} catch (IOException e) {
				throw new RuntimeException("Unable to restore backtrack information due to a IOException.", e);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Unable to restore backtrack information as the NameSpace Class could not be found.", e);
			}
		}
	}

	@Override
	public void setCalculatingPath(boolean calculatingPath) {
		super.setCalculatingPath(calculatingPath);
		if (calculatingPath && this.oldPrintStream != System.out) {
			this.oldPrintStream = System.out;
			System.setOut(new VoidPrintStream());
		} else if (!calculatingPath && this.oldPrintStream != System.out) {
			System.setOut(this.oldPrintStream);
		}
	}

	private class VoidPrintStream extends PrintStream {
		public VoidPrintStream() {
			super(System.out);
		}

		@Override
		public void write(byte[] buf, int off, int len) {
		}
	}

	public boolean isJsEnabled() {
		return jsEngine != null;
	}

	public boolean isBeanShellEnabled() {
		return beanShellEngine != null;
	}
}