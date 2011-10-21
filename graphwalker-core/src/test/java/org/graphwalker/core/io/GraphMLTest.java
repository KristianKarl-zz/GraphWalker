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

package org.graphwalker.core.io;

import org.graphwalker.core.ModelBasedTesting;
import org.graphwalker.core.graph.Edge;
import org.graphwalker.core.graph.Graph;
import org.graphwalker.core.graph.Vertex;

import junit.framework.TestCase;

public class GraphMLTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ModelBasedTesting.getInstance().reset();
	}

	public void testGraphNode() {
		try {
			GraphML modelHandler = new GraphML();
			modelHandler.load("graphml/multiple/switch/A.graphml");
			assertEquals(modelHandler.getModel().getVertexCount(), 5);
			assertEquals(modelHandler.getModel().getEdgeCount(), 8);
			assertEquals(modelHandler.getModel().getLabelKey(), "v_ClientNotRunning");

			Vertex clientNotRunning = modelHandler.getModel().findVertex("v_ClientNotRunning");
			assertNotNull(clientNotRunning);
			assertTrue(clientNotRunning.isGraphVertex());

			Vertex whatsNew = modelHandler.getModel().findVertex("v_WhatsNew");
			assertNotNull(whatsNew);
			assertFalse(whatsNew.isGraphVertex());

		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	public void testSwitchModelKeyWord() {
		try {
			GraphML modelHandler = new GraphML();
			modelHandler.load("graphml/multiple/switch/A.graphml");
			assertEquals(modelHandler.getModel().getVertexCount(), 5);
			assertEquals(modelHandler.getModel().getEdgeCount(), 8);
			assertEquals(modelHandler.getModel().getLabelKey(), "v_ClientNotRunning");

			Vertex clientNotRunning = modelHandler.getModel().findVertex("v_ClientNotRunning");
			assertNotNull(clientNotRunning);
			assertFalse(clientNotRunning.isSwitchModelKey());

			Vertex whatsNew = modelHandler.getModel().findVertex("v_WhatsNew");
			assertNotNull(whatsNew);
			assertTrue(whatsNew.isSwitchModelKey());

		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	// Test various types of efsm labels
	public void testMergeFiles() {
		try {
			GraphML modelHandler = new GraphML();
			modelHandler.load("graphml/merging");
			assertEquals(modelHandler.getModel().getVertexCount(), 37);
			assertEquals(modelHandler.getModel().getEdgeCount(), 92);
			assertEquals(modelHandler.getModel().getLabelKey(), "v_A");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	// Test various types of efsm labels
	public void testMergeEFSM_Lables() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/efsm_lables/testMergeEFSM_Lables.graphml");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	// Test merging and verifying that unique id's are created
	public void testVerifyIds() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test23");
			verifyIds(mbt.getGraph());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	// Verify that mbt checks that subgraphs are unique.
	public void testUniqueSubGraphs() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test09");
			fail("Missing error message");
		} catch (RuntimeException e) {
			String msg = e.getMessage();
			System.out.println(msg);
			assertTrue(msg
			    .matches("Found 2 subgraphs using the same name: 'B', they are defined in files: '.*graphml.test09.(C|B).graphml', and :'.*graphml.test09.(B|C).graphml'"));
		}
	}

	// Verify that mbt reports and exits when an node without name is found
	public void testEmptyVertexLabel() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test10");
			fail("Missing error message");
		} catch (RuntimeException e) {
			String msg = e.getMessage();
			System.out.println(msg);
			assertTrue(msg.matches("Could not parse file: '.*test10.B\\.graphml'. Vertex is missing mandatory label"));
		}
	}

	// Verify that mbt reports and exits when a recursive subgraph situation
	// emerges
	public void testRecuresiveIncident() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test11");
			fail("Missing error message");
		} catch (RuntimeException e) {
			String msg = e.getMessage();
			System.out.println(msg);
			assertTrue(msg.matches("Found a subgraph containing a duplicate vertex with name: 'C', in file: '.*test11.C\\.graphml'"));
		}
	}

	// Verify that mbt reports and exits when an edge containing a whitespace
	// (tab) is found
	public void testWhiteSpaceInEdgeLabel() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test13");
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	// Verify that mbt reports and exits when a vertex containing a whitespace
	// (tab) is found
	public void testWhiteSpaceInVertexLabel() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test14");
			fail("Missing error message");
		} catch (RuntimeException e) {
			String msg = e.getMessage();
			System.out.println(msg);
			assertTrue(msg
			    .matches("Could not parse file: '.*graphml.test14.C\\.graphml'. Label of vertex: 'Containing a whitespace', containing whitespaces"));
		}
	}

	// Verify that a single graphml file with an edge coming from the START vertex
	// with an empty label is catched.
	public void testMainGraphWithStartVertexOutEdgeEmptyLabel() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test15");
			fail("Missing error message");
		} catch (Exception e) {
			String msg = e.getMessage();
			System.out.println(msg);
			assertTrue(msg.matches("Did not find a Start vertex with an out edge with a label."));
		}
	}

	// Verify that a file with 2 Start vertices is catched. Both Start vertices
	// has edges with labels.
	public void testTwoStartVerticesWithLabels() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test17/test17.graphml");
			fail("Missing error message");
		} catch (Exception e) {
			String msg = e.getMessage();
			System.out.println(msg);
			assertTrue(msg.matches("Only one Start vertex can exist in one file, see file 'graphml/test17/test17.graphml'"));
		}
	}

	// Verify that a file with 2 Start vertices is catched. One edge has a label,
	// the other has not.
	public void testTwoStartVerticesOneLabel() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test18/test18.graphml");
			fail("Missing error message");
		} catch (Exception e) {
			String msg = e.getMessage();
			System.out.println(msg);
			assertTrue(msg.matches("Only one Start vertex can exist in one file, see file 'graphml/test18/test18.graphml'"));
		}
	}

	// Verify that a file with 2 Start vertices is catched. Both edges has no
	// labels.
	public void testTwoStartVerticesNoLabel() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test19/test19.graphml");
			fail("Missing error message");
		} catch (Exception e) {
			String msg = e.getMessage();
			System.out.println(msg);
			assertTrue(msg.matches("Only one Start vertex can exist in one file, see file 'graphml/test19/test19.graphml'"));
		}
	}

	// Verify that a file with 2 out edges from the Start vertex is catched.
	public void testTwoOutEdgesFromStart() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test20/test20.graphml");
			fail("Missing error message");
		} catch (Exception e) {
			String msg = e.getMessage();
			System.out.println(msg);
			assertTrue(msg.matches("A Start vertex can only have one out edge, look in file: graphml/test20/test20.graphml"));
		}
	}

	// Verify that merging of subgraphs works for graphml/mergeSubgraphs_01.
	public void test_mergeSubgraphs_01() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/mergeSubgraphs_01");
			assertTrue(mbt.getGraph().getVertices().size() == 6);
			assertTrue(mbt.getGraph().getEdges().size() == 16);
			verifyIds(mbt.getGraph());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	// Verify that merging of subgraphs works for graphml/mergeSubgraphs_02.
	public void test_mergeSubgraphs_02() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/mergeSubgraphs_02");
			assertTrue(mbt.getGraph().getVertices().size() == 16);
			assertTrue(mbt.getGraph().getEdges().size() == 55);
			verifyIds(mbt.getGraph());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	// Test merging of 2 simple graphs
	public void testMergeSimpleGraph() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test01");
			assertTrue(mbt.getGraph().getEdges().size() == 9);
			assertTrue(mbt.getGraph().getVertices().size() == 7);
			verifyIds(mbt.getGraph());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	// Test merging of 2 simple graphs, with nodes containing key word NO_MERGE
	public void testMergeSimpleGraph_Keyword_NO_MERGE() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test02");
			assertTrue(mbt.getGraph().getEdges().size() == 11);
			assertTrue(mbt.getGraph().getVertices().size() == 8);
			verifyIds(mbt.getGraph());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	// Test merging a folder consisting 162 graphs
	public void xtestMergeLargeGraph() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test03");
			assertTrue(mbt.getGraph().getEdges().size() == 1550);
			assertTrue(mbt.getGraph().getVertices().size() == 788);
			verifyIds(mbt.getGraph());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	// Verify that a graph containing a Stop vertex is correctly merged.
	public void testMergeSubGraphUsingStopVertex() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test22");
			assertTrue(mbt.getGraph().getEdges().size() == 8);
			assertTrue(mbt.getGraph().getVertices().size() == 8);
			verifyIds(mbt.getGraph());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	// Merging with subgraphs containing Stop vertices
	public void testMergeSubGraphUsingStopVertices() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/test23");
			assertTrue(mbt.getGraph().getEdges().size() == 14);
			assertTrue(mbt.getGraph().getVertices().size() == 9);
			verifyIds(mbt.getGraph());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	//
	public void testGetDescriptionVertex() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/modelWithDescr.graphml");
			assertTrue(mbt.getGraph().findVertex("v_WithDescription").getDescriptionKey()
			    .equals("<ul>\nA very fine description in a vertex\n</ul>"));
			assertTrue(mbt.getGraph().findVertex("v_WithNoDescription").getDescriptionKey().isEmpty());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	public void testGetDescriptionEdge() {
		try {
			ModelBasedTesting mbt = ModelBasedTesting.getInstance();
			mbt.readGraph("graphml/modelWithDescr.graphml");
			assertTrue(mbt.getGraph().findEdge("e_WithDescription").getDescriptionKey().equals("<ul>\nA very fine description in a edge\n</ul>"));
			assertTrue(mbt.getGraph().findEdge("e_WithNoDescription").getDescriptionKey().isEmpty());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}

	// Verify that all vertices and edges has indexes, and that no duplicates
	// exists.
	private void verifyIds(Graph g) {
		Object[] vertices1 = g.getVertices().toArray();
		for (int i = 0; i < vertices1.length; i++) {
			Vertex v1 = (Vertex) vertices1[i];
			int hits = 0;
			Integer index1 = v1.getIndexKey();
			Object[] vertices2 = g.getVertices().toArray();
			for (int j = 0; j < vertices1.length; j++) {
				Vertex v2 = (Vertex) vertices2[j];
				Integer index2 = v2.getIndexKey();
				if (index1.intValue() == index2.intValue()) {
					hits++;
				}
			}
			assertTrue(hits == 1);

			Object[] edges = g.getEdges().toArray();
			for (int j = 0; j < edges.length; j++) {
				Edge e = (Edge) edges[j];
				Integer index2 = e.getIndexKey();
				if (index1.intValue() == index2.intValue()) {
					hits++;
				}
			}
			assertTrue(hits == 1);
		}

		Object[] edges1 = g.getEdges().toArray();
		for (int i = 0; i < edges1.length; i++) {
			Edge e1 = (Edge) edges1[i];
			int hits = 0;
			Integer index1 = e1.getIndexKey();
			Object[] edges2 = g.getEdges().toArray();
			for (int j = 0; j < edges2.length; j++) {
				Edge e2 = (Edge) edges2[j];
				Integer index2 = e2.getIndexKey();
				if (index1.intValue() == index2.intValue()) {
					hits++;
				}
			}
			assertTrue(hits == 1);

			Object[] vertices2 = g.getVertices().toArray();
			for (int j = 0; j < vertices1.length; j++) {
				Vertex v2 = (Vertex) vertices2[j];
				Integer index2 = v2.getIndexKey();
				if (index1.intValue() == index2.intValue()) {
					hits++;
				}
			}
			assertTrue(hits == 1);
		}
	}
}
