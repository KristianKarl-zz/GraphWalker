package org.tigris.mbt.conditions;

public class StateCoverage extends StopCondition {
	
	private double limit;

	public StateCoverage(double limit)
	{
		this.limit = limit;
	}
	
	public boolean isFulfilled() {
		int stats[] = machine.getStatistics();
		double edges = stats[2];
		double covered = stats[3];
		return (covered/edges) >= limit;
	}

	public double getFulfillment() {
		int stats[] = machine.getStatistics();
		double edges = stats[2];
		double covered = stats[3];
		return (covered/edges) / limit;
	}

}
