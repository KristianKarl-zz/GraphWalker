package org.tigris.mbt;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.filters.GeneralEdgeAcceptFilter;

public class BlockedEdgeFilter extends GeneralEdgeAcceptFilter {

	public boolean acceptEdge(Edge edge) {
		return !edge.containsUserDatumKey("BLOCKED");
	}

	public String getName() {
		return "BlockedEdgeFilter";
	}

}
