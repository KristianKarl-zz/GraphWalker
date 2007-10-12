package org.tigris.mbt;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import bsh.EvalError;
import bsh.Interpreter;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

public class ExtendedFiniteStateMachine extends FiniteStateMachine {

	public ExtendedFiniteStateMachine(SparseGraph newModel) {
		super(newModel);
	}

	private Interpreter interpreter = new Interpreter();
	
	public String getCurrentStateName()
	{
		String stateName = super.getCurrentStateName(); 
		if( hasData() )
		{
			stateName += "/" + getCurrentData();
		}
		return stateName;
	}

	public Set getCurrentAvailableEdges()
	{
		HashSet retur = new HashSet();
		Iterator i = currentState.getOutEdges().iterator();
		while(i.hasNext())
		{
			DirectedSparseEdge edge = (DirectedSparseEdge) i.next();
			Hashtable label = splitEdge(edge);
			try {
				if(	!label.containsKey(Keywords.GUARD_KEY) || 
					((Boolean) interpreter.eval((String) label.get(Keywords.GUARD_KEY))).booleanValue())
				{
					retur.add(edge);
				}
			} catch (EvalError e) {
				e.printStackTrace();
			}
		}
		return retur;
	}

	protected Hashtable splitEdge(DirectedSparseEdge edge)
	{
		Hashtable retur = new Hashtable();
		String label = ((String) edge.getUserDatum(Keywords.LABEL_KEY)).trim();
		// Label Parameter [Guard] / Action
		int splitPosition = label.indexOf("/");
		if(splitPosition > 0)
		{
			retur.put(Keywords.ACTION_KEY, label.substring(splitPosition+1).trim());
			label = label.substring(0, splitPosition).trim();
		}
		splitPosition = label.indexOf("[");
		if(splitPosition > 0)
		{
			int endIndex = label.lastIndexOf("]");
			if(endIndex > 0)
			{
				retur.put(Keywords.GUARD_KEY, label.substring(splitPosition+1, endIndex).trim());
			} else {
				throw new RuntimeException( "Malformed Edge guard: " + edge.getUserDatum(Keywords.LABEL_KEY) );
			}
			label = label.substring(0, splitPosition).trim();
		}
		splitPosition = label.indexOf(" ");
		if(splitPosition > 0)
		{
			retur.put(Keywords.PARAMETER_KEY, label.substring(splitPosition+1).trim());
			label = label.substring(0, splitPosition).trim();
		}
		retur.put(Keywords.LABEL_KEY, label);
		return retur;
	}
	
	private boolean hasData()
	{
		return interpreter.getNameSpace().getVariableNames().length > 1;
	}
	
	public String getCurrentData() {
		if(!hasData()) return "";
		String retur = "";
		List variables = Arrays.asList(interpreter.getNameSpace().getVariableNames());
		Iterator i = variables.iterator();
		try {
			while(i.hasNext())
			{
				String key = (String) i.next();
				if(!key.equals("bsh"))
				{
					retur += key + "=" + interpreter.get(key) + ";";
				}
			}
		} catch (EvalError e) {
			e.printStackTrace();
		}
		return retur;
	}
	
	public void walkEdge(DirectedSparseEdge edge)
	{
		if(currentState.isSource(edge))
		{
			Hashtable label = splitEdge(edge);
			if(label.containsKey(Keywords.ACTION_KEY))
			{
				try {
					interpreter.eval((String) label.get(Keywords.ACTION_KEY));
				} catch (EvalError e) {
					e.printStackTrace();
				}
			}
			currentState = (DirectedSparseVertex) edge.getDest();
		}
	}
}
