package org.graphwalker.conditions;

import org.graphwalker.Keywords;
import org.graphwalker.ModelBasedTesting;
import org.graphwalker.Util;
import org.graphwalker.conditions.StopCondition;
import org.graphwalker.conditions.VertexCoverage;
import org.graphwalker.exceptions.GeneratorException;
import org.graphwalker.exceptions.StopConditionException;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;

import junit.framework.TestCase;

public class StateCoverageTest extends TestCase {
	Graph graph;
	Vertex start;
	Vertex v1;
	Vertex v2;
	Edge e0;
	Edge e1;

	protected void setUp() throws Exception {
		super.setUp();
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

	public void testConstructor() throws StopConditionException {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		mbt.setCondition(new VertexCoverage());
	}

	public void testFulfillment() throws StopConditionException, GeneratorException, InterruptedException {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		StopCondition condition = new VertexCoverage();
		mbt.setCondition(condition);
		mbt.setGraph(graph);
		mbt.setGenerator(Keywords.GENERATOR_RANDOM);
		assertTrue(mbt.hasNextStep());

		assertEquals((double) 1 / 3, condition.getFulfilment(), 0.01);
		mbt.getNextStep();
		assertEquals((double) 2 / 3, condition.getFulfilment(), 0.01);
		mbt.getNextStep();
		assertEquals((double) 3 / 3, condition.getFulfilment(), 0.01);
	}

	public void testIsFulfilled() throws StopConditionException, GeneratorException, InterruptedException {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		StopCondition condition = new VertexCoverage();
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
