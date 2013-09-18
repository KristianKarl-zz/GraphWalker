package org.graphwalker.core;

import org.graphwalker.api.Model;
import org.graphwalker.api.event.ModelSink;
import org.graphwalker.api.graph.Element;
import org.graphwalker.api.graph.Path;
import org.graphwalker.core.model.Operation;
import org.graphwalker.core.model.VerificationPoint;

import java.util.*;

/**
 * @author Nils Olsson
 */
public class SimpleModel implements Model<VerificationPoint, Operation> {

    private final String name;
    private final Map<String, VerificationPoint> vertices = new HashMap<String, VerificationPoint>();
    private final Map<String, Operation> edges = new HashMap<String, Operation>();
    private final Set<ModelSink> sinks = new HashSet<ModelSink>();

    public SimpleModel(String name) {
        this.name = name;
    }

    public void addEdge(Operation edge) {
        edges.put(edge.getName(), edge);
        for (ModelSink sink: sinks) {
            sink.edgeAdded(edge);
        }
    }

    public Operation getEdge(String name) {
        return edges.get(name);
    }

    public Collection<Operation> getEdges() {
        return edges.values();
    }

    public void addVertex(VerificationPoint vertex) {
        vertices.put(vertex.getName(), vertex);
        for (ModelSink sink: sinks) {
            sink.vertexAdded(vertex);
        }
    }

    public VerificationPoint getVertex(String name) {
        return vertices.get(name);
    }

    public Collection<VerificationPoint> getVertices() {
        return vertices.values();
    }

    public Set<Element> getConnectedComponent(Element element) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getShortestDistance(Element source, Element target) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getMaximumDistance(Element target) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Path getShortestPath(Element source, Element target) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getName() {
        return name;
    }

    public void addSink(ModelSink sink) {
        sinks.add(sink);
    }

    public void removeSink(ModelSink sink) {
        sinks.remove(sink);
    }
}
