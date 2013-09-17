package org.graphwalker.core.model;

import org.graphwalker.api.machine.State;
import org.graphwalker.api.machine.Transition;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Nils Olsson
 */
public class VerificationPoint extends BaseElement implements State<ScriptAction> {

    protected VerificationPoint(String name) {
        super(name);
    }

    public Collection<Requirement> getRequirements() {
        return Arrays.asList();
    }

    public Collection<Transition> getEdges() {
        return Arrays.asList();
    }

    public Collection<ScriptAction> getEntryActions() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<ScriptAction> getExitActions() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
