package org.graphwalker.core;

import org.graphwalker.core.model.Element;

/**
 * @author Nils Olsson
 */
public interface PathGenerator {
    Element getNextStep();
}
