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

public class CodeGeneratorTest extends TestCase {
	Logger logger = Util.setupLogger(CodeGeneratorTest.class);

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
		graph.addEdge(edge, v1, v2);
		edge.setIndexKey(new Integer(3));
		edge.setLabelKey("E1");

		FiniteStateMachine FSM = new FiniteStateMachine();
		FSM.setModel(graph);

		String[] template = { "", "{EDGE_VERTEX}: {LABEL}", "" };
		CodeGenerator generator = new CodeGenerator();
		generator.setTemplate(template);
		generator.setMachine(FSM);

		assertEquals("Edge: E1", generator.getNext()[0]);
		assertEquals("Vertex: V2", generator.getNext()[0]);
		assertFalse(generator.hasNext());
	}

	public void testHeaderFooter() {
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
		graph.addEdge(edge, v1, v2);
		edge.setIndexKey(new Integer(3));
		edge.setLabelKey("E1");

		FiniteStateMachine FSM = new FiniteStateMachine();
		FSM.setModel(graph);

		String[] template = { "This is the HEADER", "{EDGE_VERTEX}: {LABEL}", "This is the FOOTER" };
		CodeGenerator generator = new CodeGenerator();
		generator.setTemplate(template);
		generator.setMachine(FSM);

		StringBuffer str = new StringBuffer();
		while (generator.hasNext()) {
			str.append(generator.getNext()[0]);
		}
		System.out.println(str.toString());
	}

}
