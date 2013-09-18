package org.graphwalker.core;

import org.graphwalker.api.Machine;
import org.graphwalker.api.PathGenerator;
import org.graphwalker.api.StopCondition;
import org.graphwalker.api.event.MachineSink;
import org.graphwalker.api.graph.Element;

/**
 * @author Nils Olsson
 */
public class SimpleMachine implements Machine {

    private final PathGenerator pathGenerator;
    private final StopCondition stopCondition;

    protected SimpleMachine(PathGenerator pathGenerator, StopCondition stopCondition) {
        this.pathGenerator = pathGenerator;
        this.stopCondition = stopCondition;
    }

    public PathGenerator getPathGenerator() {
        return pathGenerator;
    }

    public StopCondition getStopCondition() {
        return stopCondition;
    }

    public Element step() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addSink(MachineSink sink) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeSink(MachineSink sink) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

