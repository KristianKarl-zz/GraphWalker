package org.graphwalker.core.condition;

import org.graphwalker.core.Machine;

/**
 * @author Nils Olsson
 */
public final class VertexCoverage extends BaseStopCondition {

    public VertexCoverage() {
        this("100");
    }

    public VertexCoverage(String value) {
        super(value);
    }

    public boolean isFulfilled(Machine machine) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public double getFulfilment(Machine machine) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
