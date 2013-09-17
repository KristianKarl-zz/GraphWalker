package org.graphwalker.core;

import org.graphwalker.api.Machine;
import org.graphwalker.api.PathGenerator;
import org.graphwalker.api.StopCondition;
import org.graphwalker.api.event.EventSink;
import org.graphwalker.api.model.ModelElement;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public class MachineImpl implements Machine {

    private final PathGenerator pathGenerator;
    private final StopCondition stopCondition;
    private final Set<EventSink> eventSinks = new HashSet<EventSink>();

    public MachineImpl(PathGenerator pathGenerator, StopCondition stopCondition) {
        this.pathGenerator = pathGenerator;
        this.stopCondition = stopCondition;
    }

    public ModelElement step() {
        ModelElement nextElement = getPathGenerator().getNextStep();
        for (EventSink eventSink: eventSinks) {
            eventSink.walking(nextElement);
        }
        // execute element
        return nextElement;
    }

    public void run() {
        while (!getStopCondition().isFulfilled()) {
            step();
        }
    }

    public PathGenerator getPathGenerator() {
        return pathGenerator;
    }

    public StopCondition getStopCondition() {
        return stopCondition;
    }

    public void addEventSink(EventSink eventSink) {
        eventSinks.add(eventSink);
    }

    public void removeEventSink(EventSink eventSink) {
        eventSinks.remove(eventSink);
    }
}
