/**
 * 
 */
package org.tigris.mbt.statistics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.tigris.mbt.AbstractElement;
import org.tigris.mbt.Graph;

/**
 * @author Johan Tejle
 *
 */
public class RequirementCoverageStatistics extends Statistics {

	private HashSet<String> usedRequirements;
	private HashSet<String> allRequirements;
	/**
	 * 
	 */
	public RequirementCoverageStatistics( Graph model) {
		usedRequirements = new HashSet<String>();
		allRequirements = new HashSet<String>();

		Vector<AbstractElement> allElements = new Vector<AbstractElement>();
		allElements.addAll(model.getEdges());
		allElements.addAll(model.getVertices());
		for(Iterator<AbstractElement> i = allElements.iterator();i.hasNext();)
		{
			AbstractElement e = i.next();
			if(!e.getReqTagKey().isEmpty())
			{
				appendRequirements(allRequirements, e.getReqTagKey());
			}
		}
	}

	private void appendRequirements(HashSet<String> set, String requirements)
	{
		String[] tags = requirements.split( "," );
		for ( int i = 0; i < tags.length; i++ ) 
		{
			set.add( tags[i] );	
		}
	}
	
	/* (non-Javadoc)
	 * @see org.tigris.mbt.statistics.Statistics#addProgress(edu.uci.ics.jung.graph.impl.AbstractElement)
	 */
	public void addProgress(AbstractElement element) {
		if(element != null && !element.getReqTagKey().isEmpty())
		{
			appendRequirements(usedRequirements, element.getReqTagKey());
		}
	}

	/* (non-Javadoc)
	 * @see org.tigris.mbt.statistics.Statistics#getCurrent()
	 */
	public int getCurrent() {
		return usedRequirements.size();
	}

	/* (non-Javadoc)
	 * @see org.tigris.mbt.statistics.Statistics#getMax()
	 */
	public int getMax() {
		return allRequirements.size();
	}

}
