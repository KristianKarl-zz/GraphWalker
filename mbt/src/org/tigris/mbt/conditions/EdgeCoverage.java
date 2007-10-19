package org.tigris.mbt.conditions;

import java.util.Hashtable;

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
		Hashtable stats = machine.getStatistics();
		double edges = ((Integer)stats.get("Edges")).doubleValue();
		double covered = ((Integer)stats.get("Edges covered")).doubleValue();
		return (covered/edges) >= limit;
	}

}
