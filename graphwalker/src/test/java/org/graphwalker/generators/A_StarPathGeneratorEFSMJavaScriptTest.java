package org.graphwalker.generators;

import org.graphwalker.Util;
import org.graphwalker.conditions.ReachedEdge;
import org.graphwalker.conditions.ReachedVertex;
import org.graphwalker.generators.A_StarPathGenerator;
import org.graphwalker.generators.PathGenerator;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;
import org.graphwalker.machines.ExtendedFiniteStateMachine;

import junit.framework.TestCase;

public class A_StarPathGeneratorEFSMJavaScriptTest extends TestCase {

	Graph graph;
	Vertex start;
	Vertex v1;
	Vertex v2;
	Edge e0;
	Edge e1;
	Edge e2;
	Edge e3;

	protected void setUp() throws Exception {
		super.setUp();
		graph = new Graph();

		start = Util.addVertexToGraph(graph, "Start");
		v1 = Util.addVertexToGraph(graph, "V1");
		v2 = Util.addVertexToGraph(graph, "V2");

		e0 = Util.addEdgeToGraph(graph, start, v1, "E0", null, null, "importClass(java.util.Vector);x=1;y=new Vector()");
		e1 = Util.addEdgeToGraph(graph, v1, v2, "E1", null, null, "x=2");
		e2 = Util.addEdgeToGraph(graph, v2, v2, "E2", null, "x<4", "x++");
		e3 = Util.addEdgeToGraph(graph, v2, v1, "E3", null, "y.size()<3", "y.add(x)");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		graph = null;
		start = v1 = v2 = null;
		e0 = e1 = e2 = e3 = null;
	}

	public void test_EFSM_StateStop() throws InterruptedException {
		PathGenerator pathGenerator = new A_StarPathGenerator();
		pathGenerator.setStopCondition(new ReachedVertex("V1/y=\\[2.0, 3.0, 3.0\\];x=3.0;"));
		ExtendedFiniteStateMachine machine = new ExtendedFiniteStateMachine(true);
		machine.setModel(graph);
		pathGenerator.setMachine(machine);

		String[] stepPair;
		stepPair = pathGenerator.getNext();
		assertEquals("E0", stepPair[0]);
		assertEquals("V1/y=[];x=1.0;", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E1", stepPair[0]);
		assertEquals("V2/y=[];x=2.0;", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E3", stepPair[0]);
		assertEquals("V1/y=[2.0];x=2.0;", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E1", stepPair[0]);
		assertEquals("V2/y=[2.0];x=2.0;", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E2", stepPair[0]);
		assertEquals("V2/y=[2.0];x=3.0;", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E3", stepPair[0]);
		assertEquals("V1/y=[2.0, 3.0];x=3.0;", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E1", stepPair[0]);
		assertEquals("V2/y=[2.0, 3.0];x=2.0;", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E2", stepPair[0]);
		assertEquals("V2/y=[2.0, 3.0];x=3.0;", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E3", stepPair[0]);
		assertEquals("V1/y=[2.0, 3.0, 3.0];x=3.0;", stepPair[1]);
		assertFalse(pathGenerator.hasNext());

	}

	public void test_EFSM_EdgeStop() throws InterruptedException {
		PathGenerator pathGenerator = new A_StarPathGenerator();
		pathGenerator.setStopCondition(new ReachedEdge("E2"));
		ExtendedFiniteStateMachine machine = new ExtendedFiniteStateMachine(true);
		machine.setModel(graph);
		pathGenerator.setMachine(machine);

		String[] stepPair;
		stepPair = pathGenerator.getNext();
		assertEquals("E0", stepPair[0]);
		assertEquals("V1/y=[];x=1.0;", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E1", stepPair[0]);
		assertEquals("V2/y=[];x=2.0;", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E2", stepPair[0]);
		assertEquals("V2/y=[];x=3.0;", stepPair[1]);
		assertFalse(pathGenerator.hasNext());

	}
}
