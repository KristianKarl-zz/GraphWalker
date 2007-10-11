package test.org.tigris.mbt;

import java.util.Hashtable;

import org.tigris.mbt.filters.AccessableEdgeFilter;
import org.tigris.mbt.Keywords;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserData;
import junit.framework.TestCase;

public class AccessableEdgeFilterTest extends TestCase {
	private AccessableEdgeFilter f;
	private DirectedSparseEdge e;
	private Hashtable dataStore;
	
	protected void setUp() throws Exception {
		super.setUp();
		dataStore = new Hashtable();
		
		DirectedSparseVertex v1 = new DirectedSparseVertex();
		DirectedSparseVertex v2 = new DirectedSparseVertex();
		DirectedSparseGraph g = new DirectedSparseGraph();
		g.addVertex(v1);
		g.addVertex(v2);
		
		e = new DirectedSparseEdge(v1, v2);
		g.addEdge(e);

		e.addUserDatum(Keywords.LABEL_KEY, "", UserData.SHARED);

		f = new AccessableEdgeFilter(dataStore);
	}

	public void testAcceptEdgeEdge1()
	{
		e.setUserDatum(Keywords.LABEL_KEY, "", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge2() 
	{
		e.setUserDatum(Keywords.LABEL_KEY, "test", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}
	
	public void testAcceptEdgeEdge3() 
	{
		e.setUserDatum(Keywords.LABEL_KEY, "test [true]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}
	
	public void testAcceptEdgeEdge4() 
	{
		e.setUserDatum(Keywords.LABEL_KEY, "[true]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge5() 
	{
		e.setUserDatum(Keywords.LABEL_KEY, "[true] / test", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge6() 
	{
		e.setUserDatum(Keywords.LABEL_KEY, "[false]", UserData.SHARED);
		assertEquals(false, f.acceptEdge(e));
	}
	
	public void testAcceptEdgeEdge7() 
	{
		dataStore.put("X", "false");
		e.setUserDatum(Keywords.LABEL_KEY, "[X]", UserData.SHARED);
		assertEquals(false, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge8() 
	{
		dataStore.put("X", "true");
		e.setUserDatum(Keywords.LABEL_KEY, "[X]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge9() 
	{
		dataStore.put("X", "true");
		e.setUserDatum(Keywords.LABEL_KEY, "[X=true]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge10() 
	{
		dataStore.put("X", "true");
		e.setUserDatum(Keywords.LABEL_KEY, "[X=false]", UserData.SHARED);
		assertEquals(false, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge11() 
	{
		dataStore.put("X", "true");
		dataStore.put("Y", "true");
		e.setUserDatum(Keywords.LABEL_KEY, "[X=Y]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge12() 
	{
		dataStore.put("X", "true");
		dataStore.put("Y", "false");
		e.setUserDatum(Keywords.LABEL_KEY, "[X=Y]", UserData.SHARED);
		assertEquals(false, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge13() 
	{
		dataStore.put("X", "5");
		e.setUserDatum(Keywords.LABEL_KEY, "[X=5]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge14() 
	{
		dataStore.put("X", "6");
		e.setUserDatum(Keywords.LABEL_KEY, "[X=5]", UserData.SHARED);
		assertEquals(false, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge15() 
	{
		dataStore.put("X", "6");
		e.setUserDatum(Keywords.LABEL_KEY, "[X>5]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge16() 
	{
		dataStore.put("X", "4");
		e.setUserDatum(Keywords.LABEL_KEY, "[X<5]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}
}
