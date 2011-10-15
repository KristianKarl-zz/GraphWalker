/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.graphwalker.Util;
import org.graphwalker.conditions.StopCondition;
import org.graphwalker.exceptions.FoundNoEdgeException;
import org.graphwalker.graph.Edge;

/**
 * The generator generates paths through the model in a way that makes sure all
 * path permutations of ever increasing depth is tested.
 * 
 * The first pass through the model finishes when a cycle in the model is found.
 * The second pass traverses the model until all edges have been visited twice
 * or more. The third pass traverses the model until all available combinations
 * of 2 edges have been visited twice or more . . . The n:th pass traverses the
 * model until all available combinations of n edges have been visited twice or
 * more
 * 
 * The algorithm always tries the "path less traveled" where a path is a list of
 * n edges. If several paths are available with the same number of traversals on
 * is chosen by random.
 */
public class AllPathPermutationsGenerator extends PathGenerator {

	private static Logger logger = Util.setupLogger(AllPathPermutationsGenerator.class);

	private final Random random = new Random();

	/* Contains all walked paths for a specific depth */
	private final HashMap<Integer, Integer> pathWalked = new HashMap<Integer, Integer>();

	/* List of edges comprising the current path */
	private final List<Edge> savedEdges = new ArrayList<Edge>();

	private int currentDepth;

	public AllPathPermutationsGenerator(final StopCondition stopCondition) {
		super(stopCondition);
		currentDepth = 0;
	}

	public AllPathPermutationsGenerator() {
		super();
		currentDepth = 0;
	}

	@Override
	public String[] getNext() throws InterruptedException {
		Set<Edge> availableEdges;

		try {
			availableEdges = getMachine().getCurrentOutEdges();
		} catch (FoundNoEdgeException e) {
			throw new RuntimeException("No possible edges available for path", e);
		}

		Set<Edge> selectedEdges = new HashSet<Edge>();
		int fewestVisits = -1;

		// Loop through available edges and pick the one which results in a path
		// least traversed.
		for (Edge edge : availableEdges) {

			// Add available edge to the current path
			savedEdges.add(edge);

			// Has it been traversed before and if so how many times
			Integer hashValue = pathWalked.get(savedEdges.hashCode());

			// Never walked path
			if (hashValue == null) {
				fewestVisits = 0;
				selectedEdges.add(edge);

				// Has been traversed but fewer times
			} else if (hashValue < fewestVisits || fewestVisits == -1) {
				fewestVisits = hashValue;
				selectedEdges.clear();
				selectedEdges.add(edge);

				// Has been traversed but equal times
			} else if (hashValue == fewestVisits) {
				selectedEdges.add(edge);
			}

			// Remove the edge from the current path
			savedEdges.remove(edge);
		}
		Edge selectedEdge = getRandomEdge(selectedEdges);

		// Add the selected edge to the current path
		savedEdges.add(selectedEdge);

		// Update number of traversals of this path
		pathWalked.put(savedEdges.hashCode(), fewestVisits + 1);

		// Check if all paths have been traversed twice or more
		// If so, increase the length of the path by 1 and clear the hash
		if (checkCompletion()) {
			currentDepth++;
			AllPathPermutationsGenerator.logger.debug("All combinations done, changing look back depth to: " + currentDepth);
			pathWalked.clear();
		} else if (currentDepth == 0 && checkCompletionFirst()) {
			currentDepth++;
			AllPathPermutationsGenerator.logger.debug("Cycle detected, starting algorithm with depth: " + currentDepth);
			pathWalked.clear();
			savedEdges.remove(0);

			// If not, remove the first edge in the path
		} else {
			savedEdges.remove(0);
		}

		getMachine().walkEdge(selectedEdge);
		AllPathPermutationsGenerator.logger.debug(selectedEdge.getFullLabelKey());
		AllPathPermutationsGenerator.logger.debug(selectedEdge);
		AllPathPermutationsGenerator.logger.trace("Current Path: " + printPath());
		AllPathPermutationsGenerator.logger.debug("Hash size: " + pathWalked.size());
		AllPathPermutationsGenerator.logger.trace("Hash: " + printHash());
		return new String[] { getMachine().getEdgeName(selectedEdge), getMachine().getCurrentVertexName() };
	}

	private boolean checkCompletionFirst() {
		for (Integer value : pathWalked.values()) {
			if (value == 2) {
				return true;
			}
		}
		return false;
	}

	private boolean checkCompletion() {
		for (Integer value : pathWalked.values()) {
			if (value == 1) {
				return false;
			}
		}
		return true;
	}

	private String printPath() {
		StringBuilder stringBuilder = new StringBuilder();
		for (Edge edge : savedEdges) {
			stringBuilder.append(edge.getFullLabelKey());
		}
		return stringBuilder.toString();
	}

	private String printHash() {
		StringBuilder stringBuilder = new StringBuilder();
		for (Integer value : pathWalked.values()) {
			stringBuilder.append(value);
			stringBuilder.append(" ");
		}
		return stringBuilder.toString();

	}

	private Edge getRandomEdge(final Set<Edge> availableEdges) {
		return (Edge) availableEdges.toArray()[random.nextInt(availableEdges.size())];
	}

	@Override
	public String toString() {
		return "COMBINATIONS{" + super.toString() + "}";
	}

	public int getDepth() {
		return currentDepth;
	}
}
