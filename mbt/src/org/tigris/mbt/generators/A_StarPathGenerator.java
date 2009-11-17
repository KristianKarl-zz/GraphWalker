package org.tigris.mbt.generators;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.tigris.mbt.Util;
import org.tigris.mbt.exceptions.FoundNoEdgeException;
import org.tigris.mbt.generators.PathGenerator;
import org.tigris.mbt.graph.Edge;
import org.tigris.mbt.graph.Vertex;
import org.tigris.mbt.machines.FiniteStateMachine;

public class A_StarPathGenerator extends PathGenerator {

	static Logger logger = Util.setupLogger(A_StarPathGenerator.class);

	private Stack<Edge> preCalculatedPath = null;
	private Vertex lastState;

	public void setMachine(FiniteStateMachine machine) {
		super.setMachine(machine);
	}

	public String[] getNext() {
		Util.AbortIf(!hasNext(), "Finished");
		if (lastState == null || lastState != getMachine().getCurrentState() || preCalculatedPath == null || preCalculatedPath.size() == 0) {
			boolean oldCalculatingPathValue = getMachine().isCalculatingPath();
			getMachine().setCalculatingPath(true);

			preCalculatedPath = a_star();

			getMachine().setCalculatingPath(oldCalculatingPathValue);

			if (preCalculatedPath == null) {
				throw new RuntimeException("No path found to " + this.getStopCondition());
			}

			// reverse path
			Stack<Edge> temp = new Stack<Edge>();
			while (preCalculatedPath.size() > 0) {
				temp.push(preCalculatedPath.pop());
			}
			preCalculatedPath = temp;
		}

		Edge edge = (Edge) preCalculatedPath.pop();
		getMachine().walkEdge(edge);
		lastState = getMachine().getCurrentState();
		String[] retur = { getMachine().getEdgeName(edge), getMachine().getCurrentStateName() };
		return retur;
	}

	@SuppressWarnings("unchecked")
	private Stack<Edge> a_star() {
		Vector<String> closed = new Vector<String>();

		PriorityQueue<WeightedPath> q = new PriorityQueue<WeightedPath>(10, new Comparator<WeightedPath>() {
			public int compare(WeightedPath arg0, WeightedPath arg1) {
				int retur = Double.compare(arg0.getWeight(), arg1.getWeight());
				if (retur == 0)
					retur = arg0.getPath().size() - arg1.getPath().size();
				return retur;
			}
		});

		Set<Edge> availableOutEdges;
		try {
			availableOutEdges = getMachine().getCurrentOutEdges();
		} catch (FoundNoEdgeException e) {
			throw new RuntimeException("No available edges found at " + getMachine().getCurrentStateName(), e);
		}
		for (Iterator<Edge> i = availableOutEdges.iterator(); i.hasNext();) {
			Edge y = i.next();
			Stack<Edge> s = new Stack<Edge>();
			s.push(y);
			q.add(getWeightedPath(s));
		}
		double maxWeight = 0;
		while (q.size() > 0) {
			WeightedPath p = (WeightedPath) q.poll();
			if (p.getWeight() > maxWeight)
				maxWeight = p.getWeight();
			if (p.getWeight() > 0.99999) // are we done yet?
				return p.getPath();

			Edge x = (Edge) p.getPath().peek();

			// have we been here before?
			if (closed.contains(x.hashCode() + "." + p.getSubState().hashCode() + "." + p.getWeight()))
				continue; // ignore this and move on

			// We don't want to use this edge again as this path is
			// the fastest, and if we come here again we have used more
			// steps to get here than we used this time.
			closed.add(x.hashCode() + "." + p.getSubState().hashCode() + "." + p.getWeight());

			availableOutEdges = getPathOutEdges(p.getPath());
			if (availableOutEdges != null && availableOutEdges.size() > 0) {
				for (Iterator<Edge> i = availableOutEdges.iterator(); i.hasNext();) {
					Edge y = i.next();
					Stack<Edge> newStack = (Stack<Edge>) p.getPath().clone();
					newStack.push(y);
					q.add(getWeightedPath(newStack));
				}
			}
		}
		throw new RuntimeException("No path found to satisfy stop condition " + getStopCondition() + ", best path satified only "
		    + (int) (maxWeight * 100) + "% of condition.");
	}

	private WeightedPath getWeightedPath(Stack<Edge> path) {
		double weight = 0;
		String subState = "";

		getMachine().storeState();
		getMachine().walkEdge(path);
		weight = getConditionFulfilment();
		String currentState = getMachine().getCurrentStateName();
		if (currentState.contains("/")) {
			subState = currentState.split("/", 2)[1];
		}
		getMachine().restoreState();

		return new WeightedPath(path, weight, subState);
	}

	private Set<Edge> getPathOutEdges(Stack<Edge> path) {
		Set<Edge> retur = null;
		getMachine().storeState();
		getMachine().walkEdge(path);
		try {
			retur = getMachine().getCurrentOutEdges();
		} catch (FoundNoEdgeException e) {
			// no edges found? degrade gracefully and return the default value of
			// null.
		}
		getMachine().restoreState();
		return retur;
	}

	/**
	 * Will reset the generator to its initial state.
	 */
	public void reset() {
		preCalculatedPath = null;
	}

	public String toString() {
		return "A_STAR{" + super.toString() + "}";
	}

	private class WeightedPath {
		private double weight;
		private Stack<Edge> path;
		private String subState;

		public String getSubState() {
			return subState;
		}

		public void setSubState(String subState) {
			this.subState = subState;
		}

		public Stack<Edge> getPath() {
			return path;
		}

		public void setPath(Stack<Edge> path) {
			this.path = path;
		}

		public double getWeight() {
			return weight;
		}

		public void setWeight(double weight) {
			this.weight = weight;
		}

		public WeightedPath(Stack<Edge> path, double weight, String subState) {
			setPath(path);
			setWeight(weight);
			setSubState(subState);
		}
	}
}