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

package org.graphwalker;

import org.graphwalker.Util;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;
import org.graphwalker.machines.FiniteStateMachine;

import junit.framework.TestCase;

public class FiniteStateMachineTest extends TestCase {

	Graph graph;
	Vertex start;
	Vertex v1;
	Vertex v2;
	Edge e1;
	Edge e2;
	Edge e3;
	Edge e4;

	protected void setUp() throws Exception {
		super.setUp();
		graph = new Graph();

		start = Util.addVertexToGraph(graph, "Start");

		v1 = Util.addVertexToGraph(graph, "V1");
		v1.setReqTagKey("REQ002");

		v2 = Util.addVertexToGraph(graph, "V2");
		v2.setReqTagKey("REQ004");

		e1 = Util.addEdgeToGraph(graph, start, v1, "E1", null, null, null);
		e1.setReqTagKey("REQ001,REQ002");

		e2 = Util.addEdgeToGraph(graph, v1, v2, "E2", null, null, null);
		e2.setReqTagKey("REQ003");

		e3 = Util.addEdgeToGraph(graph, v2, v2, "E3", null, null, null);

		e4 = Util.addEdgeToGraph(graph, v2, v1, "E4", null, null, null);
	}

	public void testRequirements() {
		FiniteStateMachine FSM = new FiniteStateMachine();
		FSM.setModel(graph);
		assertEquals("{REQ001=0, REQ004=0, REQ003=0, REQ002=0}", FSM.getAllRequirements().toString());
		assertEquals("[]", FSM.getCoveredRequirements().toString());
	}

	public void testRequirementsWalk() {
		FiniteStateMachine FSM = new FiniteStateMachine();
		FSM.setModel(graph);
		assertEquals("Start", FSM.getCurrentVertexName());
		FSM.walkEdge(e1);
		assertEquals("E1", FSM.getLastEdgeName());
		assertEquals("V1", FSM.getCurrentVertexName());
		assertEquals("{REQ001=1, REQ004=0, REQ003=0, REQ002=2}", FSM.getAllRequirements().toString());
		assertEquals("[REQ001, REQ002]", FSM.getCoveredRequirements().toString());
		FSM.walkEdge(e2);
		assertEquals("V2", FSM.getCurrentVertexName());
		assertEquals("{REQ001=1, REQ004=1, REQ003=1, REQ002=2}", FSM.getAllRequirements().toString());
		assertEquals("[REQ001, REQ004, REQ003, REQ002]", FSM.getCoveredRequirements().toString());
	}
}
