package test.org.graphwalker.conditions;

import org.graphwalker.Keywords;
import org.graphwalker.ModelBasedTesting;
import org.graphwalker.Util;
import org.graphwalker.conditions.ReachedVertex;
import org.graphwalker.conditions.StopCondition;
import org.graphwalker.exceptions.GeneratorException;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;

import junit.framework.TestCase;

public class ReachedStateTest extends TestCase {
	Graph graph;
	Vertex start;
	Vertex v1;
	Vertex v2;
	Edge e0;
	Edge e1;

	protected void setUp() throws Exception {
		super.setUp();
		ModelBasedTesting.getInstance().reset();
		graph = new Graph();

		start = Util.addVertexToGraph(graph, "Start");
		v1 = Util.addVertexToGraph(graph, "V1");
		v2 = Util.addVertexToGraph(graph, "V2");

		e0 = Util.addEdgeToGraph(graph, start, v1, "E0", null, null, null);
		e1 = Util.addEdgeToGraph(graph, v1, v2, "E1", null, null, null);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		graph = null;
		start = v1 = v2 = null;
		e0 = e1 = null;
	}

	public void testConstructor() {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		mbt.setCondition(new ReachedVertex("V2"));
	}

	public void testFulfillment() throws GeneratorException, InterruptedException {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		StopCondition condition = new ReachedVertex("V2");
		mbt.setCondition(condition);
		mbt.setGraph(graph);
		mbt.setGenerator(Keywords.GENERATOR_RANDOM);
		assertTrue(mbt.hasNextStep());

		assertEquals((double) 0, condition.getFulfilment(), 0.01);
		mbt.getNextStep();
		assertEquals((double) 0.5, condition.getFulfilment(), 0.01);
		mbt.getNextStep();
		assertEquals((double) 1, condition.getFulfilment(), 0.01);
	}

	public void testIsFulfilled() throws GeneratorException, InterruptedException {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		StopCondition condition = new ReachedVertex("V2");
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
