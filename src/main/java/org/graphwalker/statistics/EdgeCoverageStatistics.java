/**
 * 
 */
package org.graphwalker.statistics;

import java.util.HashSet;

import org.graphwalker.graph.AbstractElement;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;

/**
 * @author Johan Tejle
 * 
 */
public class EdgeCoverageStatistics extends Statistics {

	private int max;
	private HashSet<String> usedEdges;

	/**
	 * 
	 */
	public EdgeCoverageStatistics(Graph model) {
		max = model.getEdges().size();
		usedEdges = new HashSet<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphwalker.statistics.Statistics#addProgress(edu.uci.ics.jung.graph
	 * .impl.AbstractElement)
	 */
	public void addProgress(AbstractElement element) {
		if (element instanceof Edge)
			usedEdges.add(element.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphwalker.statistics.Statistics#getCurrent()
	 */
	public int getCurrent() {
		return usedEdges.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphwalker.statistics.Statistics#getMax()
	 */
	public int getMax() {
		return max;
	}

}
