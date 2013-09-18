package org.graphwalker.core.model;

import org.graphwalker.api.machine.State;

import java.util.*;

/**
 * @author Nils Olsson
 */
public class VerificationPoint extends BaseElement implements State<Operation, ScriptAction> {

    private final List<Requirement> requirements = new ArrayList<Requirement>();
    private final Set<Operation> edges = new HashSet<Operation>();
    private final List<ScriptAction> entryActions = new ArrayList<ScriptAction>();
    private final List<ScriptAction> exitActions = new ArrayList<ScriptAction>();

    public VerificationPoint(String name) {
        super(name);
    }

    public Collection<Requirement> getRequirements() {
        return requirements;
    }

    public Collection<Operation> getEdges() {
        return Arrays.asList();
    }

    public Collection<ScriptAction> getEntryActions() {
        return entryActions;
    }

    public Collection<ScriptAction> getExitActions() {
        return exitActions;
    }
}
