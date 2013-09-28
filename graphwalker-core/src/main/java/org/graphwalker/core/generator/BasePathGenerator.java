package org.graphwalker.core.generator;

import org.apache.commons.lang3.Validate;
import org.graphwalker.core.PathGenerator;
import org.graphwalker.core.StopCondition;
import org.graphwalker.core.machine.ExecutionContext;

/**
 * @author Nils Olsson
 */
public abstract class BasePathGenerator implements PathGenerator {

    private final StopCondition stopCondition;

    public BasePathGenerator(StopCondition stopCondition) {
        this.stopCondition = Validate.notNull(stopCondition);
    }

    public Boolean hasNextStep(ExecutionContext context) {
        return stopCondition.isFulfilled(context);
    }

    public StopCondition getStopCondition() {
        return stopCondition;
    }
}
