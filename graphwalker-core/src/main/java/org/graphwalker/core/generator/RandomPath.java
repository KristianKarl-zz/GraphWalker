package org.graphwalker.core.generator;

import org.graphwalker.api.graph.Element;
import org.graphwalker.api.machine.FiniteStateMachine;

/**
 * @author Nils Olsson
 */
public class RandomPath extends BasePathGenerator {

    public RandomPath(FiniteStateMachine model) {
        super(model);
    }

    public Element getNextStep() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
