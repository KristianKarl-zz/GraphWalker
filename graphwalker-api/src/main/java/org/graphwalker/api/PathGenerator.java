package org.graphwalker.api;

import org.graphwalker.api.model.ModelElement;

/**
 * @author Nils Olsson
 */
public interface PathGenerator {
    ModelElement getNextStep(Machine machine);
}
