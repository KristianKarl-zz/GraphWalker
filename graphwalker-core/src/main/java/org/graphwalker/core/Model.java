package org.graphwalker.core;

import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Path;
import org.graphwalker.core.model.Vertex;

import java.util.List;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public interface Model {
    Model addEdge(Edge edge, Vertex source, Vertex target);
    Model addModel(Model model);
    Model addVertex(Vertex vertex);
    List<Edge> getEdges();
    List<Edge> getEdges(String vertex);
    List<Edge> getEdges(Vertex vertex);
    Set<Vertex> getVertices();
    Vertex getVertex(String name);
    Element getConnectedComponent(Element element);
    Path getShortestPath();
    int getShortestDistance();
    int getMaximumDistance();
    Set<Vertex> getStartVertices();
}
