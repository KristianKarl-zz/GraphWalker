package test.org.tigris.mbt.generators;

import org.tigris.mbt.ExtendedFiniteStateMachine;
import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.Keywords;
import org.tigris.mbt.conditions.ReachedEdge;
import org.tigris.mbt.conditions.ReachedState;
import org.tigris.mbt.generators.PathGenerator;
import org.tigris.mbt.generators.ShortestPathGenerator;
import org.tigris.mbt.io.GraphML;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.utils.UserData;

import junit.framework.TestCase;

public class ShortestPathGeneratorTest extends TestCase {

	SparseGraph graph;
	DirectedSparseEdge e1;
	DirectedSparseEdge e2;
	DirectedSparseEdge e3;
	DirectedSparseEdge e4;
	
	protected void setUp() throws Exception {
		super.setUp();
		graph = new SparseGraph();
		DirectedSparseVertex start = new DirectedSparseVertex();
		start.setUserDatum(Keywords.INDEX_KEY, new Integer(1), UserData.SHARED);
		start.setUserDatum(Keywords.LABEL_KEY, "Start", UserData.SHARED);
		graph.addVertex(start);
		DirectedSparseVertex v1 = new DirectedSparseVertex();
		v1.addUserDatum(Keywords.INDEX_KEY, new Integer(2), UserData.SHARED);
		v1.setUserDatum(Keywords.LABEL_KEY, "V1", UserData.SHARED);
		v1.setUserDatum(Keywords.REQTAG_KEY, "REQ002", UserData.SHARED);
		graph.addVertex(v1);
		DirectedSparseVertex v2 = new DirectedSparseVertex();
		v2.addUserDatum(Keywords.INDEX_KEY, new Integer(3), UserData.SHARED);
		v2.setUserDatum(Keywords.LABEL_KEY, "V2", UserData.SHARED);
		v2.setUserDatum(Keywords.REQTAG_KEY, "REQ004", UserData.SHARED);
		graph.addVertex(v2);
		e1 = new DirectedSparseEdge(start, v1);
		e1.setUserDatum(Keywords.INDEX_KEY, new Integer(4), UserData.SHARED);
		e1.setUserDatum(Keywords.LABEL_KEY, "E1", UserData.SHARED);
		e1.setUserDatum(Keywords.ACTIONS_KEY, "x=1;y=new Vector()", UserData.SHARED);
		e1.setUserDatum(Keywords.REQTAG_KEY, "REQ001,REQ002", UserData.SHARED);
		graph.addEdge(e1);
		e2 = new DirectedSparseEdge(v1, v2);
		e2.setUserDatum(Keywords.INDEX_KEY, new Integer(5), UserData.SHARED);
		e2.setUserDatum(Keywords.LABEL_KEY, "E2", UserData.SHARED);
		e2.setUserDatum(Keywords.ACTIONS_KEY, "x=2", UserData.SHARED);
		e2.setUserDatum(Keywords.REQTAG_KEY, "REQ003", UserData.SHARED);
		graph.addEdge(e2);
		e3 = new DirectedSparseEdge(v2, v2);
		e3.setUserDatum(Keywords.INDEX_KEY, new Integer(6), UserData.SHARED);
		e3.setUserDatum(Keywords.LABEL_KEY, "E3", UserData.SHARED);
		e3.setUserDatum(Keywords.ACTIONS_KEY, "x++", UserData.SHARED);
		e3.setUserDatum(Keywords.GUARD_KEY, "x<4", UserData.SHARED);
		graph.addEdge(e3);
		e4 = new DirectedSparseEdge(v2, v1);		
		e4.setUserDatum(Keywords.INDEX_KEY, new Integer(7), UserData.SHARED);
		e4.setUserDatum(Keywords.LABEL_KEY, "E4", UserData.SHARED);
		e4.setUserDatum(Keywords.ACTIONS_KEY, "y.add(x)", UserData.SHARED);
		e4.setUserDatum(Keywords.GUARD_KEY, "y.size()<3", UserData.SHARED);
		graph.addEdge(e4);
	}

	public void test_FSM_StateStop()
    {
		FiniteStateMachine FSM = new FiniteStateMachine(graph);
		FSM.setWeighted(false);
		PathGenerator pathGenerator = new ShortestPathGenerator(FSM, new ReachedState(FSM, "V2") );
		
		String[] stepPair;
		stepPair = pathGenerator.getNext();
		assertEquals("E1",  stepPair[0]);
		assertEquals("V1", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E2",  stepPair[0]);
		assertEquals("V2", stepPair[1]);
		assertFalse(pathGenerator.hasNext());
    }

	public void test_FSM_EdgeStop()
    {
		FiniteStateMachine FSM = new FiniteStateMachine(graph);
		FSM.setWeighted(false);
		PathGenerator pathGenerator = new ShortestPathGenerator(FSM, new ReachedEdge(FSM, "E3") );
		
		String[] stepPair;
		stepPair = pathGenerator.getNext();
		assertEquals("E1",  stepPair[0]);
		assertEquals("V1", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E2",  stepPair[0]);
		assertEquals("V2", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E3",  stepPair[0]);
		assertEquals("V2", stepPair[1]);
		assertFalse(pathGenerator.hasNext());
    }

	public void test_EFSM_StateStop()
    {
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(graph);
		EFSM.setWeighted(false);
		PathGenerator pathGenerator = new ShortestPathGenerator(EFSM, new ReachedState(EFSM, "V1/x=3;y=[2, 3, 3];") );
		
		String[] stepPair;
		stepPair = pathGenerator.getNext();
		assertEquals("E1",  stepPair[0]);
		assertEquals("V1/x=1;y=[];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E2",  stepPair[0]);
		assertEquals("V2/x=2;y=[];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E4",  stepPair[0]);
		assertEquals("V1/x=2;y=[2];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E2",  stepPair[0]);
		assertEquals("V2/x=2;y=[2];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E3",  stepPair[0]);
		assertEquals("V2/x=3;y=[2];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E4",  stepPair[0]);
		assertEquals("V1/x=3;y=[2, 3];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E2",  stepPair[0]);
		assertEquals("V2/x=2;y=[2, 3];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E3",  stepPair[0]);
		assertEquals("V2/x=3;y=[2, 3];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E4",  stepPair[0]);
		assertEquals("V1/x=3;y=[2, 3, 3];",  stepPair[1]);
		assertFalse(pathGenerator.hasNext());

    }

	public void test_EFSM_EdgeStop()
    {
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(graph);
		EFSM.setWeighted(false);
		PathGenerator pathGenerator = new ShortestPathGenerator(EFSM, new ReachedEdge(EFSM, "E3") );
		
		String[] stepPair;
		stepPair = pathGenerator.getNext();
		assertEquals("E1",  stepPair[0]);
		assertEquals("V1/x=1;y=[];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E2",  stepPair[0]);
		assertEquals("V2/x=2;y=[];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E3",  stepPair[0]);
		assertEquals("V2/x=3;y=[];",  stepPair[1]);
		assertFalse(pathGenerator.hasNext());

    }

}
