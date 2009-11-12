package org.tigris.mbt.conditions;

import org.tigris.mbt.Util;

public class EdgeCoverage extends StopCondition {

	private double limit;

	public EdgeCoverage() {
		this(1);
	}

	public EdgeCoverage(double limit) {
		Util.AbortIf((limit > 1 || limit < 0), "Edge coverage must be between 0 and 100");
		this.limit = limit;
	}

	public boolean isFulfilled() {
		int stats[] = machine.getStatistics();
		double edges = stats[0];
		double covered = stats[1];
		return (covered / edges) >= limit;
	}

	public double getFulfilment() {
		int stats[] = machine.getStatistics();
		double edges = stats[0];
		double covered = stats[1];
		return (covered / edges) / limit;
	}

	public String toString() {
		return "EC>=" + (int) (100 * limit);
	}

}
