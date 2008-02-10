package org.tigris.mbt.conditions;

public class RequirementCoverage extends StopCondition {
	
	private double limit;

	public RequirementCoverage() {
		this(1);
	}

	public RequirementCoverage(double limit)
	{
		this.limit = limit;
	}
	
	public boolean isFulfilled() {
		int stats[] = machine.getStatistics();
		double requirements = stats[5];
		double covered = stats[6];
		return (covered/requirements) >= limit;
	}

	public double getFulfillment() {
		int stats[] = machine.getStatistics();
		double requirements = stats[5];
		double covered = stats[6];
		return (covered/requirements) / limit;
	}
	
	public String toString() {
		return "RC>="+ (int)(100*limit);
	}

}
