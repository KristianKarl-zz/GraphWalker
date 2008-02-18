package org.tigris.mbt.conditions;

public class StateCoverage extends StopCondition {
	
	private double limit;

	public StateCoverage() {
		this(1);
	}

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

	public double getFulfilment() {
		int stats[] = machine.getStatistics();
		double edges = stats[2];
		double covered = stats[3];
		return (covered/edges) / limit;
	}

	public String toString() {
		return "SC>="+ (int)(100*limit);
	}

}
