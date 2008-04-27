/**
 * 
 */
package org.tigris.mbt.statistics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.tigris.mbt.Keywords;

import edu.uci.ics.jung.graph.impl.AbstractElement;
import edu.uci.ics.jung.graph.impl.SparseGraph;

/**
 * @author Johan Tejle
 *
 */
public class RequirementCoverageStatistics extends Statistics {

	private HashSet usedRequirements;
	private HashSet allRequirements;
	/**
	 * 
	 */
	public RequirementCoverageStatistics( SparseGraph model) {
		usedRequirements = new HashSet();
		allRequirements = new HashSet();

		Vector allElements = new Vector();
		allElements.addAll(model.getEdges());
		allElements.addAll(model.getVertices());
		for(Iterator i = allElements.iterator();i.hasNext();)
		{
			AbstractElement e = (AbstractElement) i.next();
			if(e.containsUserDatumKey(Keywords.REQTAG_KEY))
			{
				appendRequirements(allRequirements, (String) e.getUserDatum(Keywords.REQTAG_KEY));
			}
		}
	}

	private void appendRequirements(HashSet set, String requirements)
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
		if(element != null && element.containsUserDatumKey(Keywords.REQTAG_KEY))
		{
			appendRequirements(usedRequirements, (String) element.getUserDatum(Keywords.REQTAG_KEY));
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
