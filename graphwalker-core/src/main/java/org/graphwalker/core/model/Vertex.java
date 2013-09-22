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
public final class Vertex implements Element {

    private final String name;
    private final Set<Edge> edges;
    private final Set<Requirement> requirements;
    private final Set<Action> entryActions;
    private final Set<Action> exitActions;

    public Vertex(String name) {
        this(name, new HashSet<Requirement>());
    }

    public Vertex(String name, Set<Requirement> requirements) {
        this(name, requirements, new HashSet<Action>(), new HashSet<Action>());
    }

    public Vertex(String name, Set<Requirement> requirements, Set<Action> entryActions, Set<Action> exitActions) {
        this(name, new HashSet<Edge>(), requirements, entryActions, exitActions);
    }

    public Vertex(String name, Set<Edge> edges, Set<Requirement> requirements, Set<Action> entryActions, Set<Action> exitActions) {
        this.name = Validate.notEmpty(Validate.notNull(name));
        this.edges = Collections.unmodifiableSet(Validate.notNull(edges));
        this.requirements = Collections.unmodifiableSet(Validate.notNull(requirements));
        this.entryActions = Collections.unmodifiableSet(Validate.notNull(entryActions));
        this.exitActions = Collections.unmodifiableSet(Validate.notNull(exitActions));
    }

    public String getName() {
        return name;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public Set<Requirement> getRequirements() {
        return requirements;
    }

    public Set<Action> getEntryActions() {
        return entryActions;
    }

    public Set<Action> getExitActions() {
        return exitActions;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 43)
                .append(name)
                .hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (null == object) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object instanceof Vertex) {
            Vertex element = (Vertex)object;
            return new EqualsBuilder()
                    .append(name, element.getName())
                    .isEquals();
        }
        return false;
    }
}
