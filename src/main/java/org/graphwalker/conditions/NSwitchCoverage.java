// This file is part of the GraphWalker java package
// The MIT License
//
// Copyright (c) 2010 graphwalker.org
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package org.graphwalker.conditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.graphwalker.exceptions.FoundNoEdgeException;
import org.graphwalker.graph.Edge;


/**
 * 
 * @author petjoh
 * 
 *         This stop condition realizes the N-Switch-Coverage criterion. It terminates when all
 *         combinations of N consecutive edges have been traversed.
 * 
 */
public class NSwitchCoverage extends StopCondition {

  private final HashMap<Integer, Integer> pathsFound = new HashMap<Integer, Integer>();
  private final List<Edge> currentPath = new ArrayList<Edge>();
  private final int depth;
  private boolean inCycle = false;

  public NSwitchCoverage() {
    this(0);
  }

  public NSwitchCoverage(int depth) {
    this.depth = depth;
  }

  @Override
  public boolean isFulfilled() {
    if (inCycle) {
      currentPath.add(machine.getLastEdge());
      // Set current path as traversed
      if (currentPath.size() == depth + 1) {
        pathsFound.put(currentPath.hashCode(), 1);
        currentPath.remove(0);
      }
      if (currentPath.size() == depth) {
        Set<Edge> availableEdges;
        try {
          availableEdges = getMachine().getCurrentOutEdges();
        } catch (FoundNoEdgeException e) {
          if (!(pathsFound.size() == 0 || pathsFound.containsValue(0))) {
            return true;
          }
          throw new RuntimeException("No possible edges available for path", e);
        }

        // For every available edge add the resulting path if not already added as un-traversed
        for (Edge edge : availableEdges) {
          currentPath.add(edge);
          if (pathsFound.get(currentPath.hashCode()) == null) {
            pathsFound.put(currentPath.hashCode(), 0);
          }
          currentPath.remove(edge);
        }
      }
    }
    // Simple check to make sure we have cleared the initialization vertexes. If we don't require a
    // cycle in the model the coverage will never be met.
    if (machine.getCurrentInEdges().size() > 1) {
      inCycle = true;
    }

    // Special case for when we don't ever find a cycle, straight model
    if (!inCycle && machine.getUncoveredEdges().size() == 0) {
      return true;
    }
    return !(pathsFound.size() == 0 || pathsFound.containsValue(0));
  }

  @Override
  public double getFulfilment() {
    double visited = 0, unvisited = 0;
    if (!inCycle && machine.getUncoveredEdges().size() == 0) {
      return 1;
    }

    if (pathsFound.size() == 0) {
      return 0;
    }

    for (Integer value : pathsFound.values()) {
      if (value == 0) {
        unvisited++;
      } else {
        visited++;
      }
    }
    return visited / (visited + unvisited);
  }

  @Override
  public String toString() {
    return "N='" + depth + "'";
  }

}
