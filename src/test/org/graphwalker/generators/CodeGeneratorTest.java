package test.org.graphwalker.generators;

import org.apache.log4j.Logger;
import org.graphwalker.Util;
import org.graphwalker.generators.CodeGenerator;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;
import org.graphwalker.machines.FiniteStateMachine;

import junit.framework.TestCase;

public class CodeGeneratorTest extends TestCase {
	Logger logger = Util.setupLogger(CodeGeneratorTest.class);

	public void testGetNext() {
		Graph graph = new Graph();

		Vertex v1 = new Vertex();
		v1.setIndexKey(new Integer(1));
		v1.setLabelKey("Start");
		graph.addVertex(v1);

		Vertex v2 = new Vertex();
		v2.setIndexKey(new Integer(2));
		v2.setLabelKey("V2");
		graph.addVertex(v2);

		Edge edge = new Edge();
		graph.addEdge(edge, v1, v2);
		edge.setIndexKey(new Integer(3));
		edge.setLabelKey("E1");

		FiniteStateMachine FSM = new FiniteStateMachine();
		FSM.setModel(graph);

		String[] template = { "", "{EDGE_VERTEX}: {LABEL}", "" };
		CodeGenerator generator = new CodeGenerator();
		generator.setTemplate(template);
		generator.setMachine(FSM);

		assertEquals("Edge: E1", generator.getNext()[0]);
		assertEquals("Vertex: V2", generator.getNext()[0]);
		assertFalse(generator.hasNext());
	}

	public void testHeaderFooter() {
		Graph graph = new Graph();

		Vertex v1 = new Vertex();
		v1.setIndexKey(new Integer(1));
		v1.setLabelKey("Start");
		graph.addVertex(v1);

		Vertex v2 = new Vertex();
		v2.setIndexKey(new Integer(2));
		v2.setLabelKey("V2");
		graph.addVertex(v2);

		Edge edge = new Edge();
		graph.addEdge(edge, v1, v2);
		edge.setIndexKey(new Integer(3));
		edge.setLabelKey("E1");

		FiniteStateMachine FSM = new FiniteStateMachine();
		FSM.setModel(graph);

		String[] template = { "This is the HEADER", "{EDGE_VERTEX}: {LABEL}", "This is the FOOTER" };
		CodeGenerator generator = new CodeGenerator();
		generator.setTemplate(template);
		generator.setMachine(FSM);

		StringBuffer str = new StringBuffer();
		while (generator.hasNext()) {
			str.append(generator.getNext()[0]);
		}
		System.out.println(str.toString());
	}

}
