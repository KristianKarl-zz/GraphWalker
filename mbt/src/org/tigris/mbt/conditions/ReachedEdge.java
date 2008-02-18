package org.tigris.mbt.conditions;

public class ReachedEdge extends StopCondition {

	private String endEdge;

	public boolean isFulfilled() {
		return machine.getLastEdge() != null && endEdge.equals(machine.getEdgeName(machine.getLastEdge()));
	}

	public ReachedEdge(String edgeName) {
		this.endEdge = edgeName;
	}

	public double getFulfilment() {
		//FIXME change to reflect fulfilment in %  /Tejle
		return (isFulfilled()?1:0);
	}
	
	public String toString() {
		return "EDGE='"+ endEdge +"'";
	}

}
