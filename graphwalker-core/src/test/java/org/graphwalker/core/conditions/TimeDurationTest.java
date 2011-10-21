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

package org.graphwalker.core.conditions;

import org.graphwalker.core.Keywords;
import org.graphwalker.core.ModelBasedTesting;
import org.graphwalker.core.Util;
import org.graphwalker.core.exceptions.GeneratorException;
import org.graphwalker.core.generators.RandomPathGenerator;
import org.graphwalker.core.graph.Edge;
import org.graphwalker.core.graph.Graph;
import org.graphwalker.core.graph.Vertex;

import junit.framework.TestCase;

public class TimeDurationTest extends TestCase {
	Graph graph;
	Vertex start;
	Vertex v1;
	Vertex v2;
	Edge e0;
	Edge e1;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		graph = new Graph();

		start = Util.addVertexToGraph(graph, "Start");
		v1 = Util.addVertexToGraph(graph, "V1");
		v2 = Util.addVertexToGraph(graph, "V2");

		e0 = Util.addEdgeToGraph(graph, start, v1, "E0", null, null, null);
		e1 = Util.addEdgeToGraph(graph, v1, v2, "E1", null, null, null);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		graph = null;
		start = v1 = v2 = null;
		e0 = e1 = null;
	}

	public void testConstructor() {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		mbt.setGenerator(new RandomPathGenerator(new TimeDuration(1)));
	}

	public void testFulfillment() throws InterruptedException, GeneratorException {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		double startTime = System.currentTimeMillis();
		mbt.setGraph(graph);
		mbt.setGenerator(Keywords.GENERATOR_RANDOM);
		mbt.setGenerator(new RandomPathGenerator(new TimeDuration(1)));
		assertTrue(mbt.hasNextStep());

		while ((System.currentTimeMillis() - startTime) < 10)
			Thread.sleep(1);
		assertEquals((System.currentTimeMillis() - startTime) / 1000, mbt.getGenerator().getStopCondition().getFulfilment(), 0.1);
		while ((System.currentTimeMillis() - startTime) < 900)
			Thread.sleep(1);
		assertEquals((System.currentTimeMillis() - startTime) / 1000, mbt.getGenerator().getStopCondition().getFulfilment(), 0.1);
		while ((System.currentTimeMillis() - startTime) < 1000)
			Thread.sleep(1);
		assertEquals((System.currentTimeMillis() - startTime) / 1000, mbt.getGenerator().getStopCondition().getFulfilment(), 0.1);
	}

	public void testIsFulfilled() throws InterruptedException, GeneratorException {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		double startTime = System.currentTimeMillis();
		mbt.setGraph(graph);
		mbt.setGenerator(new RandomPathGenerator(new TimeDuration(1)));
		assertTrue(mbt.hasNextStep());

		while ((System.currentTimeMillis() - startTime) < 10)
			Thread.sleep(1);
		assertEquals(false, mbt.getGenerator().getStopCondition().isFulfilled());
		while ((System.currentTimeMillis() - startTime) < 900)
			Thread.sleep(1);
		assertEquals(false, mbt.getGenerator().getStopCondition().isFulfilled());
		while ((System.currentTimeMillis() - startTime) < 1000)
			Thread.sleep(1);
		System.out.println(mbt.getGenerator().getStopCondition().getFulfilment());
		assertEquals(true, mbt.getGenerator().getStopCondition().isFulfilled());
	}

}
