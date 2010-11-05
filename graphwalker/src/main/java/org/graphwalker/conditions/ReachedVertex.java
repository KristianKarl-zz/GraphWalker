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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.graphwalker.Util;
import org.graphwalker.graph.Vertex;
import org.graphwalker.machines.ExtendedFiniteStateMachine;
import org.graphwalker.machines.FiniteStateMachine;

public class ReachedVertex extends StopCondition {

	private static Logger logger = Util.setupLogger(ReachedVertex.class);
	private ArrayList<Vertex> allVertices;
	private Vertex endVertex;
	private int[] proximity;
	private int maxDistance;
	private String vertexName;
	private String subState;

	public boolean isFulfilled() {
		return getFulfilment() >= 0.99999;
	}

	public void setMachine(FiniteStateMachine machine) {
		super.setMachine(machine);
		if (this.endVertex == null)
			this.endVertex = machine.findVertex(vertexName);
		if (this.endVertex == null)
			throw new RuntimeException("Vertex '" + vertexName + "' not found in model");
		this.proximity = getFloydWarshall();
		this.maxDistance = max(this.proximity);
	}

	public ReachedVertex(String vertexName) {
		String[] vertex = vertexName.split("/", 2);
		this.vertexName = vertex[0];
		this.subState = (vertex.length > 1 ? vertex[1] : "");
	}

	public double getFulfilment() {
		logger.debug("Machine: " + getMachine());
		int distance = proximity[allVertices.indexOf(getMachine().getCurrentVertex())];
		if (getMachine() instanceof ExtendedFiniteStateMachine) {
			String currentVertex = getMachine().getCurrentVertexName();
			String currentSubState = "";
			if ( vertexName.equals(Vertex.getLabel(currentVertex))) {
				if (currentVertex.contains("/")) {
					currentSubState = currentVertex.split("/", 2)[1];
					Pattern actionPattern = Pattern.compile(this.subState);
					Matcher actionMatcher = actionPattern.matcher(currentSubState);
					if (actionMatcher.find()) {
						return 1;
					}
				}
			}
			return 0;
		}

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
		allVertices = new ArrayList<Vertex>(getMachine().getAllVertices());
		int n = allVertices.size();
		int[][] retur = new int[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				int x = 99999;
				if (i == j) {
					x = 0;
				} else if (getMachine().getModel().isPredecessor(allVertices.get(i), allVertices.get(j))) {
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
		int startIndex = allVertices.indexOf(endVertex);
		if (startIndex >= 0)
			return path[startIndex];
		throw new RuntimeException("vertex no longer in Graph!");
	}

	public String toString() {
		return "VERTEX='" + endVertex + "'";
	}

}
