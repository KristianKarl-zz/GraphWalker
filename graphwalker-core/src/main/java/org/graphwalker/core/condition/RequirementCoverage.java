package org.graphwalker.core.condition;

import org.graphwalker.core.Machine;

/**
 * @author Nils Olsson
 */
public final class RequirementCoverage extends BaseStopCondition {

    public RequirementCoverage() {
        this("100");
    }

    public RequirementCoverage(String value) {
        super(value);
    }

    public boolean isFulfilled(Machine machine) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public double getFulfilment(Machine machine) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
