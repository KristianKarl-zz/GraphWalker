package org.graphwalker.api;

/**
 * @author Nils Olsson
 */
public interface StopCondition {
    Boolean isFulfilled(Machine machine);
    Double getFulfilment(Machine machine);
}
