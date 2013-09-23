package org.graphwalker.core.condition;

/**
 * @author Nils Olsson
 */
public final class ReachedVertex extends BaseStopCondition {

    public ReachedVertex(String value) {
        super(value);
    }

    public boolean isFulfilled() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public double getFulfilment() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
