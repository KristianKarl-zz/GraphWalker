package org.graphwalker.api;

import org.graphwalker.api.model.Edge;
import org.graphwalker.api.model.ModelElement;
import org.graphwalker.api.model.Path;
import org.graphwalker.api.model.Vertex;

import java.util.Collection;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public interface Model {
    <T extends Edge> T addEdge();
    <T extends Edge> Set<T> getEdges();
    <T extends Vertex> T addVertex();
    <T extends Vertex> Set<T> getVertices();
    <T extends ModelElement> Set<T> getModelElements();
    <T extends ModelElement> Collection<T> getConnectedComponent();
    <T extends ModelElement> int getShortestDistance(T source, T target);
    <T extends ModelElement> int getMaximumDistance(T target);
    <T extends ModelElement> Path<T> getShortestPath(T source, T target);
}
