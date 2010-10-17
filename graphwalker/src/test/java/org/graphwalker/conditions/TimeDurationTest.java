package org.graphwalker.conditions;

import org.graphwalker.Keywords;
import org.graphwalker.ModelBasedTesting;
import org.graphwalker.Util;
import org.graphwalker.conditions.StopCondition;
import org.graphwalker.conditions.TimeDuration;
import org.graphwalker.exceptions.GeneratorException;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;

import junit.framework.TestCase;

public class TimeDurationTest extends TestCase {
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

	public void testConstructor() {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		mbt.setCondition(new TimeDuration(1));
	}

	public void testFulfillment() throws InterruptedException, GeneratorException {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		StopCondition condition = new TimeDuration(1);
		double startTime = (double) System.currentTimeMillis();
		mbt.setCondition(condition);
		mbt.setGraph(graph);
		mbt.setGenerator(Keywords.GENERATOR_RANDOM);
		assertTrue(mbt.hasNextStep());

		while ((System.currentTimeMillis() - startTime) < 10)
			Thread.sleep(1);
		assertEquals((System.currentTimeMillis() - startTime) / 1000, condition.getFulfilment(), 0.1);
		while ((System.currentTimeMillis() - startTime) < 900)
			Thread.sleep(1);
		assertEquals((System.currentTimeMillis() - startTime) / 1000, condition.getFulfilment(), 0.1);
		while ((System.currentTimeMillis() - startTime) < 1000)
			Thread.sleep(1);
		assertEquals((System.currentTimeMillis() - startTime) / 1000, condition.getFulfilment(), 0.1);
	}

	public void testIsFulfilled() throws InterruptedException, GeneratorException {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		StopCondition condition = new TimeDuration(1);
		double startTime = (double) System.currentTimeMillis();
		mbt.setCondition(condition);
		mbt.setGraph(graph);
		mbt.setGenerator(Keywords.GENERATOR_RANDOM);
		assertTrue(mbt.hasNextStep());

		while ((System.currentTimeMillis() - startTime) < 10)
			Thread.sleep(1);
		assertEquals(false, condition.isFulfilled());
		while ((System.currentTimeMillis() - startTime) < 900)
			Thread.sleep(1);
		assertEquals(false, condition.isFulfilled());
		while ((System.currentTimeMillis() - startTime) < 1000)
			Thread.sleep(1);
		System.out.println(condition.getFulfilment());
		assertEquals(true, condition.isFulfilled());
	}

}
