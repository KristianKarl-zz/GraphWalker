package test.org.tigris.mbt.conditions;

import org.tigris.mbt.Keywords;
import org.tigris.mbt.ModelBasedTesting;
import org.tigris.mbt.Util;
import org.tigris.mbt.conditions.TimeDuration;
import org.tigris.mbt.conditions.StopCondition;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import junit.framework.TestCase;

public class TimeDurationTest extends TestCase {
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
		mbt.setCondition(new TimeDuration(1));
	}
	
	public void testFulfillment()
	{
		ModelBasedTesting mbt = new ModelBasedTesting();
		StopCondition condition = new TimeDuration(1);
		double startTime = (double)System.currentTimeMillis();
		mbt.setCondition(condition);
		mbt.setGraph(graph);
		mbt.setGenerator(Keywords.GENERATOR_RANDOM);
		assertTrue(mbt.hasNextStep());

		while((System.currentTimeMillis() - startTime) < 10);
		assertEquals((System.currentTimeMillis() - startTime) / 1000, condition.getFulfillment(), 0.1);
		while((System.currentTimeMillis() - startTime) < 900);
		assertEquals((System.currentTimeMillis() - startTime) / 1000, condition.getFulfillment(), 0.1);
		while((System.currentTimeMillis() - startTime) < 1000);
		assertEquals((System.currentTimeMillis() - startTime) / 1000, condition.getFulfillment(), 0.1);
	}

	public void testIsFulfilled()
	{
		ModelBasedTesting mbt = new ModelBasedTesting();
		StopCondition condition = new TimeDuration(1);
		double startTime = (double)System.currentTimeMillis();
		mbt.setCondition(condition);
		mbt.setGraph(graph);
		mbt.setGenerator(Keywords.GENERATOR_RANDOM);
		assertTrue(mbt.hasNextStep());

		while((System.currentTimeMillis() - startTime) < 10);
		assertEquals(false, condition.isFulfilled());
		while((System.currentTimeMillis() - startTime) < 900);
		assertEquals(false, condition.isFulfilled());
		while((System.currentTimeMillis() - startTime) < 1000);
		System.out.println(condition.getFulfillment());
		assertEquals(true, condition.isFulfilled());
	}

}
