package org.graphwalker.core.model;

import java.util.Set;

/**
 * @author Nils Olsson
 */
public final class Vertex extends BaseElement {

    private final Set<Requirement> requirements;
    private final Set<Action> entryActions;
    private final Set<Action> exitActions;

    public Vertex(String name) {
        this(name, null, null, null);
    }

    public Vertex(String name, Set<Requirement> requirements, Set<Action> entryActions, Set<Action> exitActions) {
        super(name);
        this.requirements = unmodifiableSet(requirements);
        this.entryActions = unmodifiableSet(entryActions);
        this.exitActions = unmodifiableSet(exitActions);
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
}
