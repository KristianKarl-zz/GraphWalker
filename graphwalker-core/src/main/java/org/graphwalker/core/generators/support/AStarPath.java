package org.graphwalker.core.generators.support;

import org.graphwalker.core.conditions.StopCondition;
import org.graphwalker.core.generators.AbstractPathGenerator;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.model.Element;

public class AStarPath extends AbstractPathGenerator {

    public AStarPath() {
    }

    public AStarPath(StopCondition stopCondition) {
        super(stopCondition);
    }

    public Element getNextStep(Machine machine) {
        return null;  // TODO: Fix me (Auto generated)
    }
}
