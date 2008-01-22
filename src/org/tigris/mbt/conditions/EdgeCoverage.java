package org.tigris.mbt.conditions;

public class EdgeCoverage extends StopCondition {
	
	private double limit;

	public EdgeCoverage(double limit)
	{
		this.limit = limit;
	}
	
	public boolean isFulfilled() {
		int stats[] = machine.getStatistics();
		double edges = stats[0];
		double covered = stats[1];
		return (covered/edges) >= limit;
	}

	public double getFulfillment() {
		int stats[] = machine.getStatistics();
		double edges = stats[0];
		double covered = stats[1];
		return (covered/edges) / limit;
	}

}
