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

import org.graphwalker.core.ModelBasedTesting;
import org.graphwalker.core.Util;
import org.graphwalker.core.exceptions.GeneratorException;
import org.graphwalker.core.generators.RandomPathGenerator;
import org.graphwalker.core.graph.Edge;
import org.graphwalker.core.graph.Graph;
import org.graphwalker.core.graph.Vertex;

import junit.framework.TestCase;

public class RequirementCoverageTest extends TestCase {

	Graph graph;
	Vertex start;
	Vertex v1;
	Vertex v2;
	Edge e0;
	Edge e1;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ModelBasedTesting.getInstance().reset();
		graph = new Graph();

		start = Util.addVertexToGraph(graph, "Start");
		v1 = Util.addVertexToGraph(graph, "V1");
		v2 = Util.addVertexToGraph(graph, "V2");

		v1.setReqTagKey("R1");
		v2.setReqTagKey("R2");

		e0 = Util.addEdgeToGraph(graph, start, v1, "E0", null, null, null);
		e1 = Util.addEdgeToGraph(graph, v1, v2, "E1", null, null, null);

		e0.setReqTagKey("R3");
		e1.setReqTagKey("R4");
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
		mbt.setGenerator(new RandomPathGenerator(new RequirementCoverage()));
	}

	public void testFulfillment() throws GeneratorException, InterruptedException {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		mbt.setGraph(graph);
		mbt.setGenerator(new RandomPathGenerator(new RequirementCoverage()));
		assertTrue(mbt.hasNextStep());

		assertEquals((double) 0 / 4, mbt.getGenerator().getStopCondition().getFulfilment(), 0.01);
		mbt.getNextStep();
		assertEquals((double) 2 / 4, mbt.getGenerator().getStopCondition().getFulfilment(), 0.01);
		mbt.getNextStep();
		assertEquals((double) 4 / 4, mbt.getGenerator().getStopCondition().getFulfilment(), 0.01);
	}

	public void testIsFulfilled() throws GeneratorException, InterruptedException {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		mbt.setGraph(graph);
		mbt.setGenerator(new RandomPathGenerator(new RequirementCoverage()));
		assertTrue(mbt.hasNextStep());

		assertEquals(false, mbt.getGenerator().getStopCondition().isFulfilled());
		mbt.getNextStep();
		assertEquals(false, mbt.getGenerator().getStopCondition().isFulfilled());
		mbt.getNextStep();
		assertEquals(true, mbt.getGenerator().getStopCondition().isFulfilled());
	}

}
