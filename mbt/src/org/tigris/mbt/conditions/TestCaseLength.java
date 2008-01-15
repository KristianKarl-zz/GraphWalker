package org.tigris.mbt.conditions;

import org.tigris.mbt.FiniteStateMachine;

public class TestCaseLength implements StopCondition {

	private FiniteStateMachine machine;
	private int numberOfEdges;

	public boolean isFulfilled() {
		return machine.getNumberOfEdgesTravesed() >= numberOfEdges;
	}

	public TestCaseLength(FiniteStateMachine machine, int numberOfEdges) {
		this.machine = machine;
		this.numberOfEdges = numberOfEdges;
	}

	public double getFulfillment() {
		return machine.getNumberOfEdgesTravesed() / (double)numberOfEdges;
	}
}
