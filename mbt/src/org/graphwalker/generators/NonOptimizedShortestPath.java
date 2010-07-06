package org.graphwalker.generators;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.graphwalker.Util;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Vertex;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;

public class NonOptimizedShortestPath extends RandomPathGenerator {
	static Logger logger = Util.setupLogger(NonOptimizedShortestPath.class);
	private List<Edge> dijkstraShortestPath;

	public String[] getNext() throws InterruptedException {
		Util.AbortIf(!hasNext(), "Finished");

		if (Thread.interrupted()) {
	    throw new InterruptedException();
		}
		Edge edge = null;
		do {
			if (setDijkstraPath() == false) {
				return super.getNext();
			}
			edge = dijkstraShortestPath.remove(0);
		} while (!isEdgeAvailable(edge));

		getMachine().walkEdge(edge);
		String[] retur = { getMachine().getEdgeName(edge), getMachine().getCurrentVertexName() };
		return retur;
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
				Object[] shuffledList = Util.shuffle(unvisitedEdges.toArray());
				e = (Edge) shuffledList[0];
			}

			logger.debug("Current vertex: " + getMachine().getCurrentVertex());
			logger.debug("Will try to reach unvisited edge: " + e);

			dijkstraShortestPath = new DijkstraShortestPath<Vertex, Edge>(getMachine().getModel()).getPath(getMachine().getCurrentVertex(),
			    getMachine().getModel().getSource(e));

			// DijkstraShortestPath.getPath returns 0 if there is no way to reach the
			// destination. But,
			// DijkstraShortestPath.getPath also returns 0 paths if the the source and
			// destination vertex are the same, even if there is
			// an edge there (self-loop). So we have to check for that.
			if (dijkstraShortestPath.size() == 0) {
				if (getMachine().getCurrentVertex().getIndexKey() != getMachine().getModel().getSource(e).getIndexKey()) {
					String msg = "There is no way to reach: " + e + ", from: " + getMachine().getCurrentVertex();
					logger.error(msg);
					throw new RuntimeException(msg);
				}
			}

			dijkstraShortestPath.add(e);
			logger.debug("Dijkstra path length to that edge: " + dijkstraShortestPath.size());
			logger.debug("Dijksta path:");
			for (Iterator<Edge> iterator = dijkstraShortestPath.iterator(); iterator.hasNext();) {
				Edge object = iterator.next();
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
}
