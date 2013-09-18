package org.graphwalker.core.model;

import org.graphwalker.api.machine.Transition;

/**
 * @author Nils Olsson
 */
public class Operation extends BaseElement implements Transition<VerificationPoint, Guard> {

    private final VerificationPoint source;
    private final VerificationPoint target;

    public Operation(String name, VerificationPoint source, VerificationPoint target) {
        super(name);
        this.source = source;
        this.target = target;
    }

    public VerificationPoint getSourceVertex() {
        return source;
    }

    public VerificationPoint getTargetVertex() {
        return target;
    }

    public Double getWeight() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Guard getTransitionCondition() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
