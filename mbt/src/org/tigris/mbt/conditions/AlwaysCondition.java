package org.tigris.mbt.conditions;

public class AlwaysCondition extends StopCondition 
{
	public AlwaysCondition() {}
	public double getFulfillment() { return 1; }
	public boolean isFulfilled() { return true; }
}
