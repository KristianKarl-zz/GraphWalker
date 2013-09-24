package org.graphwalker.core.condition;

import org.graphwalker.core.Machine;

/**
 * @author Nils Olsson
 */
public final class TimeDuration extends BaseStopCondition {

    public TimeDuration(String value) {
        super(value);
    }

    public boolean isFulfilled(Machine machine) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public double getFulfilment(Machine machine) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
