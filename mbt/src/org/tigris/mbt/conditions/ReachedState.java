package org.tigris.mbt.conditions;

import org.tigris.mbt.FiniteStateMachine;

public class ReachedState implements StopCondition {

	private FiniteStateMachine machine;
	private String endState;

	public boolean isFulfilled() {
		return endState.equals(machine.getCurrentStateName());
	}

	public ReachedState(FiniteStateMachine machine, String stateName) {
		this.machine = machine;
		this.endState = stateName;
	}

	public double getFulfillment() {
		return (isFulfilled()?1:0);
	}
}
