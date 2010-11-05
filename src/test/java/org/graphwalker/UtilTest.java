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

package org.graphwalker;

import org.apache.log4j.Logger;
import org.graphwalker.Util;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;

import junit.framework.TestCase;

public class UtilTest extends TestCase {

	public void testAbortIf_1() {
		try {
			Util.AbortIf(true, "Working");
			fail("expected error message");
		} catch (Exception e) {
			assertEquals("Working", e.getMessage());
		}
	}

	public void testAbortIf_2() {
		Util.AbortIf(false, "Working");
	}

	public void testGetCompleteEdgeName() {
		Graph graph = new Graph();
		Vertex v1 = new Vertex();
		v1.setIndexKey(new Integer(1));
		v1.setLabelKey("V1");
		graph.addVertex(v1);
		Vertex v2 = new Vertex();
		v2.setIndexKey(new Integer(2));
		v2.setLabelKey("V2");
		graph.addVertex(v2);
		Edge edge = new Edge();
		edge.setIndexKey(new Integer(3));
		edge.setLabelKey("E1");
		graph.addEdge(edge, v1, v2);

		assertEquals("Edge: 'E1', INDEX=3", edge.toString());
	}

	public void testGetCompleteVertexName() {
		Graph graph = new Graph();
		Vertex v1 = new Vertex();
		v1.setIndexKey(new Integer(1));
		v1.setLabelKey("V1");
		graph.addVertex(v1);

		assertEquals("Vertex: 'V1', INDEX=1", v1.toString());
	}

	public void testSetupLogger() {
		Logger logger = Util.setupLogger(UtilTest.class);
		logger.debug("Working");
	}

	public void testReadPropertySOAP_GUI() {		
		assertEquals(true, Util.readSoapGuiStartupState()==true||Util.readSoapGuiStartupState()==false);
	}
}
