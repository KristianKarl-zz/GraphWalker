package org.graphwalker.core.generators;

import org.graphwalker.api.Model;
import org.graphwalker.api.PathGenerator;
import org.graphwalker.api.model.ModelElement;

/**
 * @author Nils Olsson
 */
public class RandomPath implements PathGenerator {

    private final Model model;

    public RandomPath(Model model) {
        this.model = model;
    }

    public ModelElement getNextStep() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
