package org.graphwalker.core.condition;

/**
 * @author Nils Olsson
 */
public final class TimeDuration extends BaseStopCondition {

    public TimeDuration(String value) {
        super(value);
    }

    public boolean isFulfilled() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public double getFulfilment() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
