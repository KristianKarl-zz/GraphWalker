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

/**
 * 
 */
package org.graphwalker.core.generators;

import org.graphwalker.core.Util;
import org.graphwalker.core.graph.Graph;
import org.graphwalker.core.graph.Vertex;
import org.graphwalker.core.machines.FiniteStateMachine;

import junit.framework.TestCase;

/**
 * @author Johan Tejle
 * 
 */
public class CombinedPathGeneratorTest extends TestCase {

	Graph graph;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		graph = new Graph();

		Vertex v1 = Util.addVertexToGraph(graph, "Start");
		Vertex v2 = Util.addVertexToGraph(graph, "V1");
		Util.addEdgeToGraph(graph, v1, v2, "E1", null, null, null);
	}

	public void testCodeList() throws InterruptedException {
		FiniteStateMachine FSM = new FiniteStateMachine();
		FSM.setModel(graph);

		String[] template = { "", "{EDGE_VERTEX}: {LABEL}", "" };
		CodeGenerator generator1 = new CodeGenerator();
		generator1.setTemplate(template);
		ListGenerator generator2 = new ListGenerator();

		CombinedPathGenerator pathGenerator = new CombinedPathGenerator();
		pathGenerator.addPathGenerator(generator1);
		pathGenerator.addPathGenerator(generator2);
		pathGenerator.setMachine(FSM);

		String[] stepPair;

		stepPair = pathGenerator.getNext();
		assertEquals("Edge: E1", stepPair[0]);
		stepPair = pathGenerator.getNext();
		assertEquals("Vertex: V1", stepPair[0]);
		stepPair = pathGenerator.getNext();
		assertEquals("E1", stepPair[0]);
		assertEquals("Edge", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("V1", stepPair[0]);
		assertEquals("Vertex", stepPair[1]);
		assertFalse(pathGenerator.hasNext());

	}
}
