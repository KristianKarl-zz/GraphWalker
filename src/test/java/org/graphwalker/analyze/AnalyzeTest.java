package org.graphwalker.analyze;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.graphwalker.ModelBasedTesting;
import org.graphwalker.Util;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;
import org.junit.Test;

import java.util.HashMap;

public class AnalyzeTest {

  @Test
  public void unreachableVertices() {
    ModelBasedTesting mbt = new ModelBasedTesting();
    mbt.readGraph("graphml/analyze/infinite-loop.graphml");
    String str = Analyze.unreachableVertices(mbt);
    assertTrue("We should have found vertices not reachable from one another", str.length() > 0);

    mbt = new ModelBasedTesting();
    mbt.readGraph("graphml/analyze/non-infinite-loop.graphml");
    str = Analyze.unreachableVertices(mbt);
    assertTrue("We should not have found any vertices not reachable from one another", str.length() == 0);
  }

  /**
   *                  +-----+
   *                  |Start|
   *                  +--+--+
   *                     |
   *                     |E0
   *                     v
   *                  +----+
   *         +--------+ V1 +
   *     E1  |        +----+
   *         |          ^
   *         |          |
   *         +----------+
   *
   */
  @Test
  public void stronglyConnected_1() {
    Graph g = new Graph();
    HashMap<String, Vertex> v = new HashMap<String, Vertex>();
    HashMap<String, Edge> e = new HashMap<String, Edge>();

    v.put("Start", Util.addVertexToGraph(g, "Start"));
    v.put("V1", Util.addVertexToGraph(g, "V1"));

    e.put("E0", Util.addEdgeToGraph(g, v.get("Start"), v.get("V1"), "E0", null, null, null));
    e.put("E1", Util.addEdgeToGraph(g, v.get("V1"), v.get("V1"), "E1", null, null, null));

    assertTrue("The graph should be strongly connected", Analyze.isStronglyConnected(g));
  }

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
  @Test
  public void stronglyConnected_2() {
    Graph g = new Graph();
    HashMap<String, Vertex> v = new HashMap<String, Vertex>();
    HashMap<String, Edge> e = new HashMap<String, Edge>();

    v.put("Start", Util.addVertexToGraph(g, "Start"));
    v.put("V1", Util.addVertexToGraph(g, "V1"));
    v.put("V2", Util.addVertexToGraph(g, "V2"));
    v.put("V3", Util.addVertexToGraph(g, "V3"));
    v.put("V4", Util.addVertexToGraph(g, "V4"));

    e.put("E0", Util.addEdgeToGraph(g, v.get("Start"), v.get("V1"), "E0", null, null, null));
    e.put("E1", Util.addEdgeToGraph(g, v.get("V1"), v.get("V2"), "E1", null, null, null));
    e.put("E2", Util.addEdgeToGraph(g, v.get("V2"), v.get("V3"), "E2", null, null, null));
    e.put("E3", Util.addEdgeToGraph(g, v.get("V3"), v.get("V4"), "E3", null, null, null));
    e.put("E4", Util.addEdgeToGraph(g, v.get("V4"), v.get("V1"), "E4", null, null, null));

    assertTrue("The graph should be strongly connected", Analyze.isStronglyConnected(g));
  }

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
   *         |        +--+--+        +--+--+
   *         +------->| V4  |------->| V5  |
   *            E3    +--+--+  E5    +--+--+
   */
  @Test
  public void notStronglyConnected_1() {
    Graph g = new Graph();
    HashMap<String, Vertex> v = new HashMap<String, Vertex>();
    HashMap<String, Edge> e = new HashMap<String, Edge>();

    v.put("Start", Util.addVertexToGraph(g, "Start"));
    v.put("V1", Util.addVertexToGraph(g, "V1"));
    v.put("V2", Util.addVertexToGraph(g, "V2"));
    v.put("V3", Util.addVertexToGraph(g, "V3"));
    v.put("V4", Util.addVertexToGraph(g, "V4"));
    v.put("V5", Util.addVertexToGraph(g, "V5"));

    e.put("E0", Util.addEdgeToGraph(g, v.get("Start"), v.get("V1"), "E0", null, null, null));
    e.put("E1", Util.addEdgeToGraph(g, v.get("V1"), v.get("V2"), "E1", null, null, null));
    e.put("E2", Util.addEdgeToGraph(g, v.get("V2"), v.get("V3"), "E2", null, null, null));
    e.put("E3", Util.addEdgeToGraph(g, v.get("V3"), v.get("V4"), "E3", null, null, null));
    e.put("E4", Util.addEdgeToGraph(g, v.get("V4"), v.get("V1"), "E4", null, null, null));
    e.put("E5", Util.addEdgeToGraph(g, v.get("V4"), v.get("V5"), "E5", null, null, null));

    assertFalse("The graph should not be strongly connected", Analyze.isStronglyConnected(g));
  }

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
   *         |        +--+--+        +--+--+
   *         +------->| V4  |------->| V5  |
   *            E3    +--+--+  E5    +--+--+
   *                     ^              |
   *                     |              |
   *                     +--------------+
   *                           E6
   */
  @Test
  public void stronglyConnected_3() {
    Graph g = new Graph();
    HashMap<String, Vertex> v = new HashMap<String, Vertex>();
    HashMap<String, Edge> e = new HashMap<String, Edge>();

    v.put("Start", Util.addVertexToGraph(g, "Start"));
    v.put("V1", Util.addVertexToGraph(g, "V1"));
    v.put("V2", Util.addVertexToGraph(g, "V2"));
    v.put("V3", Util.addVertexToGraph(g, "V3"));
    v.put("V4", Util.addVertexToGraph(g, "V4"));
    v.put("V5", Util.addVertexToGraph(g, "V5"));

    e.put("E0", Util.addEdgeToGraph(g, v.get("Start"), v.get("V1"), "E0", null, null, null));
    e.put("E1", Util.addEdgeToGraph(g, v.get("V1"), v.get("V2"), "E1", null, null, null));
    e.put("E2", Util.addEdgeToGraph(g, v.get("V2"), v.get("V3"), "E2", null, null, null));
    e.put("E3", Util.addEdgeToGraph(g, v.get("V3"), v.get("V4"), "E3", null, null, null));
    e.put("E4", Util.addEdgeToGraph(g, v.get("V4"), v.get("V1"), "E4", null, null, null));
    e.put("E5", Util.addEdgeToGraph(g, v.get("V4"), v.get("V5"), "E5", null, null, null));
    e.put("E6", Util.addEdgeToGraph(g, v.get("V5"), v.get("V4"), "E6", null, null, null));

    assertTrue("The graph should be strongly connected", Analyze.isStronglyConnected(g));
  }
}
