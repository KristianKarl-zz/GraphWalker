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
	
	protected SparseGraph model = null;
	protected DirectedSparseVertex currentState = null;
	private long startTimeStamp;
	private Stack stateStack;
	
	protected void setState(String stateName)
	{
		Iterator i = model.getVertices().iterator();
		while(i.hasNext())
		{
			DirectedSparseVertex v = (DirectedSparseVertex)i.next();
			if( ((String)v.getUserDatum(Keywords.LABEL_KEY)).equals(stateName))
			{
				currentState = v;
				setAsVisited(currentState);
				return;			
			}
		}
		throw new RuntimeException( "Vertex not Found: '" + stateName + "'" );
	}
	public void reset()
	{
		setState(Keywords.START_NODE);
	}
	
	public FiniteStateMachine(SparseGraph newModel)
	{
		model = newModel;
		stateStack = new Stack();
	}
	
	public String getCurrentStateName()
	{
		return (String) currentState.getUserDatum(Keywords.LABEL_KEY);
	}
	
	public Set getCurrentOutEdges()
	{
		return currentState.getOutEdges();
	}
	
	protected Hashtable splitEdge(DirectedSparseEdge edge)
	{
		Hashtable retur = new Hashtable();
		String label = (String) edge.getUserDatum(Keywords.LABEL_KEY);
		int splitPosition = label.indexOf(" ");
		if(splitPosition > 0)
		{
			retur.put(Keywords.PARAMETER_KEY, label.substring(splitPosition+1).trim());
			label = label.substring(0, splitPosition).trim();
		}
		retur.put(Keywords.LABEL_KEY, label);
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
			setAsVisited(edge);
			setAsVisited(currentState);
		}
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
		case METHOD_ALL_PATHS:
			return getAllPath( currentState, toState );			
		case METHOD_SHORTEST_PATH:
			return getShortestPath(toState);			
		}
		return null;
	}
	
	private Set getAllPath(DirectedSparseVertex fromState, String toState) 
	{
		HashSet testSuit = new HashSet();
		Stack currentPath = new Stack();
		
		//TODO: add allpath functionality		
		currentPath.add("// NOT IMPLEMENTED!");
		
		testSuit.add(currentPath.toString());
		return (Set) testSuit;
	}

	private Set getRandomPath(int length) 
	{
		HashSet testSuit = new HashSet();
		LinkedList edgePath = new LinkedList();

		Random random = new Random();
		
		pushState();
		
		while(length>0)
		{
			Set availableEdges = getCurrentOutEdges();
			if(availableEdges.size() == 0) 
			{
				throw new RuntimeException( "Found a dead end: '" + getCurrentStateName() + "'" );
			}
			edgePath.add(availableEdges.toArray()[random.nextInt(availableEdges.size())]);
			walkEdge((DirectedSparseEdge) edgePath.getLast());
			length--;
		}
		
		peekState();
		
		testSuit.add(getScriptFromPath(edgePath).toString());
		
		popState();
		
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
		
		if(targetState == null)
		{
			throw new RuntimeException( "Destination not Found: '" + toState + "'" );
		}
		
		pushState();
		
		calculateShortestPath( targetState );
		
		peekState();

		DijkstraPoint dp = ((DijkstraPoint)targetState.getUserDatum(Keywords.DIJKSTRA));
		testSuit.add(getScriptFromPath(dp.getShortestPath()).toString());
		
		popState();

		return (Set) testSuit;
	}
	
	protected StringBuffer getScriptFromPath(LinkedList edgePath) {
		StringBuffer path = new StringBuffer();
		for(Iterator i = edgePath.iterator();i.hasNext();)
		{
			DirectedSparseEdge nextEdge = (DirectedSparseEdge) i.next();
			Hashtable labelParts = splitEdge(nextEdge);
			String l = (String) labelParts.get(Keywords.LABEL_KEY);
			String p = (String) labelParts.get(Keywords.PARAMETER_KEY);
			
			path.append(l + (p==null ? "" : " " + p) + "\n");
			walkEdge(nextEdge);
			path.append(getCurrentStateName()+"\n");
		}
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

