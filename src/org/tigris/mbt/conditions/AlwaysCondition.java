package org.tigris.mbt.conditions;

public class AlwaysCondition extends StopCondition 
{
	public AlwaysCondition() {}
	public boolean isFulfilled() { return true; }
	public double getFulfilment() { return 1; }
	
	public String toString() {
		return "TRUE";
	}
}
