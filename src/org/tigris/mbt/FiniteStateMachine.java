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

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

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

	public static final int METHOD_RANDOMIZED = 1;
	public static final int METHOD_ALL_PATHS = 2;
	public static final int METHOD_SHORTEST_PATH = 3;
	public static final int METHOD_RANDOMIZED_WEIGHTED = 4;
	
	protected SparseGraph model = null;
	protected DirectedSparseVertex currentState = null;
	private long startTimeStamp;
	private Stack stateStack;
	private Random random = new Random();
	private boolean weighted = false;
	private DirectedSparseEdge lastEdge = null;
	
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
		setState(Keywords.START_NODE);
	}
	
	public String getCurrentStateName()
	{
		return (String) currentState.getUserDatum(Keywords.LABEL_KEY);
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
		String retur = ( System.currentTimeMillis() - startTimeStamp ) + 
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
	
	public Set getPath(int method, String fromState, String toState)
	{
		setState(fromState);
		return getPath(method, toState);
	}

	public Set getPath(int method, String toState)
	{
		startTimeStamp = System.currentTimeMillis();
		switch(method)
		{
		case METHOD_RANDOMIZED:
			return getRandomPath( Integer.parseInt(toState) );
		case METHOD_SHORTEST_PATH:
			return getShortestPath( toState );
		}
		return null;
	}
	
	private Set getRandomPath(int length) 
	{
		HashSet testSuit = new HashSet();
		LinkedList edgePath = new LinkedList();

		pushState();
		
		while(length>0)
		{
			Set availableEdges = getCurrentOutEdges();
			DirectedSparseEdge edge = null;
			Util.AbortIf(availableEdges.size() == 0, "Found a dead end: '" + getCurrentStateName() + "'"); 
			if( isWeighted() )
			{
				Vector zeroes = new Vector();
				float sum = 0;
				float limit = random.nextFloat();

				for ( Iterator i = availableEdges.iterator(); i.hasNext();)
				{
					DirectedSparseEdge e = (DirectedSparseEdge)i.next();
					Float weight = (Float) e.getUserDatum( Keywords.WEIGHT_KEY );
					if(weight == null)
					{
						zeroes.add(e);
					} else {
						sum += weight.floatValue();
						if(sum >= limit && edge == null) edge = e;
					}
				}
				Util.AbortIf( sum > 1 ,"The weight of out edges excceds 1 for " + getCurrentStateName() );	
				if( edge == null )
				{
					edge = (DirectedSparseEdge) zeroes.get(random.nextInt(zeroes.size()));
				}
			}else{
				edge = (DirectedSparseEdge) availableEdges.toArray()[random.nextInt(availableEdges.size())];
			}
			edgePath.add(edge);
			walkEdge((DirectedSparseEdge) edge);
			length--;
		}

		popState();
		
		testSuit.add(getScriptFromPath(edgePath).toString());
		return (Set) testSuit;
	}

	protected Set getShortestPath( String toState )
	{
		HashSet testSuit = new HashSet();
		// Clear up model..
		restoreModel();
		
		DirectedSparseVertex targetState = null;
		for(Iterator i = model.getVertices().iterator();i.hasNext();)
		{
			DirectedSparseVertex dsv = (DirectedSparseVertex)i.next();
			if(((String)dsv.getUserDatum(Keywords.LABEL_KEY)).equals(toState))
			{
				targetState = dsv;
			}
		}
		Util.AbortIf(targetState == null, "Destination not Found: '" + toState + "'" );
		
		pushState();
		calculateShortestPath( targetState );
		popState();

		DijkstraPoint dp = ((DijkstraPoint)targetState.getUserDatum(Keywords.DIJKSTRA));
		testSuit.add(getScriptFromPath(dp.getShortestPath()).toString());
		

		return (Set) testSuit;
	}
	
	public String getEdgeName(DirectedSparseEdge edge)
	{
		String l = (String)edge.getUserDatum( Keywords.LABEL_KEY );
		String p = (String)edge.getUserDatum( Keywords.PARAMETER_KEY );
		
		return (l==null ? "" : l) + (p==null ? "" : " " + p);
	}
	
	protected StringBuffer getScriptFromPath(LinkedList edgePath) {
		pushState();
		StringBuffer path = new StringBuffer();
		for(Iterator i = edgePath.iterator();i.hasNext();)
		{
			DirectedSparseEdge nextEdge = (DirectedSparseEdge) i.next();
			path.append(getEdgeName(nextEdge)+"\n");
			walkEdge(nextEdge);
			path.append(getCurrentStateName()+"\n");
		}
		popState();
		return path;
	}
	protected void restoreModel()
	{
		for(Iterator i = model.getVertices().iterator();i.hasNext();)
		{
			DirectedSparseVertex dsv = (DirectedSparseVertex)i.next();
			dsv.setUserDatum(Keywords.DIJKSTRA, new DijkstraPoint(), UserData.SHARED );
		}
	}
	
	private void calculateShortestPath( DirectedSparseVertex toState )
	{
		DijkstraPoint dp = ((DijkstraPoint)currentState.getUserDatum(Keywords.DIJKSTRA));
		LinkedList edgePath = (LinkedList) dp.getPath().clone();
		Set oe = getCurrentOutEdges();
		for(Iterator i = oe.iterator();i.hasNext();)
		{
			DirectedSparseEdge e = (DirectedSparseEdge)i.next();
			edgePath.add(e);
			pushState();
			walkEdge(e);
			DijkstraPoint nextDp = ((DijkstraPoint)currentState.getUserDatum(Keywords.DIJKSTRA));
			if(nextDp.compareTo(edgePath)>0)
			{
				nextDp.setPath(edgePath);
				if(!currentState.equals(toState))
				{
					calculateShortestPath( toState );
				}
			}
			popState();
			edgePath.removeLast();
		}
	}
	
	protected void pushState()
	{
		stateStack.push(currentState);
	}
	
	protected void peekState()
	{
		currentState = (DirectedSparseVertex) stateStack.peek();
	}

	protected void popState()
	{
		currentState = (DirectedSparseVertex) stateStack.pop();
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

	protected class DijkstraPoint implements Comparable
	{
		protected LinkedList edgePath = null;
		
		public DijkstraPoint()
		{
		}
		
		public void setPath( LinkedList edgePath )
		{
			this.edgePath = (LinkedList) edgePath.clone();
		}

		public LinkedList getPath()
		{
			if(edgePath==null)edgePath = new LinkedList();
			return edgePath;
		}

		public LinkedList getShortestPath()
		{
			if(edgePath==null)edgePath = new LinkedList();
			return edgePath;
		}

		public int compareTo(Object o) {
			int a = getPath().size();
			if(a==0) return 1;
			int b = 0;
			if(o instanceof LinkedList)
			{
				b = ((LinkedList)o).size();
			} else {
				b = ((DijkstraPoint)o).getPath().size();
			}
		
			return a-b;
		}
		
		
	}
}

