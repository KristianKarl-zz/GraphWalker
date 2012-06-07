package org.graphwalker.analyze;

import java.util.List;

import org.graphwalker.Keywords;
import org.graphwalker.ModelBasedTesting;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;

public class Analyze {

  /**
   * Look for vertices that are not reachable from any other vertex in the graph.
   * 
   * @param mbt
   */
  public static String unreachableVertices(ModelBasedTesting mbt) {
    StringBuffer str = new StringBuffer();
    Graph graph = mbt.getMachine().getModel();
    for (Vertex source : mbt.getMachine().getModel().getVertices()) {
      if (source.getLabelKey().equalsIgnoreCase(Keywords.START_NODE)) {
        continue;
      }
      for (Vertex target : mbt.getMachine().getModel().getVertices()) {
        if (target.getLabelKey().equalsIgnoreCase(Keywords.START_NODE)) {
          continue;
        }
        if (target == source) {
          continue;
        }
        List<Edge> dijkstraShortestPath = new DijkstraShortestPath<Vertex, Edge>(graph).getPath(source, target);
        if (dijkstraShortestPath.size() > 0) {
          continue;
        }
        str.append("There is no way to reach: " + target + ", from: " + source + "\n");
      }
    }
    return str.toString();
  }

}
