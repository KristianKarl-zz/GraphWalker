package org.tigris.mbt.conditions;

import org.tigris.mbt.Util;

public class RequirementCoverage extends StopCondition {

	private double limit;

	public RequirementCoverage() {
		this(1);
	}

	public RequirementCoverage(double limit) {
		Util.AbortIf((limit > 1 || limit < 0), "Requirement coverage must be between 0 and 100");
		this.limit = limit;
	}

	public boolean isFulfilled() {
		int stats[] = machine.getStatistics();
		double requirements = stats[5];
		double covered = stats[6];
		return (covered / requirements) >= limit;
	}

	public double getFulfilment() {
		int stats[] = machine.getStatistics();
		double requirements = stats[5];
		double covered = stats[6];
		return (covered / requirements) / limit;
	}

	public String toString() {
		return "RC>=" + (int) (100 * limit);
	}

}
