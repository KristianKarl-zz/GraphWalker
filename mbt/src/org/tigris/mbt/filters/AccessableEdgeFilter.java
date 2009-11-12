//This file is part of the Model-based Testing java package
//Copyright (C) 2005  Kristian Karl
//
//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

package org.tigris.mbt.filters;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.tigris.mbt.Util;
import org.tigris.mbt.graph.Edge;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * @author Johan Tejle
 * 
 */
public class AccessableEdgeFilter {
	static Logger logger = Util.setupLogger(AccessableEdgeFilter.class);

	private ScriptEngine jsEngine = null;
	private Interpreter beanShellEngine = null;

	public AccessableEdgeFilter(ScriptEngine sciptEngine) {
		super();
		this.jsEngine = sciptEngine;
	}

	public AccessableEdgeFilter(Interpreter beanShellEngine) {
		super();
		this.beanShellEngine = beanShellEngine;
	}

	public boolean acceptEdge(org.tigris.mbt.graph.Graph graph, Edge edge) {
		if (edge.getGuardKey().isEmpty()) {
			return true;
		}

		if (jsEngine != null) {
			try {
				return ((Boolean) jsEngine.eval((String) edge.getGuardKey()))
						.booleanValue();
			} catch (ScriptException e) {
				throw new RuntimeException("Malformed Edge guard\n\t" + edge
						+ "\n\tGuard: " + edge.getGuardKey()
						+ "\n\tBeanShell error message: '" + e.getMessage()
						+ "'");
			}
		} else if (beanShellEngine != null) {
			try {
				return ((Boolean) beanShellEngine.eval((String) edge
						.getGuardKey())).booleanValue();
			} catch (EvalError e) {
				throw new RuntimeException("Malformed Edge guard\n\t" + edge
						+ "\n\tGuard: " + edge.getGuardKey()
						+ "\n\tBeanShell error message: '" + e.getMessage()
						+ "'");
			}
		}
		return false;
	}

	public String getName() {
		return "AccessableEdgeFilter";
	}

}
