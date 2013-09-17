package org.graphwalker.core;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphwalker.api.Machine;
import org.graphwalker.api.Model;
import org.graphwalker.api.PathGenerator;
import org.graphwalker.api.StopCondition;
import org.graphwalker.api.event.EventSink;
import org.graphwalker.api.model.Edge;
import org.graphwalker.api.model.ModelElement;
import org.graphwalker.api.model.Requirement;
import org.graphwalker.api.model.Vertex;
import org.graphwalker.core.conditions.VertexCoverage;
import org.graphwalker.core.generators.RandomPath;
import org.graphwalker.core.model.EdgeImpl;
import org.graphwalker.core.model.VertexImpl;
import org.junit.Test;

/**
 * @author Nils Olsson
 */
public class TestClass implements EventSink {

    private Graph g = new SingleGraph();

    @Test
    public void doTest() {

        Model model = createModel();

        for (Vertex vertex: model.getVertices()) {
            g.addNode(vertex.getName());
        }

        for (Edge edge: model.getEdges()) {
            g.addEdge(edge.getName(), edge.getSourceVertex().getName(), edge.getTargetVertex().getName());
        }

        g.display();

        PathGenerator pathGenerator = new RandomPath(model);
        StopCondition stopCondition = new VertexCoverage(model);
        Machine machine = new MachineImpl(pathGenerator, stopCondition);
        machine.addEventSink(this);
        machine.run();
    }

    public void walking(ModelElement modelElement) {
        System.out.println("WLK: ");//+modelElement.getName());
    }

    public void stepFailed(ModelElement element, Throwable cause) {
        System.out.println("ERR: "+element.getName());
    }

    public void statusChanged(Requirement requirement) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void statusChanged(ModelElement element) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void edgeAdded(Edge edge) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void edgeRemoved(Edge edge) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void vertexAdded(Vertex vertex) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void vertexRemoved(Vertex vertex) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void requirementStatusChanged(Requirement requirement) {
        System.out.println("REQ: "+requirement.getId());
    }

    private Model createModel() {
        Model model1 = createModel1();
        Model model2 = createModel2();
        return new ModelImpl(model1, model2);
    }

    private Model createModel1() {
        Model model = new ModelImpl();
        Vertex v1 = new VertexImpl("v1");
        Vertex v2 = new VertexImpl("v2");
        model.addVertex(v1);
        model.addVertex(v2);
        Edge e1 = new EdgeImpl("e1");
        model.addEdge(e1);
        return model;
    }
    private Model createModel2() {
        Model model = new ModelImpl();
        Vertex v1 = new VertexImpl("v1");
        Vertex v2 = new VertexImpl("v2");
        Vertex v3 = new VertexImpl("v3");
        model.addVertex(v1);
        model.addVertex(v2);
        model.addVertex(v3);
        Edge e1 = new EdgeImpl("e1");
        model.addEdge(e1);
        Edge e2 = new EdgeImpl("e2");
        model.addEdge(e2);
        return model;
    }
}
