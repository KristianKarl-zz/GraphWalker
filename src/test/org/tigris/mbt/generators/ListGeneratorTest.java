package test.org.tigris.mbt.generators;

import org.apache.log4j.Logger;
import org.tigris.mbt.Util;
import org.tigris.mbt.generators.ListGenerator;
import org.tigris.mbt.graph.Edge;
import org.tigris.mbt.graph.Graph;
import org.tigris.mbt.graph.Vertex;
import org.tigris.mbt.machines.FiniteStateMachine;

import junit.framework.TestCase;

public class ListGeneratorTest extends TestCase {
	Logger logger = Util.setupLogger(ListGeneratorTest.class);

	public void testGetNext() {
		Graph graph = new Graph();
		
		Vertex v1 = new Vertex();
		v1.setIndexKey( new Integer(1) );
		v1.setLabelKey( "Start" );
		graph.addVertex(v1);
		
		Vertex v2 = new Vertex();
		v2.setIndexKey( new Integer(2) );
		v2.setLabelKey( "V2" );
		graph.addVertex(v2);
		
		Edge edge = new Edge();
		edge.setIndexKey( new Integer(3) );
		edge.setLabelKey( "E1" );
		graph.addEdge(edge, v1, v2 );
		
		ListGenerator generator = new ListGenerator();
		generator.setMachine(new FiniteStateMachine(graph));

		String[] s = generator.getNext();
		assertEquals("E1", s[0]);
		assertEquals("Edge", s[1]);
		s = generator.getNext();
		assertEquals("V2", s[0]);
		assertEquals("Vertex", s[1]);
		assertFalse(generator.hasNext());
	}
}
