package org.graphwalker.core.conditions;

import org.graphwalker.api.Model;
import org.graphwalker.api.StopCondition;

/**
 * @author Nils Olsson
 */
public class VertexCoverage implements StopCondition {

    private final Model model;

    public VertexCoverage(Model model) {
        this.model = model;
    }

    int c = 3;

    public Boolean isFulfilled() {
        return c--<=0;
    }

    public Double getFulfilment() {
        return 0d;
    }
}
