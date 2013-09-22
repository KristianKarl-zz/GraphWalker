package org.graphwalker.core;

/**
 * @author Nils Olsson
 */
public interface StopCondition {
    boolean isFulfilled();
    double getFulfilment();
}
