package org.graphwalker.api;

import org.graphwalker.api.model.ModelElement;

/**
 * @author Nils Olsson
 */
public interface Machine {
    boolean hasMoreSteps();
    ModelElement getCurrentStep();
    ModelElement getNextStep();
    Model getModel();
}
