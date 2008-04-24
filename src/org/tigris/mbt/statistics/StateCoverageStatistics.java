/**
 * 
 */
package org.tigris.mbt.statistics;

import java.util.HashSet;

import org.tigris.mbt.Util;

import edu.uci.ics.jung.graph.impl.AbstractElement;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;

/**
 * @author Johan Tejle
 *
 */
public class StateCoverageStatistics extends Statistics {

	private int max;
	private HashSet usedStates;
	/**
	 * 
	 */
	public StateCoverageStatistics( SparseGraph model) {
		max = model.getVertices().size();
		usedStates = new HashSet();
	}
	
	/* (non-Javadoc)
	 * @see org.tigris.mbt.statistics.Statistics#addProgress(edu.uci.ics.jung.graph.impl.AbstractElement)
	 */
	public void addProgress(AbstractElement element) {
		if(element instanceof DirectedSparseVertex)
			usedStates.add(Util.getCompleteName(element));
	}

	/* (non-Javadoc)
	 * @see org.tigris.mbt.statistics.Statistics#getCurrent()
	 */
	public int getCurrent() {
		return usedStates.size();
	}

	/* (non-Javadoc)
	 * @see org.tigris.mbt.statistics.Statistics#getMax()
	 */
	public int getMax() {
		return max;
	}

}
