package test.org.tigris.mbt;

import org.tigris.mbt.filters.AccessableEdgeFilter;
import org.tigris.mbt.Keywords;

import bsh.EvalError;
import bsh.Interpreter;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserData;
import junit.framework.TestCase;

public class AccessableEdgeFilterTest extends TestCase {
	private AccessableEdgeFilter f;
	private DirectedSparseEdge e;
	private Interpreter dataStore;
	
	protected void setUp() throws Exception {
		super.setUp();
		dataStore = new Interpreter();
		
		DirectedSparseVertex v1 = new DirectedSparseVertex();
		DirectedSparseVertex v2 = new DirectedSparseVertex();
		SparseGraph g = new SparseGraph();
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
	
	public void testAcceptEdgeEdge7() throws EvalError 
	{
		dataStore.eval("X=false");
		e.setUserDatum(Keywords.LABEL_KEY, "[X]", UserData.SHARED);
		assertEquals(false, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge8() throws EvalError 
	{
		dataStore.eval("X=true");
		e.setUserDatum(Keywords.LABEL_KEY, "[X]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge9()  throws EvalError
	{
		dataStore.eval("X=true");
		e.setUserDatum(Keywords.LABEL_KEY, "[X==true]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge10()  throws EvalError
	{
		dataStore.eval("X=true");
		e.setUserDatum(Keywords.LABEL_KEY, "[X==false]", UserData.SHARED);
		assertEquals(false, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge11()  throws EvalError
	{
		dataStore.eval("X=true");
		dataStore.eval("Y=true");
		e.setUserDatum(Keywords.LABEL_KEY, "[X==Y]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge12()  throws EvalError
	{
		dataStore.eval("X=true");
		dataStore.eval("Y=false");
		e.setUserDatum(Keywords.LABEL_KEY, "[X==Y]", UserData.SHARED);
		assertEquals(false, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge13() throws EvalError 
	{
		dataStore.eval("X=5");
		e.setUserDatum(Keywords.LABEL_KEY, "[X==5]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge14()  throws EvalError
	{
		dataStore.eval("X=6");
		e.setUserDatum(Keywords.LABEL_KEY, "[X==5]", UserData.SHARED);
		assertEquals(false, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge15()  throws EvalError
	{
		dataStore.eval("X=6");
		e.setUserDatum(Keywords.LABEL_KEY, "[X>5]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}

	public void testAcceptEdgeEdge16()  throws EvalError
	{
		dataStore.eval("X=4");
		e.setUserDatum(Keywords.LABEL_KEY, "[X<5]", UserData.SHARED);
		assertEquals(true, f.acceptEdge(e));
	}
}
