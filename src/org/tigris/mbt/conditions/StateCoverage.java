package org.tigris.mbt.conditions;

import org.tigris.mbt.Util;

public class StateCoverage extends StopCondition {

	private double limit;

	public StateCoverage() {
		this(1);
	}

	public StateCoverage(double limit) {
		Util.AbortIf((limit > 1 || limit < 0), "State coverage must be between 0 and 100");
		this.limit = limit;
	}

	public boolean isFulfilled() {
		int stats[] = machine.getStatistics();
		double edges = stats[2];
		double covered = stats[3];
		return (covered / edges) >= limit;
	}

	public double getFulfilment() {
		int stats[] = machine.getStatistics();
		double edges = stats[2];
		double covered = stats[3];
		return (covered / edges) / limit;
	}

	public String toString() {
		return "SC>=" + (int) (100 * limit);
	}

}
