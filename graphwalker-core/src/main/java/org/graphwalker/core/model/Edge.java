package org.graphwalker.core.model;

import java.util.Set;

/**
 * @author Nils Olsson
 */
public final class Edge extends BaseElement {

    private final Double weight;
    private final Guard guard;
    private final Set<Action> actions;

    public Edge(String name) {
        this(name, 1.0, null, null);
    }

    public Edge(String name, Guard guard, Set<Action> actions) {
        this(name, 1.0, guard, actions);
    }

    public Edge(String name, Double weight, Guard guard, Set<Action> actions) {
        super(name);
        this.weight = weight;
        this.guard = guard;
        this.actions = unmodifiableSet(actions);
    }

    public Guard getGuard() {
        return guard;
    }

    public Set<Action> getActions() {
        return actions;
    }

    public Double getWeight() {
        return weight;
    }
}
