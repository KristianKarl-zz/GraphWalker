package org.tigris.mbt.conditions;

import org.tigris.mbt.FiniteStateMachine;

public class Never implements StopCondition {

	public boolean isFulfilled() {
		return false;
	}

	public Never(FiniteStateMachine machine) {
	}

	public double getFulfillment() {
		return (isFulfilled()?1:0);
	}
}
