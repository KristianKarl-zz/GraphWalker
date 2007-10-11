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

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

/**
 * @author Johan Tejle
 *
 */
public class FiniteStateMachine{

	public static final int METHOD_RANDOMIZED = 1;
	public static final int METHOD_ALL_PATHS = 2;
	
	protected SparseGraph model = null;
	protected DirectedSparseVertex currentState = null;
	
	public void setState(String stateName)
	{
		Iterator i = model.getVertices().iterator();
		while(i.hasNext())
		{
			DirectedSparseVertex v = (DirectedSparseVertex)i.next();
			if( ((String)v.getUserDatum(Keywords.LABEL_KEY)).equals(stateName))
			{
				currentState = v;
				return;			
			}
		}
	}
	
	public FiniteStateMachine(SparseGraph newModel)
	{
		model = newModel;
		
// Following two lines could be used to hide/remove blocked edges and vertexes in the graph
//		model.getEdgeConstraints().add(new BlockedEdgeFilter());
//		model.getVertexConstraints().add(new BlockedVertexFilter());
	}
	
	public String getCurrentStateName()
	{
		return (String) currentState.getUserDatum(Keywords.LABEL_KEY);
	}
	
	public Set getCurrentAvailableEdges()
	{
		return currentState.getOutEdges();
	}
	
	public void walkEdge(DirectedSparseEdge edge)
	{
		if(currentState.isSource(edge))
		{
			currentState = (DirectedSparseVertex) edge.getDest();
		}
	}

	public Set getPath(int method, String fromState, String toState)
	{
		setState(fromState);
		return getPath(method, toState);
	}

	public Set getPath(int method, String toState)
	{
		switch(method)
		{
		case METHOD_RANDOMIZED:
			return getRandomPath( Integer.parseInt(toState) );
		case METHOD_ALL_PATHS:
			return getAllPath( currentState, toState );			
		}
		return null;
	}
	
	private Set getAllPath(DirectedSparseVertex fromState, String toState) 
	{
		Vector testSuit = new Vector();
		Stack currentPath = new Stack();
		
		//TODO: add allpath functionality		
		currentPath.add("// NOT IMPLEMENTED!");
		
		testSuit.add(currentPath.toString());
		return (Set) testSuit;
	}

	private Set getRandomPath(int length) 
	{
		Vector testSuit = new Vector();
		StringBuffer path = new StringBuffer();
		DirectedSparseEdge nextEdge;
		Random random = new Random();
		while(length>0)
		{
			Set availableEdges = getCurrentAvailableEdges();
			if(availableEdges.size() == 0) 
			{
				throw new RuntimeException( "Found a dead end: '" + getCurrentStateName() + "'" );
			}
			nextEdge = (DirectedSparseEdge) availableEdges.toArray()[random.nextInt(availableEdges.size())];
			path.append(nextEdge.getUserDatum(Keywords.LABEL_KEY)+"\n");
			walkEdge(nextEdge);
			path.append(getCurrentStateName()+"\n");
			length -= 1;
		}
		testSuit.add(path.toString());
		return (Set) testSuit;
	}
}
