/**
 * 
 */
package test.org.graphwalker.generators;

import org.graphwalker.Util;
import org.graphwalker.generators.CodeGenerator;
import org.graphwalker.generators.CombinedPathGenerator;
import org.graphwalker.generators.ListGenerator;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;
import org.graphwalker.machines.FiniteStateMachine;

import junit.framework.TestCase;

/**
 * @author Johan Tejle
 * 
 */
public class CombinedPathGeneratorTest extends TestCase {

	Graph graph;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		graph = new Graph();

		Vertex v1 = Util.addVertexToGraph(graph, "Start");
		Vertex v2 = Util.addVertexToGraph(graph, "V1");
		Util.addEdgeToGraph(graph, v1, v2, "E1", null, null, null);
	}

	public void testCodeList() throws InterruptedException {
		FiniteStateMachine FSM = new FiniteStateMachine();
		FSM.setModel(graph);

		String[] template = { "", "{EDGE_VERTEX}: {LABEL}", "" };
		CodeGenerator generator1 = new CodeGenerator();
		generator1.setTemplate(template);
		ListGenerator generator2 = new ListGenerator();

		CombinedPathGenerator pathGenerator = new CombinedPathGenerator();
		pathGenerator.addPathGenerator(generator1);
		pathGenerator.addPathGenerator(generator2);
		pathGenerator.setMachine(FSM);

		String[] stepPair;

		stepPair = pathGenerator.getNext();
		assertEquals("Edge: E1", stepPair[0]);
		stepPair = pathGenerator.getNext();
		assertEquals("Vertex: V1", stepPair[0]);
		stepPair = pathGenerator.getNext();
		assertEquals("E1", stepPair[0]);
		assertEquals("Edge", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("V1", stepPair[0]);
		assertEquals("Vertex", stepPair[1]);
		assertFalse(pathGenerator.hasNext());

	}
}
