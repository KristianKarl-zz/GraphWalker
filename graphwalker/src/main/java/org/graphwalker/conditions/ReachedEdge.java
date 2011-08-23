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

	@Override
	public boolean isFulfilled() {
		return getFulfilment() >= 0.99999;
	}

	@Override
	public void setMachine(FiniteStateMachine machine) {
		super.setMachine(machine);
		if (this.endEdge == null)
			this.endEdge = machine.findEdge(edgeName);
		if (this.endEdge == null)
			throw new RuntimeException("Vertex '" + edgeName + "' not found in model");
		this.proximity = getFloydWarshall();
		this.maxDistance = max(this.proximity);
	}

	public ReachedEdge(String edgeName) {
		String[] vertex = edgeName.split("/", 2);
		this.edgeName = vertex[0];
	}

	@Override
	public double getFulfilment() {
		int distance = this.maxDistance;
		if (getMachine().getLastEdge() != null)
			distance = proximity[allEdges.indexOf(getMachine().getLastEdge())];

		return (1) - ((double) distance / (double) maxDistance);
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

	@Override
	public String toString() {
		return "EDGE='" + endEdge + "'";
	}
}
