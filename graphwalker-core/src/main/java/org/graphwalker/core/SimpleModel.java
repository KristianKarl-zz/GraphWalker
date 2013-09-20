package org.graphwalker.core;

import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Path;
import org.graphwalker.core.model.Vertex;

import java.util.*;

/**
 * @author Nils Olsson
 */
public final class SimpleModel implements Model {

    private final Set<Vertex> vertices = new HashSet<Vertex>();
    private final Map<String, Vertex> vertexNameMap = new HashMap<String, Vertex>();
    private final List<Edge> edges = new ArrayList<Edge>();
    private final Map<String, List<Edge>> vertexEdgeMap = new HashMap<String, List<Edge>>();

    public SimpleModel() {
    }

    public Model addEdge(Edge edge, Vertex source, Vertex target) {
        if (!edges.contains(edge)) {

        } else {

        }
        return this;
    }

    public Model addModel(Model model) {
        // merge
        return this;
    }

    public Model addVertex(Vertex vertex) {
        if (!vertexNameMap.containsKey(vertex.getName())) {
            vertices.add(vertex);
            vertexNameMap.put(vertex.getName(), vertex);
        } else {
            //merge
        }
        return this;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Edge> getEdges(String vertex) {
        return vertexEdgeMap.get(vertex);
    }

    public List<Edge> getEdges(Vertex vertex) {
        return getEdges(vertex.getName());
    }

    public Set<Vertex> getVertices() {
        return vertices;
    }

    public Vertex getVertex(String name) {
        return vertexNameMap.get(name);
    }

    public Element getConnectedComponent(Element element) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Path getShortestPath() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getShortestDistance() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getMaximumDistance() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<Vertex> getStartVertices() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
