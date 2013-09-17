package org.graphwalker.core;

import org.graphwalker.api.Model;
import org.graphwalker.api.model.Edge;
import org.graphwalker.api.model.ModelElement;
import org.graphwalker.api.model.Path;
import org.graphwalker.api.model.Vertex;

import java.util.Collection;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public class ModelImpl implements Model {

    public ModelImpl() {

    }

    public ModelImpl(Model... models) {

    }

    public Edge addEdge(Edge edge) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<Edge> getEdges() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Edge getEdge(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Vertex addVertex(Vertex vertex) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<Vertex> getVertices() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Vertex getVertex(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ModelElement getCurrentElement() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<ModelElement> getModelElements() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<ModelElement> getConnectedComponent(ModelElement element) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getShortestDistance(ModelElement source, ModelElement target) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getMaximumDistance(ModelElement target) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Path getShortestPath(ModelElement source, ModelElement target) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
