package org.tigris.mbt.conditions;

import org.tigris.mbt.FiniteStateMachine;

public class RequirementCoverage implements StopCondition {
	
	private FiniteStateMachine machine;
	private double limit;

	public RequirementCoverage(FiniteStateMachine machine, double limit)
	{
		this.machine = machine;
		this.limit = limit;
	}
	
	public boolean isFulfilled() {
		int stats[] = machine.getStatistics();
		double requirements = stats[5];
		double covered = stats[6];
		return (covered/requirements) >= limit;
	}

}
