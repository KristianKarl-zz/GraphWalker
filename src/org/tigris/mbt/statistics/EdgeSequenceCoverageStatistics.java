/**
 * 
 */
package org.tigris.mbt.statistics;

import java.util.HashSet;
import java.util.Stack;

import org.tigris.mbt.Util;

import edu.uci.ics.jung.graph.impl.AbstractElement;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseGraph;

/**
 * @author Johan Tejle
 *
 */
public class EdgeSequenceCoverageStatistics extends Statistics {

	private HashSet usedSequences;
	private HashSet allSequences;
	private Stack pathHistory;
	private int length;
	/**
	 * 
	 */
	public EdgeSequenceCoverageStatistics( SparseGraph model, int sequenceLength ) {
		this.length = sequenceLength;
		usedSequences = new HashSet();
		allSequences = new HashSet();
		pathHistory = new Stack();
		
		Stack[] possibilities = new Stack[sequenceLength];
		for(int i=0; i<sequenceLength; i++)
			possibilities[i] = new Stack();
		possibilities[0].addAll(model.getEdges());
		while(possibilities[0].size()>0)
		{
			for(int i=0; i<sequenceLength-1; i++)
			{
				if(possibilities[i].size()==0)
					return;
				if(possibilities[i+1].size()==0)
					possibilities[i+1].addAll(((DirectedSparseEdge)possibilities[i].peek()).getDest().getOutEdges());
			}
			while(possibilities[sequenceLength-1].size()>0)
			{
				allSequences.add(getSequenceName(possibilities));
				possibilities[sequenceLength-1].pop();
			}
			for(int i=sequenceLength-1;i>0;i--)
				if(possibilities[i].size()==0)
					possibilities[i-1].pop();
		}
	}
	
	/**
	 * @param possibilities
	 * @return
	 */
	private String getSequenceName(Stack[] possibilities) {
		String retur="";
		for(int i=0;i<possibilities.length;i++)
			retur += Util.getCompleteName((AbstractElement) possibilities[i].peek()) +" ";
		return retur.trim();
	}

	/**
	 * @param pathHistory
	 * @return
	 */
	private String getCurrentSequenceName() {
		String retur="";
		for(int i=0;i<pathHistory.size();i++)
			retur += Util.getCompleteName((AbstractElement) pathHistory.elementAt(i)) +" ";
		return retur.trim();
	}

	/* (non-Javadoc)
	 * @see org.tigris.mbt.statistics.Statistics#addProgress(edu.uci.ics.jung.graph.impl.AbstractElement)
	 */
	public void addProgress(AbstractElement element) {
		if(element instanceof DirectedSparseEdge)
			pathHistory.add(element);
		if(pathHistory.size()>this.length)
			pathHistory.remove(0);
		if(pathHistory.size()==this.length)
			usedSequences.add(getCurrentSequenceName());
	}

	/* (non-Javadoc)
	 * @see org.tigris.mbt.statistics.Statistics#getCurrent()
	 */
	public int getCurrent() {
		return usedSequences.size();
	}

	/* (non-Javadoc)
	 * @see org.tigris.mbt.statistics.Statistics#getMax()
	 */
	public int getMax() {
		return allSequences.size();
	}

}
