/**
 * 
 */
package org.tigris.mbt.statistics;

import java.util.HashSet;
import java.util.Stack;

import org.tigris.mbt.AbstractElement;
import org.tigris.mbt.Edge;
import org.tigris.mbt.Graph;

/**
 * @author Johan Tejle
 *
 */
public class EdgeSequenceCoverageStatistics extends Statistics {

	private HashSet<String> usedSequences;
	private HashSet<String> allSequences;
	private Stack<AbstractElement> pathHistory;
	private int length;
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public EdgeSequenceCoverageStatistics( Graph model, int sequenceLength ) {
		this.length = sequenceLength;
		usedSequences = new HashSet<String>();
		allSequences = new HashSet<String>();
		pathHistory = new Stack<AbstractElement>();
		
		Stack<Edge>[] possibilities = new Stack[sequenceLength];
		for(int i=0; i<sequenceLength; i++)
			possibilities[i] = new Stack<Edge>();
		possibilities[0].addAll(model.getEdges());
		while(possibilities[0].size()>0)
		{
			for(int i=0; i<sequenceLength-1; i++)
			{
				if(possibilities[i].size()==0)
					return;
				if(possibilities[i+1].size()==0)
					possibilities[i+1].addAll( model.getOutEdges(model.getDest(possibilities[i].peek())) );
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
	private String getSequenceName(Stack<Edge>[] possibilities) {
		String retur="";
		for(int i=0;i<possibilities.length;i++)
			retur += possibilities[i].peek().hashCode() +" ";
		return retur.trim();
	}

	/**
	 * @param pathHistory
	 * @return
	 */
	private String getCurrentSequenceName() {
		String retur="";
		for(int i=0;i<pathHistory.size();i++)
			retur += pathHistory.elementAt(i).hashCode() +" ";
		return retur.trim();
	}

	/* (non-Javadoc)
	 * @see org.tigris.mbt.statistics.Statistics#addProgress(edu.uci.ics.jung.graph.impl.AbstractElement)
	 */
	public void addProgress(AbstractElement element) {
		if(element instanceof Edge)
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
