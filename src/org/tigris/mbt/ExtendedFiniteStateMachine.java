package org.tigris.mbt;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.tigris.mbt.filters.AccessableEdgeFilter;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.UtilEvalError;

import edu.uci.ics.jung.graph.Edge;
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
		Set	retur = super.getCurrentOutEdges();
		for(Iterator i = retur.iterator();i.hasNext();)
		{
			if(!accessableFilter.acceptEdge( (Edge) i.next()))
			{
				i.remove();
			}
		}
		if(retur.size()==0)
		{
			throw new RuntimeException( "Cul-De-Sac: Dead end found in '" + getCurrentStateName() + "', aborting.2");
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
			NameSpace ns = interpreter.getNameSpace();
			String[] variableNames = interpreter.getNameSpace().getVariableNames();
			for(int  i=0; i<variableNames.length; i++)
			{
				if(!variableNames[i].equals("bsh"))
				{
					retur.put(variableNames[i], ns.getVariable(variableNames[i]));
				}
			}
		} catch (UtilEvalError e) {
			throw new RuntimeException( "Malformed model data: " + e.getMessage() );
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
		dataStack.push(getCurrentData());
	}

	public void peekState()
	{
		super.peekState();
		Hashtable data = (Hashtable) dataStack.peek();
		try {
			for(Enumeration e = data.keys();e.hasMoreElements();)
			{
				String variableName = (String) e.nextElement();
				interpreter.getNameSpace().setVariable(variableName, data.get(variableName), false);
			}
		} catch (UtilEvalError e) {
			throw new RuntimeException( "Malformed data: '" + data + "' " + e.getMessage() );
		}
	}
	
	public void popState()
	{
		super.popState();
		peekState();
		dataStack.pop();
	}
}
