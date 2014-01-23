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
import org.graphwalker.exceptions.GeneratorException;
import org.graphwalker.exceptions.StopConditionException;
import org.graphwalker.generators.RandomPathGenerator;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;

public class NSwitchCoverageTest extends TestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testFulfillment() throws StopConditionException, GeneratorException, InterruptedException {
    ModelBasedTesting mbt = ModelBasedTesting.getInstance();

    Graph graph = new Graph();

    Vertex start = Util.addVertexToGraph(graph, "Start");
    Vertex v1 = Util.addVertexToGraph(graph, "V1");
    Vertex v2 = Util.addVertexToGraph(graph, "V2");
    Vertex v3 = Util.addVertexToGraph(graph, "V3");
    Vertex v4 = Util.addVertexToGraph(graph, "V4");
    Vertex v5 = Util.addVertexToGraph(graph, "V5");
    Vertex v6 = Util.addVertexToGraph(graph, "V6");
    Vertex v7 = Util.addVertexToGraph(graph, "V7");
    Vertex v8 = Util.addVertexToGraph(graph, "V8");
    Vertex v9 = Util.addVertexToGraph(graph, "V9");
    Vertex v10 = Util.addVertexToGraph(graph, "V10");

    Util.addEdgeToGraph(graph, start, v1, "E_Start_1", null, null, null);
    Util.addEdgeToGraph(graph, v1, v2, "E_1_2", null, null, null);
    Util.addEdgeToGraph(graph, v1, v3, "E_1_3", null, null, null);
    Util.addEdgeToGraph(graph, v2, v4, "E_2_4", null, null, null);
    Util.addEdgeToGraph(graph, v2, v5, "E_2_5", null, null, null);
    Util.addEdgeToGraph(graph, v3, v6, "E_3_6", null, null, null);
    Util.addEdgeToGraph(graph, v3, v7, "E_3_7", null, null, null);
    Util.addEdgeToGraph(graph, v4, v8, "E_4_8", null, null, null);
    Util.addEdgeToGraph(graph, v5, v8, "E_5_8", null, null, null);
    Util.addEdgeToGraph(graph, v6, v9, "E_6_9", null, null, null);
    Util.addEdgeToGraph(graph, v7, v9, "E_7_9", null, null, null);
    Util.addEdgeToGraph(graph, v8, v10, "E_8_10", null, null, null);
    Util.addEdgeToGraph(graph, v9, v10, "E_9_10", null, null, null);
    Util.addEdgeToGraph(graph, v10, v1, "E_10_1", null, null, null);

    mbt.setGraph(graph);
    mbt.setGenerator(new RandomPathGenerator(new NSwitchCoverage(1)));

    int count = 0;
    while (mbt.hasNextStep() && count < 1000) {
      count++;
      mbt.getNextStep();
    }

    assertEquals(1.0, mbt.getGenerator().getStopCondition().getFulfilment(), 0.01);
  }

  public void testStraightModel() throws StopConditionException, GeneratorException, InterruptedException {
    ModelBasedTesting mbt = ModelBasedTesting.getInstance();

    Graph graph = new Graph();

    Vertex start = Util.addVertexToGraph(graph, "Start");
    Vertex v1 = Util.addVertexToGraph(graph, "V1");
    Vertex v2 = Util.addVertexToGraph(graph, "V2");
    Vertex v3 = Util.addVertexToGraph(graph, "V3");
    Vertex v4 = Util.addVertexToGraph(graph, "V4");
    Vertex v5 = Util.addVertexToGraph(graph, "V5");

    Util.addEdgeToGraph(graph, start, v1, "e1", null, null, null);
    Util.addEdgeToGraph(graph, v1, v2, "e2", null, null, null);
    Util.addEdgeToGraph(graph, v2, v3, "e2", null, null, null);
    Util.addEdgeToGraph(graph, v3, v4, "e2", null, null, null);
    Util.addEdgeToGraph(graph, v4, v5, "e2", null, null, null);

    mbt.setGraph(graph);
    mbt.setGenerator(new RandomPathGenerator(new NSwitchCoverage(1)));

    int count = 0;
    while (mbt.hasNextStep() && count < 1000) {
      count++;
      mbt.getNextStep();
    }
    assertEquals(1.0, mbt.getGenerator().getStopCondition().getFulfilment(), 0.01);
  }

}
