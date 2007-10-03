package test.org.tigris.mbt;

import org.tigris.mbt.AccessableEdgeFilter;
import org.tigris.mbt.AbstractModel;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserData;
import junit.framework.TestCase;

public class AccessableEdgeFilterTest extends TestCase {
	private AccessableEdgeFilter f;
	private DirectedSparseEdge e;
	
	protected void setUp() throws Exception {
		super.setUp();
		DirectedSparseVertex v1 = new DirectedSparseVertex();
		DirectedSparseVertex v2 = new DirectedSparseVertex();
		DirectedSparseGraph g = new DirectedSparseGraph();
		g.addVertex(v1);
		g.addVertex(v2);
		
		e = new DirectedSparseEdge(v1, v2);
		g.addEdge(e);

		e.addUserDatum(AbstractModel.LABEL_KEY, "", UserData.SHARED);

		f = new AccessableEdgeFilter();
	}

	public void testAcceptEdgeEdge1()
	{
		e.setUserDatum(AbstractModel.LABEL_KEY, "", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge2() 
	{
		e.setUserDatum(AbstractModel.LABEL_KEY, "test", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}
	
	public void testAcceptEdgeEdge3() 
	{
		e.setUserDatum(AbstractModel.LABEL_KEY, "test [true]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}
	
	public void testAcceptEdgeEdge4() 
	{
		e.setUserDatum(AbstractModel.LABEL_KEY, "[true]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge5() 
	{
		e.setUserDatum(AbstractModel.LABEL_KEY, "[true] / test", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge6() 
	{
		e.setUserDatum(AbstractModel.LABEL_KEY, "[false]", UserData.SHARED);
		assertEquals(false, f.acceptEdge(e));
	}
	
	public void testAcceptEdgeEdge7() 
	{
		AbstractModel.setDataStore("X", "false");
		e.setUserDatum(AbstractModel.LABEL_KEY, "[X]", UserData.SHARED);
		assertEquals(false, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge8() 
	{
		AbstractModel.setDataStore("X", "true");
		e.setUserDatum(AbstractModel.LABEL_KEY, "[X]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge9() 
	{
		AbstractModel.setDataStore("X", "true");
		e.setUserDatum(AbstractModel.LABEL_KEY, "[X=true]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge10() 
	{
		AbstractModel.setDataStore("X", "true");
		e.setUserDatum(AbstractModel.LABEL_KEY, "[X=false]", UserData.SHARED);
		assertEquals(false, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge11() 
	{
		AbstractModel.setDataStore("X", "true");
		AbstractModel.setDataStore("Y", "true");
		e.setUserDatum(AbstractModel.LABEL_KEY, "[X=Y]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge12() 
	{
		AbstractModel.setDataStore("X", "true");
		AbstractModel.setDataStore("Y", "false");
		e.setUserDatum(AbstractModel.LABEL_KEY, "[X=Y]", UserData.SHARED);
		assertEquals(false, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge13() 
	{
		AbstractModel.setDataStore("X", "5");
		e.setUserDatum(AbstractModel.LABEL_KEY, "[X=5]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge14() 
	{
		AbstractModel.setDataStore("X", "6");
		e.setUserDatum(AbstractModel.LABEL_KEY, "[X=5]", UserData.SHARED);
		assertEquals(false, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge15() 
	{
		AbstractModel.setDataStore("X", "6");
		e.setUserDatum(AbstractModel.LABEL_KEY, "[X>5]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge16() 
	{
		AbstractModel.setDataStore("X", "4");
		e.setUserDatum(AbstractModel.LABEL_KEY, "[X<5]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}
}
