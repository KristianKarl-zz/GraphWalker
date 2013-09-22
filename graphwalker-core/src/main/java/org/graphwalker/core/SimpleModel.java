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

    //private final Map<String, Vertex> vertices = new HashMap<String, Vertex>();
    //private final List<Edge> edges = new ArrayList<Edge>();

    private final Set<Vertex> vertices;
    private final Set<Edge> edges;

    public SimpleModel() {
        this(new HashSet<Vertex>(), new HashSet<Edge>());
    }

    public SimpleModel(Set<Vertex> vertices, Set<Edge> edges) {
        this.vertices = Collections.unmodifiableSet(vertices);
        this.edges = Collections.unmodifiableSet(edges);
    }

    public Model addEdge(Edge edge) {
        throw new RuntimeException("not implemented");
    }

    public Model addModel(Model model) {
        throw new RuntimeException("not implemented");
    }

    public Model addVertex(Vertex vertex) {
        throw new RuntimeException("not implemented");
    }

    public List<Edge> getEdges() {
        return null; //edges;
    }

    public Collection<Vertex> getVertices() {
        return null;//vertices.values();
    }

    public Vertex getVertex(String name) {
        return null;//vertices.get(name);
    }

    public Set<Element> getConnectedComponent(Element element) {
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
