package test.org.tigris.mbt;

import org.apache.log4j.Logger;
import org.tigris.mbt.Keywords;
import org.tigris.mbt.Util;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.utils.UserData;

import junit.framework.TestCase;

public class UtilTest extends TestCase {

	public void testAbortIf_1()
    {
		try{
	    	Util.AbortIf(true, "Working");
	    	fail("expected error message");
		}catch(Exception e)
		{
			assertEquals("Working", e.getMessage());
		}
    }
	
	public void testAbortIf_2()
    {
    	Util.AbortIf(false, "Working");
    }

	public void testGetCompleteEdgeName()
    {
		SparseGraph graph = new SparseGraph();
		DirectedSparseVertex v1 = new DirectedSparseVertex();
		v1.setUserDatum(Keywords.INDEX_KEY, new Integer(1), UserData.SHARED);
		v1.setUserDatum(Keywords.LABEL_KEY, "V1", UserData.SHARED);
		graph.addVertex(v1);
		DirectedSparseVertex v2 = new DirectedSparseVertex();
		v2.addUserDatum(Keywords.INDEX_KEY, new Integer(2), UserData.SHARED);
		v2.setUserDatum(Keywords.LABEL_KEY, "V2", UserData.SHARED);
		graph.addVertex(v2);
		DirectedSparseEdge edge = new DirectedSparseEdge(v1, v2);
		edge.setUserDatum(Keywords.INDEX_KEY, new Integer(3), UserData.SHARED);
		edge.setUserDatum(Keywords.LABEL_KEY, "E1", UserData.SHARED);
		graph.addEdge(edge);

		assertEquals("'E1', INDEX=3 ('V1', INDEX=1 -> 'V2', INDEX=2)", Util.getCompleteEdgeName(edge));
    }

	public void testGetCompleteVertexName()
    {
		SparseGraph graph = new SparseGraph();
		DirectedSparseVertex v1 = new DirectedSparseVertex();
		v1.setUserDatum(Keywords.INDEX_KEY, new Integer(1), UserData.SHARED);
		v1.setUserDatum(Keywords.LABEL_KEY, "V1", UserData.SHARED);
		graph.addVertex(v1);
		
		assertEquals("'V1', INDEX=1", Util.getCompleteVertexName(v1));
    }
	public void testSetupLogger()
    {
    	Logger logger = Util.setupLogger(UtilTest.class);
    	logger.debug("Working");
    }
}
