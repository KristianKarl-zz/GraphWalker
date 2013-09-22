package org.graphwalker.core.model;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public final class Edge implements Element {

    private final String name;
    private final Vertex source;
    private final Vertex target;
    private final Boolean blocked;
    private final Double weight;
    private final Guard guard;
    private final Set<Action> actions;

    public Edge(String name, Vertex source, Vertex target) {
        this(name, source, target, new Guard("true"), new HashSet<Action>());
    }

    public Edge(String name, Vertex source, Vertex target, Set<Action> actions) {
        this(name, source, target, new Guard("true"), actions, false);
    }

    public Edge(String name, Vertex source, Vertex target, Guard guard, Set<Action> actions) {
        this(name, source, target, guard, actions, false);
    }

    public Edge(String name, Vertex source, Vertex target, Guard guard, Set<Action> actions, Boolean blocked) {
        this(name, source, target, guard, actions, blocked, 1.0);
    }

    public Edge(String name, Vertex source, Vertex target, Guard guard, Set<Action> actions, Boolean blocked, Double weight) {
        this.name = name;
        this.source = Validate.notNull(source);
        this.target = Validate.notNull(target);
        this.guard = Validate.notNull(guard);
        this.actions = Collections.unmodifiableSet(Validate.notNull(actions));
        this.blocked = Validate.notNull(blocked);
        this.weight = Validate.notNull(weight);
    }

    public String getName() {
        return name;
    }

    public Vertex getSourceVertex() {
        return source;
    }

    public Vertex getTargetVertex() {
        return target;
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

    public Boolean isBlocked() {
        return blocked;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(23, 47)
                .appendSuper(super.hashCode())
                .append(source.getName())
                .append(target.getName())
                .append(blocked)
                .append(weight)
                .toHashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (null == object) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object instanceof Edge) {
            Edge edge = (Edge)object;
            return new EqualsBuilder()
                    .appendSuper(super.equals(object))
                    .append(source, edge.getSourceVertex())
                    .append(target, edge.getTargetVertex())
                    .isEquals();
        }
        return false;
    }
}
