package org.tigris.mbt.conditions;

public class ReachedEdge extends StopCondition {

	private String endEdge;

	public boolean isFulfilled() {
		return machine.getLastEdge() != null && endEdge.equals(machine.getEdgeName(machine.getLastEdge()));
	}

	public ReachedEdge(String edgeName) {
		this.endEdge = edgeName;
	}

	public double getFulfillment() {
		return (isFulfilled()?1:0);
	}
}
