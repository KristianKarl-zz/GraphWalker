package test.org.tigris.mbt.generators;

import org.apache.log4j.Logger;
import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.Keywords;
import org.tigris.mbt.generators.CodeGenerator;
import org.tigris.mbt.Util;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.utils.UserData;
import junit.framework.TestCase;

public class CodeGeneratorTest extends TestCase {
	Logger logger = Util.setupLogger(CodeGeneratorTest.class);
	
	public void testGetNext() {
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
		
		CodeGenerator generator = new CodeGenerator("{EDGE_VERTEX}: {LABEL}");
		generator.setMachine(FSM);

		assertEquals("Edge: E1", generator.getNext()[0]);
		assertEquals("Vertex: V2", generator.getNext()[0]);
		assertFalse(generator.hasNext());
	}

}
