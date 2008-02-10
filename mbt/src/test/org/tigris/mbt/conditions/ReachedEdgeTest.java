package test.org.tigris.mbt.conditions;

import org.tigris.mbt.Keywords;
import org.tigris.mbt.ModelBasedTesting;
import org.tigris.mbt.Util;
import org.tigris.mbt.conditions.ReachedEdge;
import org.tigris.mbt.conditions.StopCondition;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import junit.framework.TestCase;

public class ReachedEdgeTest extends TestCase {

	SparseGraph graph;
	DirectedSparseVertex start;
	DirectedSparseVertex v1;
	DirectedSparseVertex v2;
	DirectedSparseEdge e0;
	DirectedSparseEdge e1;
	
	protected void setUp() throws Exception {
		super.setUp();
		graph = new SparseGraph();
		
		start = Util.addVertexToGraph(graph, "Start");
		v1 = Util.addVertexToGraph(graph, "V1");
		v2 = Util.addVertexToGraph(graph, "V2");

		e0 = Util.addEdgeToGraph(graph, start, v1, "E0", null, null, null);
		e1 = Util.addEdgeToGraph(graph, v1, v2, "E1", null, null, null);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		graph.removeAllEdges();
		graph.removeAllVertices();
		start = v1 = v2 = null; 
		e0 = e1 = null;
	}

	public void testConstructor()
	{
		ModelBasedTesting mbt = new ModelBasedTesting();
		mbt.setCondition(new ReachedEdge("E1"));
	}
	
	public void testFulfillment()
	{
		ModelBasedTesting mbt = new ModelBasedTesting();
		StopCondition condition = new ReachedEdge("E1");
		mbt.setCondition(condition);
		mbt.setGraph(graph);
		mbt.setGenerator(Keywords.GENERATOR_RANDOM);
		assertTrue(mbt.hasNextStep());
		
		assertEquals((double)0, condition.getFulfillment(), 0.01);
		mbt.getNextStep();
		assertEquals((double)0, condition.getFulfillment(), 0.01);
		mbt.getNextStep();
		assertEquals((double)1, condition.getFulfillment(), 0.01);
	}

	public void testIsFulfilled()
	{
		ModelBasedTesting mbt = new ModelBasedTesting();
		StopCondition condition = new ReachedEdge("E1");
		mbt.setCondition(condition);
		mbt.setGraph(graph);
		mbt.setGenerator(Keywords.GENERATOR_RANDOM);
		assertTrue(mbt.hasNextStep());

		assertEquals(false, condition.isFulfilled());
		mbt.getNextStep();
		assertEquals(false, condition.isFulfilled());
		mbt.getNextStep();
		assertEquals(true, condition.isFulfilled());
	}


}
