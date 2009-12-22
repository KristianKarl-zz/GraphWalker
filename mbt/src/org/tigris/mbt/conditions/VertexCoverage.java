package org.tigris.mbt.conditions;

import org.tigris.mbt.exceptions.StopConditionException;

public class VertexCoverage extends StopCondition {

	private double limit;

	public VertexCoverage() throws StopConditionException {
		this(1);
	}

	public VertexCoverage(double limit) throws StopConditionException {
		if (limit > 1 || limit < 0)
			throw new StopConditionException("Excpeted a vertex coverage between 0 and 100. Actual: " + limit * 100);
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
