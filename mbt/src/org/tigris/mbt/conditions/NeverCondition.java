package org.tigris.mbt.conditions;

public class NeverCondition extends StopCondition {

	public boolean isFulfilled() {
		return false;
	}

	public NeverCondition() {
	}

	public double getFulfillment() {
		return 0;
	}
}
