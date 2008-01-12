package test.org.tigris.mbt;

import java.util.Vector;

import org.tigris.mbt.ExtendedFiniteStateMachine;
import org.tigris.mbt.Keywords;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.utils.UserData;

import junit.framework.TestCase;

public class ExtendedFiniteStateMachineTest extends TestCase {

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
		e3.setUserDatum(Keywords.GUARD_KEY, "x<6", UserData.SHARED);
		graph.addEdge(e3);
		e4 = new DirectedSparseEdge(v2, v1);		
		e4.setUserDatum(Keywords.INDEX_KEY, new Integer(7), UserData.SHARED);
		e4.setUserDatum(Keywords.LABEL_KEY, "E4", UserData.SHARED);
		e4.setUserDatum(Keywords.ACTIONS_KEY, "y.add(x)", UserData.SHARED);
		e4.setUserDatum(Keywords.GUARD_KEY, "y.size()<3", UserData.SHARED);
		graph.addEdge(e4);
	}

	public void testConstructor() 
	{
		new ExtendedFiniteStateMachine(graph);
	}

	public void testRequirements() 
	{
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(graph);
		assertEquals("{REQ001=0, REQ004=0, REQ003=0, REQ002=0}", EFSM.getAllRequirements().toString());
		assertEquals("[]", EFSM.getCoveredRequirements().toString());
	}

	public void testRequirementsWalk() 
	{
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(graph);
		assertEquals("Start",EFSM.getCurrentStateName());
		EFSM.walkEdge(e1);
		assertEquals("V1/x=1;y=[];",EFSM.getCurrentStateName());
		assertEquals("{REQ001=1, REQ004=0, REQ003=0, REQ002=2}", EFSM.getAllRequirements().toString());
		assertEquals("[REQ001, REQ002]", EFSM.getCoveredRequirements().toString());
		EFSM.walkEdge(e2);
		assertEquals("V2/x=2;y=[];",EFSM.getCurrentStateName());
		assertEquals("{REQ001=1, REQ004=1, REQ003=1, REQ002=2}", EFSM.getAllRequirements().toString());
		assertEquals("[REQ001, REQ004, REQ003, REQ002]", EFSM.getCoveredRequirements().toString());
	}


	public void testBacktrackWalk() 
	{
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(graph);
		EFSM.setBacktrack(true);
		assertEquals("Start",EFSM.getCurrentStateName());
		EFSM.walkEdge(e1);
		assertEquals("V1/x=1;y=[];",EFSM.getCurrentStateName());
		EFSM.walkEdge(e2);
		assertEquals("V2/x=2;y=[];",EFSM.getCurrentStateName());
		EFSM.walkEdge(e3);
		assertEquals("V2/x=3;y=[];",EFSM.getCurrentStateName());
		EFSM.backtrack();
		assertEquals("V2/x=2;y=[];",EFSM.getCurrentStateName());
		EFSM.backtrack();
		assertEquals("V1/x=1;y=[];",EFSM.getCurrentStateName());
		EFSM.backtrack();
		assertEquals("Start",EFSM.getCurrentStateName());
	}

	public void testBacktrackWalk2() 
	{
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(graph);
		EFSM.setBacktrack(true);
		assertEquals("Start",EFSM.getCurrentStateName());
		EFSM.walkEdge(e1);
		assertEquals("V1/x=1;y=[];",EFSM.getCurrentStateName());
		EFSM.walkEdge(e2);
		assertEquals("V2/x=2;y=[];",EFSM.getCurrentStateName());
		EFSM.walkEdge(e3);
		assertEquals("V2/x=3;y=[];",EFSM.getCurrentStateName());
		EFSM.walkEdge(e4);
		assertEquals("V1/x=3;y=[3];",EFSM.getCurrentStateName());
		EFSM.backtrack();
		assertEquals("V2/x=3;y=[];",EFSM.getCurrentStateName());
		EFSM.backtrack();
		assertEquals("V2/x=2;y=[];",EFSM.getCurrentStateName());
		EFSM.backtrack();
		assertEquals("V1/x=1;y=[];",EFSM.getCurrentStateName());
		EFSM.backtrack();
		assertEquals("Start",EFSM.getCurrentStateName());
	}

}
