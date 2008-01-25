package org.tigris.mbt.conditions;

public class AlwaysCondition extends StopCondition 
{
	public AlwaysCondition() {}
	public boolean isFulfilled() { return true; }
	public double getFulfillment() { return 1; }
}
