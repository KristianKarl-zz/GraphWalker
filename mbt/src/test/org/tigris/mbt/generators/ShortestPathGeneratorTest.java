package test.org.tigris.mbt.generators;

import org.tigris.mbt.ExtendedFiniteStateMachine;
import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.Util;
import org.tigris.mbt.conditions.ReachedEdge;
import org.tigris.mbt.conditions.ReachedState;
import org.tigris.mbt.generators.PathGenerator;
import org.tigris.mbt.generators.ShortestPathGenerator;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;

import junit.framework.TestCase;

public class ShortestPathGeneratorTest extends TestCase {

	SparseGraph graph;
	DirectedSparseVertex start;
	DirectedSparseVertex v1;
	DirectedSparseVertex v2;
	DirectedSparseEdge e0;
	DirectedSparseEdge e1;
	DirectedSparseEdge e2;
	DirectedSparseEdge e3;
	
	protected void setUp() throws Exception {
		super.setUp();
		graph = new SparseGraph();
		
		start = Util.addVertexToGraph(graph, "Start");
		v1 = Util.addVertexToGraph(graph, "V1");
		v2 = Util.addVertexToGraph(graph, "V2");

		e0 = Util.addEdgeToGraph(graph, start, v1, "E0", null, null, "x=1;y=new Vector()");
		e1 = Util.addEdgeToGraph(graph, v1, v2, "E1", null, null, "x=2");
		e2 = Util.addEdgeToGraph(graph, v2, v2, "E2", null, "x<4", "x++");
		e3 = Util.addEdgeToGraph(graph, v2, v1, "E3", null, "y.size()<3", "y.add(x)");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		graph.removeAllEdges();
		graph.removeAllVertices();
		start = v1 = v2 = null; 
		e0 = e1 = e2 = e3 = null;
	}
	public void test_FSM_StateStop()
    {
		FiniteStateMachine FSM = new FiniteStateMachine(graph);
		FSM.setWeighted(false);
		PathGenerator pathGenerator = new ShortestPathGenerator(FSM, new ReachedState(FSM, "V2") );
		
		String[] stepPair;
		stepPair = pathGenerator.getNext();
		assertEquals("E0",  stepPair[0]);
		assertEquals("V1", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E1",  stepPair[0]);
		assertEquals("V2", stepPair[1]);
		assertFalse(pathGenerator.hasNext());
    }

	public void test_FSM_EdgeStop()
    {
		FiniteStateMachine FSM = new FiniteStateMachine(graph);
		FSM.setWeighted(false);
		PathGenerator pathGenerator = new ShortestPathGenerator(FSM, new ReachedEdge(FSM, "E2") );
		
		String[] stepPair;
		stepPair = pathGenerator.getNext();
		assertEquals("E0",  stepPair[0]);
		assertEquals("V1", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E1",  stepPair[0]);
		assertEquals("V2", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E2",  stepPair[0]);
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
		assertEquals("E0",  stepPair[0]);
		assertEquals("V1/x=1;y=[];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E1",  stepPair[0]);
		assertEquals("V2/x=2;y=[];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E3",  stepPair[0]);
		assertEquals("V1/x=2;y=[2];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E1",  stepPair[0]);
		assertEquals("V2/x=2;y=[2];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E2",  stepPair[0]);
		assertEquals("V2/x=3;y=[2];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E3",  stepPair[0]);
		assertEquals("V1/x=3;y=[2, 3];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E1",  stepPair[0]);
		assertEquals("V2/x=2;y=[2, 3];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E2",  stepPair[0]);
		assertEquals("V2/x=3;y=[2, 3];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E3",  stepPair[0]);
		assertEquals("V1/x=3;y=[2, 3, 3];",  stepPair[1]);
		assertFalse(pathGenerator.hasNext());

    }

	public void test_EFSM_EdgeStop()
    {
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(graph);
		EFSM.setWeighted(false);
		PathGenerator pathGenerator = new ShortestPathGenerator(EFSM, new ReachedEdge(EFSM, "E2") );
		
		String[] stepPair;
		stepPair = pathGenerator.getNext();
		assertEquals("E0",  stepPair[0]);
		assertEquals("V1/x=1;y=[];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E1",  stepPair[0]);
		assertEquals("V2/x=2;y=[];",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("E2",  stepPair[0]);
		assertEquals("V2/x=3;y=[];",  stepPair[1]);
		assertFalse(pathGenerator.hasNext());

    }

}
