package org.tigris.mbt.conditions;

import org.tigris.mbt.exceptions.StopConditionException;

public class EdgeCoverage extends StopCondition {

	private double limit;

	public EdgeCoverage() throws StopConditionException {
		this(1);
	}

	public EdgeCoverage(double limit) throws StopConditionException {
		if (limit > 1 || limit < 0)
			throw new StopConditionException("Excpeted an edge coverage between 0 and 100. Actual: " + limit * 100);
		this.limit = limit;
	}

	public boolean isFulfilled() {
		double edges = machine.getAllEdges().size();
		double covered = machine.getNumOfCoveredEdges();
		return (covered / edges) >= limit;
	}

	public double getFulfilment() {
		double edges = machine.getAllEdges().size();
		double covered = machine.getNumOfCoveredEdges();
		return (covered / edges) / limit;
	}

	public String toString() {
		return "EC>=" + (int) (100 * limit);
	}

}
