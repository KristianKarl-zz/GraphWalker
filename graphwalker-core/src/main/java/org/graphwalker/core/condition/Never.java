package org.graphwalker.core.condition;

import org.graphwalker.core.Machine;

/**
 * @author Nils Olsson
 */
public final class Never extends BaseStopCondition {

    public Never() {
        this("");
    }

    public Never(String value) {
        super(value);
    }

    public boolean isFulfilled(Machine machine) {
        return false;
    }

    public double getFulfilment(Machine machine) {
        return 0;
    }
}
