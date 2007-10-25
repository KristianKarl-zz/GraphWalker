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
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;

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
	
	static Logger logger = Logger.getLogger(FiniteStateMachine.class);

	protected SparseGraph model = null;
	protected DirectedSparseVertex currentState = null;
	private Stack stateStack;
	private Stack numberOfEdgesTravesedStack;
	private boolean weighted = false;
	private DirectedSparseEdge lastEdge = null;
	private int numberOfEdgesTravesed = 0;
	protected boolean backtracking = false;
	protected boolean abortOnDeadEnds = true;

	private long start_time;
	
	protected void setState(String stateName)
	{
		logger.debug("Setting state to: '" + stateName + "'");
		DirectedSparseVertex e = findState(stateName);
		Util.AbortIf(e == null, "Vertex not Found: '" + stateName + "'");
		
		currentState = e;
		setAsVisited(e);
	}

	private DirectedSparseVertex findState(String stateName)
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
	
	public FiniteStateMachine(SparseGraph newModel)
	{
		logger.debug("Initializing");
		model = newModel;
		stateStack = new Stack();
		numberOfEdgesTravesedStack = new Stack();
		setState(Keywords.START_NODE);
		start_time = System.currentTimeMillis();
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
	
	public Set getCurrentOutEdges()
	{
		Set retur = new HashSet(currentState.getOutEdges());
		if(retur.size()==0)
		{
			throw new RuntimeException( "Cul-De-Sac: Dead end found in '" + getCurrentStateName() + "', aborting.");
		}
		return retur;
	}
	
	protected void setAsVisited(AbstractElement e)
	{
		Integer visited;
		if(e.containsUserDatumKey(Keywords.VISITED_KEY))
		{
			visited = (Integer) e.getUserDatum( Keywords.VISITED_KEY );
			visited = new Integer( visited.intValue() + 1 );
		}
		else
		{
			visited = new Integer( 1 );
		}
		e.setUserDatum( Keywords.VISITED_KEY, visited, UserData.SHARED );
	}
	
	public boolean walkEdge(DirectedSparseEdge edge)
	{
		if(currentState.isSource(edge))
		{
			if(this.backtracking && edge.containsUserDatumKey(Keywords.BACKTRACK))
			{
				pushState();
				logger.debug("Backtrack +1: " + stateStack.size() + " states stored");
			}
			currentState = (DirectedSparseVertex) edge.getDest();
			lastEdge = edge;
			setAsVisited(edge);
			setAsVisited(currentState);
			numberOfEdgesTravesed++;
			return true;
		}
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
		
		return 
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
		
		return 
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

		int[] retur = {e.size(), getCoverage(e), v.size(), getCoverage(v), numberOfEdgesTravesed};
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
				notCovered.add( "Edge not reached: " + Util.getCompleteEdgeName(e) + newLine);
			}
		}
		for(Iterator i = model.getVertices().iterator();i.hasNext();)
		{
			DirectedSparseVertex v = (DirectedSparseVertex) i.next();
			if(!isVisited(v))
			{
				notCovered.add( "Vertex not reached: " + Util.getCompleteVertexName(v) + newLine);
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

	private boolean isVisited(AbstractElement abstractElement) {
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
	
	public String getEdgeName(DirectedSparseEdge edge)
	{
		String l = (String)edge.getUserDatum( Keywords.LABEL_KEY );
		String p = (String)edge.getUserDatum( Keywords.PARAMETER_KEY );
		
		return (l==null ? "" : l) + (p==null ? "" : " " + p);
	}
	
	public void pushState()
	{
		stateStack.push(currentState);
		numberOfEdgesTravesedStack.push(new Integer(numberOfEdgesTravesed));
	}
	
	public void peekState()
	{
		currentState = (DirectedSparseVertex) stateStack.peek();
		numberOfEdgesTravesed = ((Integer)numberOfEdgesTravesedStack.peek()).intValue(); 
	}

	public void popState()
	{
		peekState();
		stateStack.pop();
		numberOfEdgesTravesedStack.pop(); 
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
		if(this.backtracking && getLastEdge().containsUserDatumKey(Keywords.BACKTRACK)) popState();
	}
	
	public void enableBacktrack(boolean backtracking) 
	{
		this.backtracking  = backtracking;
		
	}
}

