package org.tigris.mbt.conditions;

public class ReachedState extends StopCondition {

	private String endState;

	public boolean isFulfilled() {
		return endState.equals(machine.getCurrentStateName());
	}

	public ReachedState(String stateName) {
		this.endState = stateName;
	}

	public double getFulfillment() {
		return (isFulfilled()?1:0);
	}
	
	public String toString() {
		return "STATE='"+ endState +"'";
	}

}
