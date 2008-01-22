package org.tigris.mbt.conditions;

import org.tigris.mbt.FiniteStateMachine;

public abstract class StopCondition {
	protected FiniteStateMachine machine;
	
	public FiniteStateMachine getMachine() {
		return machine;
	}
	
	public void setMachine(FiniteStateMachine machine) {
		this.machine = machine;
	}
	
	public abstract boolean isFulfilled();
	public abstract double getFulfillment();
}
