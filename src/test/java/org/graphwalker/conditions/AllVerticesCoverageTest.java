// This file is part of the GraphWalker java package
// The MIT License
//
// Copyright (c) 2010 graphwalker.org
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package org.graphwalker.conditions;

import junit.framework.TestCase;
import org.graphwalker.ModelBasedTesting;
import org.graphwalker.Util;
import org.graphwalker.analyze.Analyze;
import org.graphwalker.exceptions.GeneratorException;
import org.graphwalker.exceptions.StopConditionException;
import org.graphwalker.generators.RandomPathGenerator;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class AllVerticesCoverageTest extends TestCase {

  Graph graph;
  HashMap<String, Vertex> v = new HashMap<String, Vertex>();
  HashMap<String, Edge> e = new HashMap<String, Edge>();

  /**
   *                  +-----+
   *                  |Start|
   *                  +--+--+
   *                     |
   *                     |E0
   *                     v
   *                  +----+
   *         +--------+ V1 +
   *     E1  v        +----+
   *       +----+        ^
   *       | V2 |        |
   *       +-+--+        |
   *     E2  |           |
   *         v           | E4
   *       +----+        |
   *       | V3 |        |
   *       +-+--+        |
   *         |        +--+--+
   *         +------->| V4  |
   *            E3    +--+--+
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    graph = new Graph();

    v.put("Start", Util.addVertexToGraph(graph, "Start"));
    v.put("V1", Util.addVertexToGraph(graph, "V1"));
    v.put("V2", Util.addVertexToGraph(graph, "V2"));
    v.put("V3", Util.addVertexToGraph(graph, "V3"));
    v.put("V4", Util.addVertexToGraph(graph, "V4"));

    e.put("E0", Util.addEdgeToGraph(graph, v.get("Start"), v.get("V1"), "E0", null, null, null));
    e.put("E1", Util.addEdgeToGraph(graph, v.get("V1"), v.get("V2"), "E1", null, null, null));
    e.put("E2", Util.addEdgeToGraph(graph, v.get("V2"), v.get("V3"), "E2", null, null, null));
    e.put("E3", Util.addEdgeToGraph(graph, v.get("V3"), v.get("V4"), "E3", null, null, null));
    e.put("E4", Util.addEdgeToGraph(graph, v.get("V4"), v.get("V1"), "E4", null, null, null));
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    graph = null;
    v.clear();
    e.clear();
  }

  public void testFulfillment() throws StopConditionException, GeneratorException, InterruptedException {
    ModelBasedTesting mbt = ModelBasedTesting.getInstance();
    mbt.setGraph(graph);
    mbt.setGenerator(new RandomPathGenerator(new AllVerticesCoverage((2))));
    assertTrue(mbt.hasNextStep());
    assertEquals(0, mbt.getGenerator().getStopCondition().getFulfilment(), 0.01);

    // Walking E0
    mbt.getNextStep();
    assertEquals(0, mbt.getGenerator().getStopCondition().getFulfilment(), 0.01);

    // Walking E1
    mbt.getNextStep();
    assertEquals(0, mbt.getGenerator().getStopCondition().getFulfilment(), 0.01);

    // Walking E2
    mbt.getNextStep();
    assertEquals(0, mbt.getGenerator().getStopCondition().getFulfilment(), 0.01);

    // Walking E3
    mbt.getNextStep();
    assertEquals(0, mbt.getGenerator().getStopCondition().getFulfilment(), 0.01);

    // Walking E4
    mbt.getNextStep();
    assertEquals((double)1/4, mbt.getGenerator().getStopCondition().getFulfilment(), 0.01);

    // Walking E1
    mbt.getNextStep();
    assertEquals((double)2/4, mbt.getGenerator().getStopCondition().getFulfilment(), 0.01);

    // Walking E2
    mbt.getNextStep();
    assertEquals((double)3/4, mbt.getGenerator().getStopCondition().getFulfilment(), 0.01);

    // Walking E3
    mbt.getNextStep();
    assertEquals((double)1, mbt.getGenerator().getStopCondition().getFulfilment(), 0.01);

    // Walking E4
    mbt.getNextStep();
    assertEquals((double)1, mbt.getGenerator().getStopCondition().getFulfilment(), 0.01);
  }

  public void testIsFulfilled() throws StopConditionException, GeneratorException, InterruptedException {
    ModelBasedTesting mbt = ModelBasedTesting.getInstance();
    mbt.setGraph(graph);
    mbt.setGenerator(new RandomPathGenerator(new AllVerticesCoverage(2)));
    assertTrue(mbt.hasNextStep());

    for (int i = 0; i<7; i++ ) {
      mbt.getNextStep();
      System.out.println(mbt.getCurrentEdge());
      assertFalse("Expected the fulfillment to be false at i=" + i, mbt.getGenerator().getStopCondition().isFulfilled());
    }

    mbt.getNextStep();
    System.out.println(mbt.getCurrentEdge());
    assertEquals(true, mbt.getGenerator().getStopCondition().isFulfilled());
  }

}
