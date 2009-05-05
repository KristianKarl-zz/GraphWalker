/**
 * 
 */
package org.tigris.mbt.statistics;

import java.util.HashSet;

import org.tigris.mbt.AbstractElement;
import org.tigris.mbt.Graph;
import org.tigris.mbt.Vertex;

/**
 * @author Johan Tejle
 *
 */
public class StateCoverageStatistics extends Statistics {

	private int max;
	private HashSet<String> usedStates;
	/**
	 * 
	 */
	public StateCoverageStatistics( Graph model) {
		max = model.getVertices().size();
		usedStates = new HashSet<String>();
	}
	
	/* (non-Javadoc)
	 * @see org.tigris.mbt.statistics.Statistics#addProgress(edu.uci.ics.jung.graph.impl.AbstractElement)
	 */
	public void addProgress(AbstractElement element) {
		if(element instanceof Vertex)
			usedStates.add(element.toString());
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
