package org.graphwalker.core.condition;

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

    public boolean isFulfilled() {
        return false;
    }

    public double getFulfilment() {
        return 0;
    }
}
