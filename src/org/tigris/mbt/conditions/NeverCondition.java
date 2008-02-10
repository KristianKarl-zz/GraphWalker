package org.tigris.mbt.conditions;

public class NeverCondition extends StopCondition 
{
	public NeverCondition() { }
	public boolean isFulfilled() { return false; }
	public double getFulfillment() { return 0; }
	
	public String toString() {
		return "FALSE";
	}

}
