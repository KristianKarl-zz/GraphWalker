package org.tigris.mbt.conditions;

public interface StopCondition {
	public abstract boolean isFulfilled();
	public abstract double getFulfillment();
}
