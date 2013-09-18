package org.graphwalker.api;

import org.graphwalker.api.event.ModelSink;
import org.graphwalker.api.event.Source;
import org.graphwalker.api.machine.FiniteStateMachine;
import org.graphwalker.api.machine.State;
import org.graphwalker.api.machine.Transition;

/**
 * @author Nils Olsson
 */
public interface Model<S extends State, T extends Transition> extends FiniteStateMachine<S, T>, Source<ModelSink> {
}
