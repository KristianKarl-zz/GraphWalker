package org.tigris.mbt.conditions;

import org.tigris.mbt.FiniteStateMachine;

public class StateCoverage implements StopCondition {
	
	private FiniteStateMachine machine;
	private double limit;

	public StateCoverage(FiniteStateMachine machine, double limit)
	{
		this.machine = machine;
		this.limit = limit;
	}
	
	public boolean isFulfilled() {
		int stats[] = machine.getStatistics();
		double edges = stats[2];
		double covered = stats[3];
		return (covered/edges) >= limit;
	}

	public double getFulfillment() {
		int stats[] = machine.getStatistics();
		double edges = stats[2];
		double covered = stats[3];
		return (covered/edges) / limit;
	}

}
