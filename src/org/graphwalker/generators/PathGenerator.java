package org.graphwalker.generators;

import java.util.Set;

import org.graphwalker.conditions.StopCondition;
import org.graphwalker.exceptions.FoundNoEdgeException;
import org.graphwalker.graph.Edge;
import org.graphwalker.machines.FiniteStateMachine;

public abstract class PathGenerator {
	private FiniteStateMachine machine;
	private StopCondition stopCondition;

	public abstract String[] getNext() throws InterruptedException;

	public boolean hasNext() {
		return !stopCondition.isFulfilled();
	}

	public FiniteStateMachine getMachine() {
		return machine;
	}

	public void setMachine(FiniteStateMachine machine) {
		this.machine = machine;
		if (this.stopCondition != null)
			this.stopCondition.setMachine(machine);
	}

	public void setStopCondition(StopCondition stopCondition) {
		this.stopCondition = stopCondition;
		if (this.machine != null)
			this.stopCondition.setMachine(this.machine);
	}

	public StopCondition getStopCondition() {
		return stopCondition;
	}

	/**
	 * @return the condition fulfilment
	 */
	public double getConditionFulfilment() {
		return stopCondition.getFulfilment();
	}

	/**
	 * Will reset the generator to its initial vertex.
	 */
	public void reset() {
	}

	PathGenerator() {
	}

	PathGenerator(StopCondition stopCondition) {
		setStopCondition(stopCondition);
	}

	PathGenerator(FiniteStateMachine machine, StopCondition stopCondition) {
		this(stopCondition);
		setMachine(machine);
	}

	public String toString() {
		if (getStopCondition() != null)
			return getStopCondition().toString();
		return "";
	}

	public boolean isEdgeAvailable(Edge edge) {
		Set<Edge> availableEdges;
		try {
			availableEdges = getMachine().getCurrentOutEdges();
		} catch (FoundNoEdgeException e) {
			throw new RuntimeException("No possible edges available for path", e);
		}
		if (availableEdges.contains(edge)) {
			return true;
		}

		return false;
	}
}
