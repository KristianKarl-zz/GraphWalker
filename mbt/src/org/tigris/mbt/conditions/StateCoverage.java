package org.tigris.mbt.conditions;

import java.util.Hashtable;

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
		Hashtable stats = machine.getStatistics();
		double edges = ((Integer)stats.get("Vertices")).doubleValue();
		double covered = ((Integer)stats.get("Vertices covered")).doubleValue();
		return (covered/edges) >= limit;
	}

}
