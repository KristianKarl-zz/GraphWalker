package org.graphwalker.temp;

import org.graphwalker.api.Model;
import org.graphwalker.api.PathGenerator;
import org.graphwalker.api.StopCondition;
import org.graphwalker.api.model.ModelElement;

/**
 * @author Nils Olsson
 */
public class Machine implements org.graphwalker.api.Machine, Runnable {

    private final Model model;
    private final PathGenerator pathGenerator;
    private final StopCondition stopCondition;

    public Machine(Model model, PathGenerator pathGenerator, StopCondition stopCondition) {
        this.model = model;
        this.pathGenerator = pathGenerator;
        this.stopCondition = stopCondition;
    }

    public boolean hasMoreSteps() {
        return stopCondition.isFulfilled(this);
    }

    public ModelElement getCurrentStep() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ModelElement getNextStep() {
        return pathGenerator.getNextStep(this);
    }

    public Model getModel() {
        return model;
    }

    public void run() {

    }
}
