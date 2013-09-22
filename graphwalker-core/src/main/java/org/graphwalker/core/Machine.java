package org.graphwalker.core;

import org.graphwalker.core.model.Element;
import org.graphwalker.core.script.Context;

/**
 * @author Nils Olsson
 */
public interface Machine {
    Element getNextStep();
    Element getCurrentStep();
    Boolean hasNextStep();
    Context getContext();

}
