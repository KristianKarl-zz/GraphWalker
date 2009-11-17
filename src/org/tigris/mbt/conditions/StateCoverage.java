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
		double vertices = machine.getAllStates().size();
		double covered = machine.getNumOfCoveredVertices();
		return (covered / vertices) >= limit;
	}

	public double getFulfilment() {
		double vertices = machine.getAllStates().size();
		double covered = machine.getNumOfCoveredVertices();
		return (covered / vertices) / limit;
	}

	public String toString() {
		return "SC>=" + (int) (100 * limit);
	}

}
