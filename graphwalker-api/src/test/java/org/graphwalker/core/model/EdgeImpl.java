package org.graphwalker.core.model;

import org.graphwalker.api.model.Action;
import org.graphwalker.api.model.Edge;
import org.graphwalker.api.model.Guard;
import org.graphwalker.api.model.Vertex;

import java.util.Set;

/**
 * @author Nils Olsson
 */
public class EdgeImpl implements Edge {

    private String name;

    public EdgeImpl(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Vertex getSourceVertex() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Vertex getTargetVertex() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<Action> getActions() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<Guard> getGuards() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Double getWeight() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
