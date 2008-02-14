/**
 * 
 */
package org.tigris.mbt.statistics;

import java.util.HashSet;

import org.tigris.mbt.Util;

import edu.uci.ics.jung.graph.impl.AbstractElement;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseGraph;

/**
 * @author Johan Tejle
 *
 */
public class EdgeCoverageStatistics extends Statistics {

	private int max;
	private HashSet usedEdges;
	/**
	 * 
	 */
	public EdgeCoverageStatistics( SparseGraph model) {
		max = model.getEdges().size();
		usedEdges = new HashSet();
	}
	
	/* (non-Javadoc)
	 * @see org.tigris.mbt.statistics.Statistics#addProgress(edu.uci.ics.jung.graph.impl.AbstractElement)
	 */
	public void addProgress(AbstractElement element) {
		if(element instanceof DirectedSparseEdge)
			usedEdges.add(Util.getCompleteName(element));
	}

	/* (non-Javadoc)
	 * @see org.tigris.mbt.statistics.Statistics#getCurrent()
	 */
	public int getCurrent() {
		return usedEdges.size();
	}

	/* (non-Javadoc)
	 * @see org.tigris.mbt.statistics.Statistics#getMax()
	 */
	public int getMax() {
		return max;
	}

}
