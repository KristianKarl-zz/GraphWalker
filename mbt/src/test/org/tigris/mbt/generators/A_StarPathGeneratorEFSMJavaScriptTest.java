package test.org.tigris.mbt.generators;

import org.tigris.mbt.Util;
import org.tigris.mbt.conditions.ReachedEdge;
import org.tigris.mbt.conditions.ReachedState;
import org.tigris.mbt.generators.PathGenerator;
import org.tigris.mbt.generators.A_StarPathGenerator;
import org.tigris.mbt.graph.Edge;
import org.tigris.mbt.graph.Graph;
import org.tigris.mbt.graph.Vertex;
import org.tigris.mbt.machines.ExtendedFiniteStateMachine;

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
		graph.removeAllEdges();
		graph.removeAllVertices();
		start = v1 = v2 = null;
		e0 = e1 = e2 = e3 = null;
	}

	public void test_EFSM_StateStop() {
		PathGenerator pathGenerator = new A_StarPathGenerator();
		pathGenerator.setStopCondition(new ReachedState("V1/y=\\[2.0, 3.0, 3.0\\];x=3.0;"));
		pathGenerator.setMachine(new ExtendedFiniteStateMachine(graph, true));

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

	public void test_EFSM_EdgeStop() {
		PathGenerator pathGenerator = new A_StarPathGenerator();
		pathGenerator.setStopCondition(new ReachedEdge("E2"));
		pathGenerator.setMachine(new ExtendedFiniteStateMachine(graph, true));

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
