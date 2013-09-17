package org.graphwalker.core.model;

import org.graphwalker.api.machine.State;
import org.graphwalker.api.machine.Transition;

/**
 * @author Nils Olsson
 */
public class Operation extends BaseElement implements Transition<Guard> {

    private final VerificationPoint source;
    private final VerificationPoint target;

    protected Operation(String name, VerificationPoint source, VerificationPoint target) {
        super(name);
        this.source = source;
        this.target = target;
    }

    public State getSource() {
        return source;
    }

    public State getTarget() {
        return target;
    }

    public Double getWeight() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Guard getCondition() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
