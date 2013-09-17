package org.graphwalker.core.model;

import org.graphwalker.api.model.Edge;
import org.graphwalker.api.model.Requirement;
import org.graphwalker.api.model.Vertex;

import java.util.Set;

/**
 * @author Nils Olsson
 */
public class VertexImpl implements Vertex {

    private String name;

    public VertexImpl(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<Edge> getEdges() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<Requirement> getRequirements() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
