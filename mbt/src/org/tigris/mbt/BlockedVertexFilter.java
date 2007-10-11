package org.tigris.mbt;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.filters.GeneralVertexAcceptFilter;

public class BlockedVertexFilter extends GeneralVertexAcceptFilter {

	public boolean acceptVertex(Vertex vertex) {
		return !vertex.containsUserDatumKey("BLOCKED");
	}

	public String getName() {
		return "BlockedVertexFilter";
	}

}
