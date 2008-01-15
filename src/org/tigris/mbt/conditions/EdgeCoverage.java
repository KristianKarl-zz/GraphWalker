package org.tigris.mbt.conditions;

import org.tigris.mbt.FiniteStateMachine;

public class EdgeCoverage implements StopCondition {
	
	private FiniteStateMachine machine;
	private double limit;

	public EdgeCoverage(FiniteStateMachine machine, double limit)
	{
		this.machine = machine;
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
