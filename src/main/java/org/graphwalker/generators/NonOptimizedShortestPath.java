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

package org.graphwalker.generators;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.graphwalker.Util;
import org.graphwalker.conditions.StopCondition;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Vertex;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;

public class NonOptimizedShortestPath extends RandomPathGenerator {

  private boolean toggleAllOrUnvisited = true;

  public NonOptimizedShortestPath(StopCondition stopCondition) {
    super(stopCondition);
  }

  private static Logger logger = Util.setupLogger(NonOptimizedShortestPath.class);
  private List<Edge> dijkstraShortestPath;

  public NonOptimizedShortestPath() {
    super();
  }

  @Override
  public String[] getNext() throws InterruptedException {
    Util.AbortIf(!hasNext(), "Finished");

    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    Edge edge = null;
    do {
      if (!setDijkstraPath()) {
        return super.getNext();
      }
      edge = dijkstraShortestPath.remove(0);
    } while (!isEdgeAvailable(edge));

    getMachine().walkEdge(edge);
    return new String[] {getMachine().getEdgeName(edge), getMachine().getCurrentVertexName()};
  }

  private boolean setDijkstraPath() {
    // Is there a path to walk, given from DijkstraShortestPath?
    if (dijkstraShortestPath == null || dijkstraShortestPath.size() == 0) {
      Vector<Edge> unvisitedEdges = getMachine().getUncoveredEdges();
      logger.debug("Number of unvisited edges: " + unvisitedEdges.size());

      Edge e = null;
      if (unvisitedEdges.size() == 0) {
        return false;
      } else {
        Object[] shuffledList = null;
        if (toggleAllOrUnvisited) {
          shuffledList = Util.shuffle(unvisitedEdges.toArray());
        } else {
          shuffledList = Util.shuffle(getMachine().getAllEdgesExceptStartEdge().toArray());
        }
        toggleAllOrUnvisited = !toggleAllOrUnvisited;

        e = (Edge) shuffledList[0];
      }

      logger.debug("Current vertex: " + getMachine().getCurrentVertex());
      logger.debug("Will try to reach unvisited edge: " + e);

      dijkstraShortestPath =
          new DijkstraShortestPath<Vertex, Edge>(getMachine().getModel()).getPath(getMachine().getCurrentVertex(),
              getMachine().getModel().getSource(e));

      // DijkstraShortestPath.getPath returns 0 if there is no way to reach the
      // destination. But,
      // DijkstraShortestPath.getPath also returns 0 paths if the the source and
      // destination vertex are the same, even if there is
      // an edge there (self-loop). So we have to check for that.
      if (dijkstraShortestPath.size() == 0) {
        if (!getMachine().getCurrentVertex().getIndexKey().equals(getMachine().getModel().getSource(e).getIndexKey())) {
          if (!toggleAllOrUnvisited) {
            String msg = "There is no way to reach: " + e + ", from: " + getMachine().getCurrentVertex();
            logger.error(msg);
            throw new RuntimeException(msg);
          }
        }
      }

      dijkstraShortestPath.add(e);
      logger.debug("Dijkstra path length to that edge: " + dijkstraShortestPath.size());
      logger.debug("Dijksta path:");
      for (Edge object : dijkstraShortestPath) {
        logger.debug("  " + object);
      }
    }
    return true;
  }

  public void emptyCurrentPath() {
    if (dijkstraShortestPath == null || dijkstraShortestPath.size() == 0) {
      return;
    }
    dijkstraShortestPath = null;
  }

  @Override
  public String toString() {
    if (getStopCondition() == null) {
      return "SHORTESTNONOPT";
    } else {
      return "SHORTESTNONOPT{" + getStopCondition().toString() + "}";
    }
  }
}
