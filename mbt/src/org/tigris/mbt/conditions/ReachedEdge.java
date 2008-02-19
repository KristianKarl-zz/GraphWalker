package org.tigris.mbt.conditions;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

public class ReachedEdge extends StopCondition {

	private String edgeName;
	private DirectedSparseEdge endEdge;

	public boolean isFulfilled() {
		if(endEdge != null)
			return machine.getLastEdge().equals(endEdge);
		return machine.getLastEdge() != null && edgeName.equals(machine.getEdgeName(machine.getLastEdge()));
	}

	public ReachedEdge(String edgeName) {
		this.edgeName = edgeName;
	}

	public ReachedEdge(DirectedSparseEdge endEdge) {
		this.endEdge = endEdge;
	}

	public double getFulfilment() {
		//FIXME change to reflect fulfilment in %  /Tejle
		return (isFulfilled()?1:0);
	}
	
	public String toString() {
		return "EDGE='"+ edgeName +"'";
	}

}
