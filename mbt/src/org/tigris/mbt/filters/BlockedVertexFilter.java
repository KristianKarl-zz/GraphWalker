package org.tigris.mbt.filters;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.filters.EfficientFilter;
import edu.uci.ics.jung.graph.filters.GeneralVertexAcceptFilter;

public class BlockedVertexFilter extends GeneralVertexAcceptFilter  implements EfficientFilter
{

	public boolean acceptVertex(Vertex vertex) {
		return !vertex.containsUserDatumKey("BLOCKED");
	}

	public String getName() {
		return "BlockedVertexFilter";
	}

}
