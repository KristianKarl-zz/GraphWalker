package org.graphwalker.conditions;

import org.graphwalker.Keywords;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Vertex;

import java.util.Collection;

/**
 * Stops if all edges were visited at least N times
 */
public class AllVerticesCoverage extends StopCondition {

  private int factor;

  public AllVerticesCoverage(int factor) {
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
    final Collection<Vertex> allVertices = machine.getAllVertices();
    int matched = 0;
    for (Vertex vertex : allVertices) {

      // don't care for the edge from the Start vertex
      if ( vertex.getLabelKey().equalsIgnoreCase(Keywords.START_NODE)) {
        continue;
      }

      if (vertex.getVisitedKey() >= factor) {
        matched++;
      }
    }

    // don't care for the edge from the Start vertex
    return (double) matched / (allVertices.size() - 1);
  }
}
