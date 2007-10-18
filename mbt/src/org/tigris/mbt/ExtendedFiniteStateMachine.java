package org.tigris.mbt;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import bsh.EvalError;
import bsh.Interpreter;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.utils.UserData;

public class ExtendedFiniteStateMachine extends FiniteStateMachine {

	private Interpreter interpreter = new Interpreter();
	private Stack dataStack;
	
	public ExtendedFiniteStateMachine(SparseGraph newModel) {
		super(newModel);
		dataStack = new Stack();
	}

	public String getCurrentStateName()
	{
		String stateName = super.getCurrentStateName(); 
		if( hasData() )
		{
			stateName += "/" + getCurrentDataString();
		}
		return stateName;
	}

	public Set getCurrentOutEdges()
	{
		HashSet retur = new HashSet();
		Iterator i = currentState.getOutEdges().iterator();
		while(i.hasNext())
		{
			DirectedSparseEdge edge = (DirectedSparseEdge) i.next();

			try {
				if(	!edge.containsUserDatumKey(Keywords.GUARD_KEY) || 
					((Boolean) interpreter.eval((String) edge.getUserDatum(Keywords.GUARD_KEY))).booleanValue())
				{
					retur.add(edge);
				}
			} catch (EvalError e) {
				throw new RuntimeException( "Malformed Edge guard: " + e.getErrorText() );
			}
		}
		return retur;
	}

	private boolean hasData()
	{
		return interpreter.getNameSpace().getVariableNames().length > 1;
	}
	
	public Hashtable getCurrentData() {
		Hashtable retur = new Hashtable();
		if(!hasData()) return retur;

		try {
			List variables = Arrays.asList(interpreter.getNameSpace().getVariableNames());
			for(int  i=0; i<variables.size(); i++)
			{
				String key = (String) variables.get(i);
				if(!key.equals("bsh"))
				{
					retur.put(key, interpreter.get(key));
				}
			}
		} catch (EvalError e) {
			throw new RuntimeException( "Malformed model data: " + e.getErrorText() );
		}
		return retur;
	}

	public String getCurrentDataString() 
	{
		String retur = "";
		Hashtable data = getCurrentData();
		Enumeration e = data.keys();
		while(e.hasMoreElements())
		{
			String key = (String) e.nextElement();
			retur +=  key + "=" + data.get(key) + ";";
		}
		return retur;
	}
	
	public void walkEdge(DirectedSparseEdge edge)
	{
		if(currentState.isSource(edge))
		{
			if(edge.containsUserDatumKey(Keywords.ACTIONS_KEY))
			{
				Vector actions = (Vector)edge.getUserDatum(Keywords.ACTIONS_KEY);
				for (Iterator iter = actions.iterator(); iter.hasNext();) 
				{
					String action = (String) iter.next();
					if ( action != null )
					{
						try {
							interpreter.eval(action);
						} catch (EvalError e) {
							throw new RuntimeException( "Malformed action sequence: " + e.getErrorText() );
						}
					}
				}				
			}
			super.walkEdge(edge);
		}
	}

	protected void restoreModel()
	{
		for(Iterator i = model.getVertices().iterator();i.hasNext();)
		{
			DirectedSparseVertex dsv = (DirectedSparseVertex)i.next();
			dsv.setUserDatum(Keywords.DIJKSTRA, new ExtendedDijkstraPoint(this), UserData.SHARED );
		}
	}
	
	protected void pushState()
	{
		super.pushState();
		String data = getCurrentDataString();
		dataStack.push(data);
	}

	protected void peekState()
	{
		super.peekState();
		String data = (String) dataStack.peek();
		try {
			interpreter.eval(data);
		} catch (EvalError e) {
			throw new RuntimeException( "Malformed data: '" + data + "' " + e.getErrorText() );
		}
	}
	
	protected void popState()
	{
		super.popState();
		String data = (String) dataStack.pop();
		try {
			interpreter.eval(data);
		} catch (EvalError e) {
			throw new RuntimeException( "Malformed data: '" + data + "' " + e.getErrorText() );
		}
	}
	
	protected class ExtendedDijkstraPoint extends DijkstraPoint implements Comparable
	{
		protected Hashtable edgePaths;
		protected ExtendedFiniteStateMachine parent;
		
		public ExtendedDijkstraPoint()
		{
			edgePaths = new Hashtable();
		}
		
		public ExtendedDijkstraPoint( ExtendedFiniteStateMachine parent) {
			this();
			this.parent = parent;
		}

		public void setPath( LinkedList edgePath )
		{
			if(edgePaths == null) edgePaths = new Hashtable();
			edgePaths.put(parent.getCurrentDataString(), edgePath.clone()); 
		}

		public LinkedList getPath()
		{
			String key = parent.getCurrentDataString();
			LinkedList localPath = (LinkedList) edgePaths.get(key);
			if(localPath == null)
			{
				localPath = new LinkedList();
				edgePaths.put(key, localPath);
			}
			return localPath;
		}

		public LinkedList getShortestPath()
		{
			LinkedList retur = null; 
			for(Iterator i = edgePaths.values().iterator();i.hasNext();)
			{
				LinkedList path = (LinkedList) i.next();
				if(retur == null || retur.size() > path.size()) retur = path;
			}
			return retur;
		}
	}
}
