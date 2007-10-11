package org.tigris.mbt;

import java.util.Enumeration;
import java.util.Hashtable;

import org.tigris.mbt.filters.AccessableEdgeFilter;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

public class ExtendedFiniteStateMachine extends FiniteStateMachine {

	private Hashtable dataStore = new Hashtable();
	public String getCurrentStateName()
	{
		return super.getCurrentStateName() + "/" + getCurrentData();
	}

	public ExtendedFiniteStateMachine(DirectedSparseGraph newModel)
	{
		super(newModel);
		model.getEdgeConstraints().add(new AccessableEdgeFilter(dataStore));
	}
	
	public String getCurrentData() {
		String retur = "";
		Enumeration keys = dataStore.keys();
		while(keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			retur += key + "=" + (String) dataStore.get(key);
			if(keys.hasMoreElements()) retur += ":";
		}
		return retur;
	}
	
	public void walkEdge(DirectedSparseEdge edge)
	{
		if(currentState.isSource(edge))
		{
			//TODO: add parsing of possible actions 
			currentState = (DirectedSparseVertex) edge.getDest();
		}
	}
}
