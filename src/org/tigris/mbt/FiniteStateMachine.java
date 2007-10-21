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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

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
	
	protected SparseGraph model = null;
	protected DirectedSparseVertex currentState = null;
	private Stack stateStack;
	private Stack numberOfEdgesTravesedStack;
	private boolean weighted = false;
	private DirectedSparseEdge lastEdge = null;
	private int numberOfEdgesTravesed = 0;
	
	protected void setState(String stateName)
	{
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
		model = newModel;
		stateStack = new Stack();
		numberOfEdgesTravesedStack = new Stack();
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
	
	public Set getCurrentOutEdges()
	{
		return currentState.getOutEdges();
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
	
	public void walkEdge(List edges)
	{
		for(Iterator i = edges.iterator();i.hasNext();)
		{
			walkEdge( (DirectedSparseEdge) i.next());
		}
	}
	
	public void walkEdge(DirectedSparseEdge edge)
	{
		if(currentState.isSource(edge))
		{
			currentState = (DirectedSparseVertex) edge.getDest();
			lastEdge = edge;
			setAsVisited(edge);
			setAsVisited(currentState);
			numberOfEdgesTravesed++;
		}
	}

	public DirectedSparseEdge getLastEdge()
	{
		return lastEdge;
	}
	
	public String getStatisticsStringHeader()
	{
		return "Timestamp,Edges available,Edges covered,Vertices available,Vertices covered\n";
	}

	public String getStatisticsString()
	{
		Hashtable stats = getStatistics();
		String retur = System.currentTimeMillis() + 
			"," + stats.get("Edges") + 
			"," + stats.get("Edges covered") +
			"," + stats.get("Vertices") + 
			"," + stats.get("Vertices covered") + "\n";
		
		return retur;
	}
	
	public Hashtable getStatistics()
	{
		Hashtable retur = new Hashtable();
		
		Set e = model.getEdges();
		Set v = model.getVertices();

		retur.put("Edges", new Integer(e.size()) );
		retur.put("Edges covered", new Integer(getCoverage(e)) );

		retur.put("Vertices", new Integer(v.size()) );
		retur.put("Vertices covered", new Integer(getCoverage(v)) );

		return retur;
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
		currentState = (DirectedSparseVertex) stateStack.pop();
		numberOfEdgesTravesed = ((Integer)numberOfEdgesTravesedStack.pop()).intValue(); 
	}
	
	/**
	 * @param weighted if edge weights are to be considered
	 */
	public void setWeighted(boolean weighted) {
		this.weighted = weighted;
	}
	/**
	 * @return true if the edge weights is considered
	 */
	public boolean isWeighted() {
		return weighted;
	}

	/**
	 * @return the number of edges traversed
	 */
	public int getNumberOfEdgesTravesed() {
		return numberOfEdgesTravesed;
	}
}

