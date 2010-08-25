package test.org.graphwalker;

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

	public void testPrintClassPath() {
		assertEquals(true, Util.printClassPath().length() > 0);
	}

	public void testReadPropertySOAP_GUI() {		
		assertEquals(true, Util.readSoapGuiStartupState()==true);
	}
}
