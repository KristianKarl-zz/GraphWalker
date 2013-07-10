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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.graphwalker.Util;
import org.graphwalker.conditions.StopCondition;
import org.graphwalker.exceptions.FoundNoEdgeException;
import org.graphwalker.graph.Edge;

public class RandomPathGenerator extends PathGenerator {

  private static Logger logger = Util.setupLogger(RandomPathGenerator.class);

  private Random random = new Random();

  public RandomPathGenerator(StopCondition stopCondition) {
    super(stopCondition);
  }

  public RandomPathGenerator() {
    super();
  }

  @Override
  public String[] getNext() throws InterruptedException {
    Set<Edge> availableEdges;
    try {
      availableEdges = getMachine().getCurrentOutEdges();
    } catch (FoundNoEdgeException e) {
      throw new RuntimeException("No possible edges available for path", e);
    }
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    Edge edge = (getMachine().isWeighted() ? getWeightedEdge(availableEdges) : getRandomEdge(availableEdges));
    getMachine().walkEdge(edge);
    logger.debug(edge.getFullLabelKey());
    logger.debug(edge);
    return new String[] {getMachine().getEdgeName(edge), getMachine().getCurrentVertexName()};
  }

  private Edge getWeightedEdge(Set<Edge> availableEdges) {

    Map<Edge, Double> probabilities = new HashMap<Edge, Double>();
    int numberOfZeros = 0;
    double sum = 0;

    for (Edge edge : availableEdges) {
      if (edge.getWeightKey() > 0) {
        probabilities.put(edge, (double) edge.getWeightKey());
        sum += edge.getWeightKey();
        if (sum > 1) {
          throw new RuntimeException("The sum of all weights in edges from vertex: '" + getMachine().getModel().getSource(edge).getLabelKey()
              + "', adds up to more than 1.00");
        }
      } else {
        numberOfZeros++;
        probabilities.put(edge, 0d);
      }
    }

    double rest = (1 - sum) / numberOfZeros;
    int index = random.nextInt(100);
    logger.debug("Randomized integer index = " + index);

    double weight = 0;

    for (Edge edge : availableEdges) {

      if (probabilities.get(edge) == 0) {
        probabilities.put(edge, rest);
      }

      logger.debug("The edge: '" + edge.getLabelKey() + "' is given the probability of " + probabilities.get(edge) * 100 + "%");

      weight = weight + probabilities.get(edge) * 100;
      logger.debug("Current weight is: " + weight);
      if (index < weight) {
        logger.debug("Selected edge is: " + edge);
        return edge;
      }
    }

    throw new RuntimeException("No edge found");
  }

  private Edge getRandomEdge(Set<Edge> availableEdges) {
    return (Edge) availableEdges.toArray()[random.nextInt(availableEdges.size())];
  }

  @Override
  public String toString() {
    return "RANDOM{" + super.toString() + "}";
  }
}
