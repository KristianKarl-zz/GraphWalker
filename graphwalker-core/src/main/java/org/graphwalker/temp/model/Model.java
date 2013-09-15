package org.graphwalker.temp.model;

import org.graphwalker.api.model.Edge;
import org.graphwalker.api.model.ModelElement;
import org.graphwalker.api.model.Path;
import org.graphwalker.api.model.Vertex;

import java.util.Collection;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public class Model implements org.graphwalker.api.Model {

    public <T extends Edge> T addEdge() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends Edge> Set<T> getEdges() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends Vertex> T addVertex() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends Vertex> Set<T> getVertices() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends ModelElement> Set<T> getModelElements() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends ModelElement> Collection<T> getConnectedComponent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends ModelElement> int getShortestDistance(T source, T target) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends ModelElement> int getMaximumDistance(T target) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T extends ModelElement> Path<T> getShortestPath(T source, T target) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
