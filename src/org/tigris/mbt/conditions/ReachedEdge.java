package org.tigris.mbt.conditions;

import org.tigris.mbt.FiniteStateMachine;

public class ReachedEdge implements StopCondition {

	private FiniteStateMachine machine;
	private String endEdge;

	public boolean isFulfilled() {
		return machine.getLastEdge() != null && endEdge.equals(machine.getEdgeName(machine.getLastEdge()));
	}

	public ReachedEdge(FiniteStateMachine machine, String edgeName) {
		this.machine = machine;
		this.endEdge = edgeName;
	}
}
