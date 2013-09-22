package org.graphwalker.core;

import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Path;
import org.graphwalker.core.model.Vertex;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public interface Model {
    Model addEdge(Edge edge);
    Model addModel(Model model);
    Model addVertex(Vertex vertex);
    List<Edge> getEdges();
    Collection<Vertex> getVertices();
    Vertex getVertex(String name);
    Set<Element> getConnectedComponent(Element element);
    Path getShortestPath();
    int getShortestDistance();
    int getMaximumDistance();
    Set<Vertex> getStartVertices();
}
