package org.tigris.mbt;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.tigris.mbt.filters.AccessableEdgeFilter;

import bsh.EvalError;
import bsh.Interpreter;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.SparseGraph;

public class ExtendedFiniteStateMachine extends FiniteStateMachine {

	static Logger logger = Logger.getLogger(ExtendedFiniteStateMachine.class);
	
	private Interpreter interpreter = new Interpreter();
	private AccessableEdgeFilter accessableFilter;
	private Stack dataStack;
	
	public ExtendedFiniteStateMachine(SparseGraph newModel) {
		super(newModel);
		dataStack = new Stack();
		accessableFilter = new AccessableEdgeFilter(interpreter);
	}

	public String getCurrentStateName()
	{
		return super.getCurrentStateName() + (hasInternalVariables()?"/" + getCurrentDataString():"");
	}

	public Set getCurrentOutEdges()
	{
		HashSet retur = new HashSet();
		for(Iterator i = currentState.getOutEdges().iterator();i.hasNext();)
		{
			DirectedSparseEdge edge = (DirectedSparseEdge) i.next();
			if(accessableFilter.acceptEdge(edge))
			{
				retur.add(edge);
			}
		}
		if(retur.size()==0)
		{
			logger.fatal("Cul-De-Sac: Dead end found in '" + getCurrentStateName() + "', aborting.");
			System.exit(-1);
		}
		return retur;
	}

	private boolean hasInternalVariables()
	{
		return interpreter.getNameSpace().getVariableNames().length > 1;
	}
	
	public Hashtable getCurrentData() {
		Hashtable retur = new Hashtable();
		if(!hasInternalVariables()) return retur;

		try {
			String[] variables = interpreter.getNameSpace().getVariableNames();
			for(int  i=0; i<variables.length; i++)
			{
				String key = variables[i];
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
	
	public boolean walkEdge(DirectedSparseEdge edge)
	{
		boolean hasWalkedEdge = super.walkEdge(edge);
		if(hasWalkedEdge)
		{
			if(edge.containsUserDatumKey(Keywords.ACTIONS_KEY))
			{
				String actions = (String)edge.getUserDatum(Keywords.ACTIONS_KEY);
				try {
					interpreter.eval(actions);
				} catch (EvalError e) {
					logger.error(e);
					logger.error( Util.getCompleteEdgeName( edge ) );
					throw new RuntimeException( "Malformed action sequence: " + e.getMessage() );
				}
			}
		}
		return hasWalkedEdge;
	}

	public void pushState()
	{
		super.pushState();
		String data = getCurrentDataString();
		dataStack.push(data);
	}

	public void peekState()
	{
		super.peekState();
		String data = (String) dataStack.peek();
		try {
			interpreter.eval(data);
		} catch (EvalError e) {
			throw new RuntimeException( "Malformed data: '" + data + "' " + e.getMessage() );
		}
	}
	
	public void popState()
	{
		super.popState();
		String data = (String) dataStack.pop();
		try {
			interpreter.eval(data);
		} catch (EvalError e) {
			throw new RuntimeException( "Malformed data: '" + data + "' " + e.getMessage() );
		}
	}
	
}
