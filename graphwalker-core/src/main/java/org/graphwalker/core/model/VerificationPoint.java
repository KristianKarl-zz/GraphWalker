package org.graphwalker.core.model;

import org.graphwalker.api.machine.State;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public class VerificationPoint extends BaseElement implements State<Operation, ScriptAction> {

    private final Set<Requirement> requirements = new HashSet<Requirement>();
    private final Set<Operation> edges = new HashSet<Operation>();
    private final Set<ScriptAction> entryActions = new HashSet<ScriptAction>();
    private final Set<ScriptAction> exitActions = new HashSet<ScriptAction>();

    public VerificationPoint(String name) {
        super(name);
    }

    public Collection<Requirement> getRequirements() {
        return requirements;
    }

    public Collection<Operation> getEdges() {
        return edges;
    }

    public Collection<ScriptAction> getEntryActions() {
        return entryActions;
    }

    public Collection<ScriptAction> getExitActions() {
        return exitActions;
    }
}
