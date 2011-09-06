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
import org.graphwalker.conditions.TestCaseLength;
import org.graphwalker.generators.RandomPathGenerator;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;
import org.graphwalker.machines.FiniteStateMachine;

import junit.framework.TestCase;

public class RequirementVariableTest extends TestCase {

	Graph graph;
	Vertex start;
	Vertex v1;
	Vertex v2;
	Edge e1;
	Edge e2;
	Edge e3;
	Edge e4;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		graph = new Graph();

		start = Util.addVertexToGraph(graph, "Start");

		v1 = Util.addVertexToGraph(graph, "V1");

		v2 = Util.addVertexToGraph(graph, "V2");
		v2.setReqTagKey("REQ004,${reqvar}");

		e1 = Util.addEdgeToGraph(graph, start, v1, "E1", null, null, "");
		e1.setActionsKey("reqvar=\"REQ002,REQ003\"");

		e2 = Util.addEdgeToGraph(graph, v1, v2, "E2", null, null, "");
		e2.setActionsKey("reqvar=reqvar + \",REQ005\" + \",REQ007\"");

		e3 = Util.addEdgeToGraph(graph, v2, v1, "E3", null, null, "");

		e4 = Util.addEdgeToGraph(graph, v1, v2, "E2", null, null, "");
		e4.setActionsKey("reqvar=\"REQ006\"");
	}

	public void testRequirements() {
		// ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(false);

		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.enableExtended(true);
			FiniteStateMachine EFSM = mbt.getMachine();
			mbt.setGenerator(new RandomPathGenerator(new TestCaseLength(10)));

			EFSM.setModel(graph);
			mbt.populateMachineRequirementHashTable();
			EFSM.walkEdge(e1);
			EFSM.walkEdge(e2);
			mbt.passRequirement(true);
			String[] tmp = EFSM.getStatisticsVerbose().split("\n");
			int numberOfExpectedAssersions = 0;
			for (int i = 0; i < tmp.length; i++) {
				if (tmp[i].contains("REQ002")) {
					assertEquals("Requirement: REQ002 has passed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ003")) {
					assertEquals("Requirement: REQ003 has passed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ004")) {
					assertEquals("Requirement: REQ004 has passed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ005")) {
					assertEquals("Requirement: REQ005 has passed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ006")) {
					assertEquals("Requirement: REQ006 is not tested.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ007")) {
					assertEquals("Requirement: REQ007 has passed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
			}
			assertEquals(6, numberOfExpectedAssersions);
			numberOfExpectedAssersions = 0;

			EFSM.walkEdge(e3);
			EFSM.walkEdge(e4);
			mbt.passRequirement(false);
			tmp = EFSM.getStatisticsVerbose().split("\n");
			for (int i = 0; i < tmp.length; i++) {
				if (tmp[i].contains("REQ002")) {
					assertEquals("Requirement: REQ002 has passed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ003")) {
					assertEquals("Requirement: REQ003 has passed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ004")) {
					assertEquals("Requirement: REQ004 has failed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ005")) {
					assertEquals("Requirement: REQ005 has passed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ006")) {
					assertEquals("Requirement: REQ006 has failed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ007")) {
					assertEquals("Requirement: REQ007 has passed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
			}
			assertEquals(6, numberOfExpectedAssersions);
			numberOfExpectedAssersions = 0;

			EFSM.walkEdge(e3);
			EFSM.walkEdge(e2);
			mbt.passRequirement(false);
			tmp = EFSM.getStatisticsVerbose().split("\n");
			for (int i = 0; i < tmp.length; i++) {
				if (tmp[i].contains("REQ002")) {
					assertEquals("Requirement: REQ002 has passed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ003")) {
					assertEquals("Requirement: REQ003 has passed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ004")) {
					assertEquals("Requirement: REQ004 has failed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ005")) {
					assertEquals("Requirement: REQ005 has failed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ006")) {
					assertEquals("Requirement: REQ006 has failed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ007")) {
					assertEquals("Requirement: REQ007 has failed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
			}
			assertEquals(6, numberOfExpectedAssersions);
			numberOfExpectedAssersions = 0;

			EFSM.walkEdge(e3);
			EFSM.walkEdge(e2);
			mbt.passRequirement(true);
			for (int i = 0; i < tmp.length; i++) {
				if (tmp[i].contains("REQ002")) {
					assertEquals("Requirement: REQ002 has passed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ003")) {
					assertEquals("Requirement: REQ003 has passed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ004")) {
					assertEquals("Requirement: REQ004 has failed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ005")) {
					assertEquals("Requirement: REQ005 has failed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ006")) {
					assertEquals("Requirement: REQ006 has failed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
				if (tmp[i].contains("REQ007")) {
					assertEquals("Requirement: REQ007 has failed.", tmp[i]);
					numberOfExpectedAssersions++;
				}
			}
			assertEquals(6, numberOfExpectedAssersions);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void testRequirementsWalk() {
		// ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(false);
		// EFSM.setModel(graph);
		// assertEquals("Start", EFSM.getCurrentVertexName());
		// EFSM.walkEdge(e1);
		// assertEquals("V1/x=1;y=[];", EFSM.getCurrentVertexName());
		// assertEquals("{REQ001=1, REQ004=0, REQ003=0, REQ002=2}",
		// EFSM.getAllRequirements().toString());
		// assertEquals("[REQ001, REQ002]",
		// EFSM.getCoveredRequirements().toString());
		// EFSM.walkEdge(e2);
		// assertEquals("V2/x=2;y=[];", EFSM.getCurrentVertexName());
		// assertEquals("{REQ001=1, REQ004=1, REQ003=1, REQ002=2}",
		// EFSM.getAllRequirements().toString());
		// assertEquals("[REQ001, REQ004, REQ003, REQ002]",
		// EFSM.getCoveredRequirements().toString());
	}
}
