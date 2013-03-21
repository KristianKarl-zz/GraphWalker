package org.graphwalker.core.generators.support;

import org.graphwalker.core.conditions.StopCondition;
import org.graphwalker.core.generators.AbstractPathGenerator;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.ModelElement;
import org.graphwalker.core.model.support.ModelContext;

import java.util.List;

public final class AStarPath extends AbstractPathGenerator {

    public AStarPath() {
    }

    public AStarPath(StopCondition stopCondition) {
        super(stopCondition);
    }

    public ModelElement getNextStep(ModelContext context, List<ModelElement> elements) {
        return null;  // TODO: Fix me (Auto generated)
    }
}
