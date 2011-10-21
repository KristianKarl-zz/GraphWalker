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

package org.graphwalker.core.generators;

import org.apache.log4j.Logger;
import org.graphwalker.core.Util;
import org.graphwalker.core.graph.Edge;
import org.graphwalker.core.graph.Graph;
import org.graphwalker.core.graph.Vertex;
import org.graphwalker.core.machines.FiniteStateMachine;

import junit.framework.TestCase;

public class ListGeneratorTest extends TestCase {
	Logger logger = Util.setupLogger(ListGeneratorTest.class);

	public void testGetNext() {
		Graph graph = new Graph();

		Vertex v1 = new Vertex();
		v1.setIndexKey(new Integer(1));
		v1.setLabelKey("Start");
		graph.addVertex(v1);

		Vertex v2 = new Vertex();
		v2.setIndexKey(new Integer(2));
		v2.setLabelKey("V2");
		graph.addVertex(v2);

		Edge edge = new Edge();
		edge.setIndexKey(new Integer(3));
		edge.setLabelKey("E1");
		graph.addEdge(edge, v1, v2);

		ListGenerator generator = new ListGenerator();
		FiniteStateMachine fsm = new FiniteStateMachine();
		fsm.setModel(graph);
		generator.setMachine(fsm);

		String[] s = generator.getNext();
		assertEquals("E1", s[0]);
		assertEquals("Edge", s[1]);
		s = generator.getNext();
		assertEquals("V2", s[0]);
		assertEquals("Vertex", s[1]);
		assertFalse(generator.hasNext());
	}
}
