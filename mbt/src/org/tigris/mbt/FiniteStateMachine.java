//This file is part of the Model-based Testing java package
//Copyright (C) 2005  Kristian Karl
//
//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

package org.tigris.mbt;

import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.tigris.mbt.exceptions.FoundNoEdgeException;

import edu.uci.ics.jung.graph.impl.AbstractElement;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserData;

/**
 * @author Johan Tejle
 *
 */
public class FiniteStateMachine{
	
	static Logger logger = Util.setupLogger(FiniteStateMachine.class);

	protected SparseGraph model = null;
	protected DirectedSparseVertex currentState = null;
	private boolean weighted = false;
	private DirectedSparseEdge lastEdge = null;
	private Stack edgeStack;
	private Stack statestore;
	private int numberOfEdgesTravesed = 0;
	protected boolean backtracking = false;
	protected boolean abortOnDeadEnds = true;
	protected boolean calculatingPath = false;

	private long start_time;

	private Hashtable associatedRequirements;

	protected void setState(String stateName)
	{
		logger.debug("Setting state to: '" + stateName + "'");
		DirectedSparseVertex e = findState(stateName);
		Util.AbortIf(e == null, "Vertex not Found: '" + stateName + "'");
		
		currentState = e;
		setAsVisited(e);
	}

	public DirectedSparseVertex findState(String stateName)
	{
		for(Iterator i = model.getVertices().iterator(); i.hasNext();)
		{
			DirectedSparseVertex e = (DirectedSparseVertex) i.next();
			if( ((String)e.getUserDatum(Keywords.LABEL_KEY)).equals(stateName))
			{
				return e;
			}
		}
		return null;
	}
	
	public FiniteStateMachine(SparseGraph model)
	{
		this();
		this.setModel(model);
	}
	
	public FiniteStateMachine()
	{
		logger.debug("Initializing");
		edgeStack = new Stack();
		start_time = System.currentTimeMillis();
	}
	
	public void setModel(SparseGraph model) {
		this.model = model;
		setState(Keywords.START_NODE);
	}
	
	public DirectedSparseVertex getCurrentState() {
		return currentState;
	}

	public String getCurrentStateName()
	{
		return getStateName(currentState);
	}
	
	public Set getAllStates()
	{
		return model.getVertices();
	}
	
	public Set getAllEdges()
	{
		return model.getEdges();
	}
	
	public String getStateName(DirectedSparseVertex state)
	{
		return (String) state.getUserDatum(Keywords.LABEL_KEY);
	}
	
	public Set getCurrentOutEdges() throws FoundNoEdgeException
	{
		Set retur = new HashSet(currentState.getOutEdges());
		if(retur.size()==0)
		{
			throw new FoundNoEdgeException( "Cul-De-Sac, dead end found in '" + Util.getCompleteName( getCurrentState() ) + "'" );
		}
		return retur;
	}
	
	public void setAsVisited(AbstractElement e)
	{
		int visits = getVisited(e);
		setVisited(e, visits + 1);

		if(e.containsUserDatumKey(Keywords.REQTAG_KEY))
		{
			Hashtable reqs = getAllRequirements();
			String[] tags = ((String)e.getUserDatum(Keywords.REQTAG_KEY)).split( "," );
			for ( int j = 0; j < tags.length; j++ ) 
			{
				reqs.put( tags[j], new Integer(((Integer)reqs.get(tags[j])).intValue()+1));	
			}
		}
	}
	
	public void setAsUnvisited(AbstractElement e)
	{
		int visits = getVisited(e);
		setVisited(e, visits - 1);

		if(visits <= 0 )
			logger.error( "Edge: " + Util.getCompleteName( e ) + ", has a negative number in VISITED_KEY" );
		
		if(e.containsUserDatumKey(Keywords.REQTAG_KEY))
		{
			Hashtable reqs = getAllRequirements();
			String[] tags = ((String)e.getUserDatum(Keywords.REQTAG_KEY)).split( "," );
			for ( int j = 0; j < tags.length; j++ ) 
			{
				reqs.put( tags[j], new Integer(((Integer)reqs.get(tags[j])).intValue()-1));	
			}
		}
	}
	
	protected int getVisited(AbstractElement element)
	{
		int retur = 0;
		if(element.containsUserDatumKey( Keywords.VISITED_KEY ))
			retur = ((Integer)element.getUserDatum( Keywords.VISITED_KEY )).intValue();
		return retur; 
	}
	
	protected void setVisited(AbstractElement element, int timesVisited)
	{
		element.setUserDatum(Keywords.VISITED_KEY, new Integer(timesVisited), UserData.SHARED);
	}
	
	public void walkEdge(Stack p) 
	{
		for(int i = 0; i < p.size(); i++)
		{
			walkEdge((DirectedSparseEdge) p.get(i));
		}
	}
	
	public boolean walkEdge(DirectedSparseEdge edge)
	{
		if(currentState.isSource(edge))
		{
			lastEdge = edge;
			if(isBacktrackPossible())
			{
				track();
			}

			currentState = (DirectedSparseVertex) edge.getDest();
			setAsVisited(lastEdge);
			setAsVisited(currentState);
			numberOfEdgesTravesed++;
			logger.debug( "No. of walked edges: " + numberOfEdgesTravesed );
			return true;
		}
		else
			logger.error( "Edge: " + Util.getCompleteName( edge ) + ", is not the source of: " + Util.getCompleteName( currentState ) );
		return false;
	}

	public DirectedSparseEdge getLastEdge()
	{
		return lastEdge;
	}
	
	public String getStatisticsStringCompact()
	{
		int stats[] = getStatistics();
		int e   = stats[0];
		int ec  = stats[1];
		int v   = stats[2];
		int vc  = stats[3];
		int len = stats[4];
		int req = stats[5];
		int reqc= stats[6];
		
		return 
		(req>0?"RC: " + reqc + "/" + req + " => " +  (100*reqc)/req + "% ":"") +
		"EC: " + ec + "/" + e + " => " + (100*ec)/e + "% " +
		"SC: " + vc + "/" + v + " => " + (100*vc)/v + "% " +
		"L: " + len; 
	}

	public String getStatisticsString()
	{
		int stats[] = getStatistics();
		int e   = stats[0];
		int ec  = stats[1];
		int v   = stats[2];
		int vc  = stats[3];
		int len = stats[4];
		int req = stats[5];
		int reqc= stats[6];
		
		return 
		(req>0?"Coverage Requirements: " + reqc + "/" + req + " => " +  (100*reqc)/req + "%\n":"") +
		"Coverage Edges: " + ec + "/" + e + " => " +  (100*ec)/e + "%\n" + 
		"Coverage States: " + vc + "/" + v + " => " + (100*vc)/v  + "%\n" + 
		"Unvisited Edges:  " + (e-ec) + "\n" + 
		"Unvisited States: " + (v-vc) + "\n" +
		"Testcase length:  " + len; 
	}
	
	public int[] getStatistics()
	{
		Set e = model.getEdges();
		Set v = model.getVertices();

		int[] retur = {e.size(), getCoverage(e), v.size(), getCoverage(v), numberOfEdgesTravesed, getAllRequirements().size(), getCoveredRequirements().size()};
		return retur;
	}
	
	public String getStatisticsVerbose()
	{
		String retur = "";
		String newLine = "\n";
		
		Vector notCovered = new Vector();
		for(Iterator i = model.getEdges().iterator();i.hasNext();)
		{
			DirectedSparseEdge e = (DirectedSparseEdge) i.next();
			if(!isVisited(e))
			{
				notCovered.add( "Edge not reached: " + Util.getCompleteName(e) + newLine);
			}
		}
		for(Iterator i = model.getVertices().iterator();i.hasNext();)
		{
			DirectedSparseVertex v = (DirectedSparseVertex) i.next();
			if(!isVisited(v))
			{
				notCovered.add( "Vertex not reached: " + Util.getCompleteName(v) + newLine);
			}
		}
		if(notCovered.size()>0)
		{
			Collections.sort(notCovered);
			for(Iterator i = notCovered.iterator();i.hasNext();)
			{
				retur += i.next();
			}
		}
		retur += getStatisticsString() + newLine;
		retur += "Execution time: " + ( ( System.currentTimeMillis() - start_time ) / 1000 ) + " seconds";
		return retur;
	}

	public boolean isVisited(AbstractElement abstractElement) {
		return abstractElement.containsUserDatumKey( Keywords.VISITED_KEY ) && ((Integer)abstractElement.getUserDatum( Keywords.VISITED_KEY )).intValue() > 0;
	}

	protected int getCoverage(Set modelItems)
	{
		int unique = 0;
		
		for(Iterator i=modelItems.iterator(); i.hasNext();)
		{
			AbstractElement ae = (AbstractElement) i.next();
			if(ae.containsUserDatumKey(Keywords.VISITED_KEY))
			{
				if(((Integer) ae.getUserDatum( Keywords.VISITED_KEY )).intValue()>0)
				{
					unique++;
				}
			}
		}
		
		return unique;
	}
	
	public Hashtable getAllRequirements()
	{
		if(associatedRequirements == null)
		{ 
			associatedRequirements = new Hashtable();
			
			Vector abstractElements = new Vector();
			abstractElements.addAll(getAllStates());
			abstractElements.addAll(getAllEdges());
			
			for(Iterator i = abstractElements.iterator();i.hasNext();)
			{
				AbstractElement ae = (AbstractElement) i.next();
				String reqtags = (String)ae.getUserDatum(Keywords.REQTAG_KEY);
				if(reqtags != null )
				{
					String[] tags = reqtags.split( "," );
					for ( int j = 0; j < tags.length; j++ ) 
					{
						associatedRequirements.put( tags[j], new Integer(0) );	
					}
				}
			}
		}
		return associatedRequirements;
	}
	
	public Set getCoveredRequirements()
	{
		Vector notCoveredValues = new Vector();
		notCoveredValues.add(new Integer(0));
		Hashtable allRequirements = (Hashtable) getAllRequirements().clone();
		allRequirements.values().removeAll(notCoveredValues);
		return allRequirements.keySet();
	}
	
	public String getEdgeName(DirectedSparseEdge edge)
	{
		String l = (String)edge.getUserDatum( Keywords.LABEL_KEY );
		String p = (String)edge.getUserDatum( Keywords.PARAMETER_KEY );
		
		return (l==null ? "" : l) + (p==null ? "" : " " + p);
	}
	
	public void storeState()
	{
		if(this.statestore == null)
			this.statestore = new Stack();
		this.statestore.push(new Integer(edgeStack.size()));
	}
	
	public void restoreState()
	{
		if(this.statestore == null || this.statestore.size() == 0)
			throw new RuntimeException("Nothing to restore");
		int prevState = ((Integer)this.statestore.pop()).intValue();
		if( prevState > edgeStack.size())
			throw new RuntimeException("Cannot restore state from backtrack");
		while(prevState < edgeStack.size())
		{
			popState();
		}
	}
	
	protected void track()
	{
		edgeStack.push(getLastEdge());
	}
	
	protected void popState()
	{
		setAsUnvisited(getLastEdge());
		setAsUnvisited(getCurrentState());

		edgeStack.pop();
		if(lastEdge == null)
		{
			setState(Keywords.START_NODE);
		} else {
			currentState = (DirectedSparseVertex) lastEdge.getSource();
		}
		lastEdge = (edgeStack.size()>0?(DirectedSparseEdge) edgeStack.peek():null);
		numberOfEdgesTravesed--;
	}
		
	/**
	 * @param weighted if edge weights are to be considered
	 */
	public void setWeighted(boolean weighted) 
	{
		this.weighted = weighted;
	}
	/**
	 * @return true if the edge weights is considered
	 */
	public boolean isWeighted() 
	{
		return weighted;
	}

	/**
	 * @return the number of edges traversed
	 */
	public int getNumberOfEdgesTravesed() 
	{
		return numberOfEdgesTravesed;
	}

	public void backtrack()
	{
		if(isBacktrackPossible())
		{
			popState();
		} else {
			if(!isLastEdgeBacktrackSupported())
				throw new RuntimeException( "Backtracking was asked for, but model does not suppport BACKTRACK at egde: " + Util.getCompleteName( getLastEdge() ) );	
			if(!isBacktrackEnabled())
				throw new RuntimeException( "Backtracking was asked for, but was disabled." );			
			throw new RuntimeException( "Backtracking was asked for, but was refused" );						
		}
	}
	
	public void setBacktrackEnabled(boolean backtracking) 
	{
		this.backtracking  = backtracking;
	}

	public boolean isBacktrackEnabled() 
	{
		return this.backtracking;
	}

	public boolean isBacktrackPossible() 
	{
		return isBacktrackEnabled() || isCalculatingPath() || isLastEdgeBacktrackSupported();
	}
	
	private boolean isLastEdgeBacktrackSupported()
	{
		return getLastEdge().containsUserDatumKey( Keywords.BACKTRACK );
	}
	
	public boolean isCalculatingPath() {
		return calculatingPath;
	}

	public void setCalculatingPath(boolean calculatingPath) {
		this.calculatingPath = calculatingPath;
	}
	/**
	 * This functions returns a list of edges, which has not yet been visited
	 * @return DirectedSparseEdge
	 */
	public Vector getUnvisitedEdges()
	{
		Vector retur = new Vector();
		for ( Iterator i = getAllEdges().iterator(); i.hasNext();)
		{
			AbstractElement element = (AbstractElement) i.next();
			if(!isVisited( element ))
			{
				retur.add( element );
			}
		}
		return retur;
	}
	
	public SparseGraph getModel()
	{
		return model;
	}
}

