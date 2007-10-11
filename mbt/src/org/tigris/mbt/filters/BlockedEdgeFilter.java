package org.tigris.mbt.filters;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.filters.EfficientFilter;
import edu.uci.ics.jung.graph.filters.GeneralEdgeAcceptFilter;

public class BlockedEdgeFilter extends GeneralEdgeAcceptFilter  implements EfficientFilter
{

	public boolean acceptEdge(Edge edge) {
		return !edge.containsUserDatumKey("BLOCKED");
	}

	public String getName() {
		return "BlockedEdgeFilter";
	}

}
