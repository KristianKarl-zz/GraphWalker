package test.org.tigris.mbt;

import org.tigris.mbt.ExtendedFiniteStateMachine;
import org.tigris.mbt.Keywords;
import org.tigris.mbt.Util;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.utils.UserData;

import junit.framework.TestCase;

public class ExtendedFiniteStateMachineTest extends TestCase {

	SparseGraph graph;
	DirectedSparseVertex start;
	DirectedSparseVertex v1;
	DirectedSparseVertex v2;
	DirectedSparseEdge e1;
	DirectedSparseEdge e2;
	DirectedSparseEdge e3;
	DirectedSparseEdge e4;
	
	protected void setUp() throws Exception {
		super.setUp();
		graph = new SparseGraph();
		
		start = Util.addVertexToGraph(graph, "Start");
		
		v1 = Util.addVertexToGraph(graph, "V1");
		v1.setUserDatum(Keywords.REQTAG_KEY, "REQ002", UserData.SHARED);

		v2 = Util.addVertexToGraph(graph, "V2");
		v2.setUserDatum(Keywords.REQTAG_KEY, "REQ004", UserData.SHARED);

		e1 = Util.addEdgeToGraph(graph, start, v1, "E1", null, null, "x=1;y=new Vector()");
		e1.setUserDatum(Keywords.REQTAG_KEY, "REQ001,REQ002", UserData.SHARED);
		
		e2 = Util.addEdgeToGraph(graph, v1, v2, "E2", null, null, "x=2");
		e2.setUserDatum(Keywords.REQTAG_KEY, "REQ003", UserData.SHARED);

		e3 = Util.addEdgeToGraph(graph, v2, v2, "E3", null, "x<6", "x++");

		e4 = Util.addEdgeToGraph(graph, v2, v1, "E4", null, "y.size()<3", "y.add(x)");
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
