package org.tigris.mbt.conditions;

public class Never extends StopCondition {

	public boolean isFulfilled() {
		return false;
	}

	public Never() {
	}

	public double getFulfillment() {
		return 0;
	}
}
