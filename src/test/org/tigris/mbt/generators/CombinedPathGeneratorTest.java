/**
 * 
 */
package test.org.tigris.mbt.generators;

import org.tigris.mbt.Util;
import org.tigris.mbt.generators.CodeGenerator;
import org.tigris.mbt.generators.CombinedPathGenerator;
import org.tigris.mbt.generators.ListGenerator;
import org.tigris.mbt.graph.Graph;
import org.tigris.mbt.graph.Vertex;
import org.tigris.mbt.machines.FiniteStateMachine;

import junit.framework.TestCase;

/**
 * @author Johan Tejle
 *
 */
public class CombinedPathGeneratorTest extends TestCase {

	Graph graph;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		graph = new Graph();
		
		Vertex v1 = Util.addVertexToGraph(graph, "Start");
		Vertex v2 = Util.addVertexToGraph(graph, "V1");
		Util.addEdgeToGraph(graph, v1, v2, "E1", null, null, null);
	}

	public void testCodeList()
	{
	FiniteStateMachine FSM = new FiniteStateMachine(graph);

	String[] template = {"", "{EDGE_VERTEX}: {LABEL}", ""};
	CodeGenerator generator1 = new CodeGenerator(template);
	ListGenerator generator2 = new ListGenerator();
	
	CombinedPathGenerator pathGenerator = new CombinedPathGenerator(generator1);
	pathGenerator.addPathGenerator(generator2);
	pathGenerator.setMachine(FSM);
	
	String[] stepPair;
	
	stepPair = pathGenerator.getNext();
	assertEquals("Edge: E1",  stepPair[0]);
	stepPair = pathGenerator.getNext();
	assertEquals("Vertex: V1",  stepPair[0]);
	stepPair = pathGenerator.getNext();
	assertEquals("E1",  stepPair[0]);
	assertEquals("Edge",  stepPair[1]);
	stepPair = pathGenerator.getNext();
	assertEquals("V1",  stepPair[0]);
	assertEquals("Vertex",  stepPair[1]);
	assertFalse(pathGenerator.hasNext());

	}
}
