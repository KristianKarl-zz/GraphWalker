//This file is part of the GraphWalker java package
//The MIT License
//
//Copyright (c) 2010 graphwalker.org
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

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

	public PathGenerator(StopCondition stopCondition) {
		this.stopCondition = stopCondition;
	}

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

	@Override
	public String toString() {
		if (getStopCondition() != null)
			return getStopCondition().toString();
		return "";
	}

	protected boolean isEdgeAvailable(Edge edge) {
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
