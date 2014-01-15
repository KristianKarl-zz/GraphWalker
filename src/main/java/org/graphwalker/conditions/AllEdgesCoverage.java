package org.graphwalker.conditions;

import org.graphwalker.Keywords;
import org.graphwalker.graph.Edge;

import java.util.Collection;

/**
 * Stops if all edges were visited at least N times
 */
public class AllEdgesCoverage extends StopCondition {

  private int factor;

  public AllEdgesCoverage(int factor) {
    if (factor < 1) {
      throw new IllegalArgumentException("Argument should be greater than 0");
    }
    this.factor = factor;
  }

  @Override
  public boolean isFulfilled() {
    return ( getFulfilment() > 0.99999999 );
  }

  @Override
  public double getFulfilment() {
    final Collection<Edge> allEdges = machine.getAllEdges();
    int matched = 0;
    for (Edge edge : allEdges) {

      // don't care for the edge from the Start vertex
      if ( getMachine().getModel().getSource(edge).getLabelKey().equalsIgnoreCase(Keywords.START_NODE)) {
        continue;
      }

      if (edge.getVisitedKey() >= factor) {
        matched++;
      }
    }

    // don't care for the edge from the Start vertex
    return (double) matched / (allEdges.size() - 1);
  }
}
