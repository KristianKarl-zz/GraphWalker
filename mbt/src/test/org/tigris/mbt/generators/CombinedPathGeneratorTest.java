/**
 * 
 */
package test.org.tigris.mbt.generators;

import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.Keywords;
import org.tigris.mbt.generators.CodeGenerator;
import org.tigris.mbt.generators.CombinedPathGenerator;
import org.tigris.mbt.generators.ListGenerator;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.utils.UserData;
import junit.framework.TestCase;

/**
 * @author Johan Tejle
 *
 */
public class CombinedPathGeneratorTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testCodeList()
	{
	SparseGraph graph = new SparseGraph();
	
	DirectedSparseVertex v1 = new DirectedSparseVertex();
	v1.setUserDatum(Keywords.INDEX_KEY, new Integer(1), UserData.SHARED);
	v1.setUserDatum(Keywords.LABEL_KEY, "Start", UserData.SHARED);
	graph.addVertex(v1);
	
	DirectedSparseVertex v2 = new DirectedSparseVertex();
	v2.addUserDatum(Keywords.INDEX_KEY, new Integer(2), UserData.SHARED);
	v2.setUserDatum(Keywords.LABEL_KEY, "V2", UserData.SHARED);
	graph.addVertex(v2);
	
	DirectedSparseEdge edge = new DirectedSparseEdge(v1, v2);
	edge.setUserDatum(Keywords.INDEX_KEY, new Integer(3), UserData.SHARED);
	edge.setUserDatum(Keywords.LABEL_KEY, "E1", UserData.SHARED);
	graph.addEdge(edge);
	
	FiniteStateMachine FSM = new FiniteStateMachine(graph);
	
	CodeGenerator generator1 = new CodeGenerator(FSM, "{EDGE_VERTEX}: {LABEL}");
	ListGenerator generator2 = new ListGenerator(FSM);
	
	CombinedPathGenerator pathGenerator = new CombinedPathGenerator(generator1);
	pathGenerator.addPathGenerator(generator2);
	
	String[] stepPair;
	
	stepPair = pathGenerator.getNext();
	assertEquals("E1",  stepPair[0]);
	assertEquals("Edge",  stepPair[1]);
	stepPair = pathGenerator.getNext();
	assertEquals("V2",  stepPair[0]);
	assertEquals("Vertex",  stepPair[1]);
	stepPair = pathGenerator.getNext();
	assertEquals("Edge: E1",  stepPair[0]);
	assertEquals("Edge",  stepPair[1]);
	stepPair = pathGenerator.getNext();
	assertEquals("Vertex: V2",  stepPair[0]);
	assertEquals("Vertex",  stepPair[1]);
	assertFalse(pathGenerator.hasNext());

	}
}
