/**
 * 
 */
package org.graphwalker.statistics;

import java.util.HashSet;

import org.graphwalker.graph.AbstractElement;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;

/**
 * @author Johan Tejle
 * 
 */
public class VertexCoverageStatistics extends Statistics {

	private int max;
	private HashSet<String> usedVertices;

	/**
	 * 
	 */
	public VertexCoverageStatistics(Graph model) {
		max = model.getVertices().size();
		usedVertices = new HashSet<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphwalker.statistics.Statistics#addProgress(edu.uci.ics.jung.graph
	 * .impl.AbstractElement)
	 */
	public void addProgress(AbstractElement element) {
		if (element instanceof Vertex)
			usedVertices.add(element.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphwalker.statistics.Statistics#getCurrent()
	 */
	public int getCurrent() {
		return usedVertices.size();
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
