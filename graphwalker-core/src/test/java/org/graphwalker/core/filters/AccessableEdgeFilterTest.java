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

import org.graphwalker.core.graph.Edge;
import org.graphwalker.core.graph.Graph;
import org.graphwalker.core.graph.Vertex;

import bsh.EvalError;
import bsh.Interpreter;

import junit.framework.TestCase;

public class AccessableEdgeFilterTest extends TestCase {
	private AccessableEdgeFilter f;
	private Graph g;
	private Edge e;
	private Interpreter dataStore;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataStore = new Interpreter();

		Vertex v1 = new Vertex();
		Vertex v2 = new Vertex();
		g = new Graph();
		g.addVertex(v1);
		g.addVertex(v2);

		e = new Edge();
		g.addEdge(e, v1, v2);

		e.setLabelKey("");

		f = new AccessableEdgeFilter(dataStore);
	}

	public void testAcceptEdgeEdge1() {
		e.setLabelKey("");
		assertEquals(true, f.acceptEdge(g, e));
	}

	public void testAcceptEdgeEdge2() {
		e.setLabelKey("test");
		assertEquals(true, f.acceptEdge(g, e));
	}

	public void testAcceptEdgeEdge3() {
		e.setLabelKey("test");
		e.setGuardKey("true");
		assertEquals(true, f.acceptEdge(g, e));
	}

	public void testAcceptEdgeEdge4() {
		e.setGuardKey("true");
		assertEquals(true, f.acceptEdge(g, e));
	}

	public void testAcceptEdgeEdge5() {
		e.setGuardKey("true");
		e.setActionsKey("test");
		assertEquals(true, f.acceptEdge(g, e));
	}

	public void testAcceptEdgeEdge6() {
		e.setGuardKey("false");
		assertEquals(false, f.acceptEdge(g, e));
	}

	public void testAcceptEdgeEdge7() throws EvalError {
		dataStore.eval("X=false");
		e.setGuardKey("X");
		assertEquals(false, f.acceptEdge(g, e));
	}

	public void testAcceptEdgeEdge8() throws EvalError {
		dataStore.eval("X=true");
		e.setGuardKey("X");
		assertEquals(true, f.acceptEdge(g, e));
	}

	public void testAcceptEdgeEdge9() throws EvalError {
		dataStore.eval("X=true");
		e.setGuardKey("X==true");
		assertEquals(true, f.acceptEdge(g, e));
	}

	public void testAcceptEdgeEdge10() throws EvalError {
		dataStore.eval("X=true");
		e.setGuardKey("X==false");
		assertEquals(false, f.acceptEdge(g, e));
	}

	public void testAcceptEdgeEdge11() throws EvalError {
		dataStore.eval("X=true");
		dataStore.eval("Y=true");
		e.setGuardKey("X==Y");
		assertEquals(true, f.acceptEdge(g, e));
	}

	public void testAcceptEdgeEdge12() throws EvalError {
		dataStore.eval("X=true");
		dataStore.eval("Y=false");
		e.setGuardKey("X==Y");
		assertEquals(false, f.acceptEdge(g, e));
	}

	public void testAcceptEdgeEdge13() throws EvalError {
		dataStore.eval("X=5");
		e.setGuardKey("X==5");
		assertEquals(true, f.acceptEdge(g, e));
	}

	public void testAcceptEdgeEdge14() throws EvalError {
		dataStore.eval("X=6");
		e.setGuardKey("X==5");
		assertEquals(false, f.acceptEdge(g, e));
	}

	public void testAcceptEdgeEdge15() throws EvalError {
		dataStore.eval("X=6");
		e.setGuardKey("X>5");
		assertEquals(true, f.acceptEdge(g, e));
	}

	public void testAcceptEdgeEdge16() throws EvalError {
		dataStore.eval("X=4");
		e.setGuardKey("X<5");
		assertEquals(true, f.acceptEdge(g, e));
	}
}
