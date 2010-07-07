package org.graphwalker.conditions;

import java.util.ArrayList;

import org.graphwalker.graph.Edge;
import org.graphwalker.machines.FiniteStateMachine;

public class ReachedEdge extends StopCondition {

	private ArrayList<Edge> allEdges;
	private Edge endEdge;
	private int[] proximity;
	private int maxDistance;
	private String edgeName;

	public boolean isFulfilled() {
		return getFulfilment() >= 0.99999;
	}

	public void setMachine(FiniteStateMachine machine) {
		super.setMachine(machine);
		if (this.endEdge == null)
			this.endEdge = machine.findEdge(edgeName);
		if (this.endEdge == null)
			throw new RuntimeException("Vertex '" + edgeName + "' not found in model");
		this.proximity = getFloydWarshall();
		this.maxDistance = max(this.proximity);
	}

	public ReachedEdge(Edge endEdge) {
		this.endEdge = endEdge;
	}

	public ReachedEdge(String edgeName) {
		String[] vertex = edgeName.split("/", 2);
		this.edgeName = vertex[0];
	}

	public double getFulfilment() {
		int distance = this.maxDistance;
		if (getMachine().getLastEdge() != null)
			distance = proximity[allEdges.indexOf(getMachine().getLastEdge())];

		return ((double) 1) - ((double) distance / (double) maxDistance);
	}

	private int max(int[] t) {
		int maximum = t[0];
		for (int i = 1; i < t.length; i++) {
			if (t[i] > maximum) {
				maximum = t[i];
			}
		}
		return maximum;
	}

	private int[][] getFloydWarshallMatrix() {
		allEdges = new ArrayList<Edge>(getMachine().getAllEdges());
		int n = allEdges.size();
		int[][] retur = new int[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				int x = 99999;
				if (i == j) {
					x = 0;
				} else if (getMachine().getModel().isSource(getMachine().getModel().getDest(allEdges.get(j)), allEdges.get(i))) {
					x = 1;
				}
				retur[i][j] = x;
			}
		return retur;
	}

	private int[] getFloydWarshall() {
		int path[][] = getFloydWarshallMatrix();
		int n = path.length;
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++) {
					path[i][j] = Math.min(path[i][j], path[i][k] + path[k][j]);
				}
		}
		int startIndex = allEdges.indexOf(endEdge);
		if (startIndex >= 0)
			return path[startIndex];
		throw new RuntimeException("edge no longer in Graph!");
	}

	public String toString() {
		return "EDGE='" + endEdge + "'";
	}
}
