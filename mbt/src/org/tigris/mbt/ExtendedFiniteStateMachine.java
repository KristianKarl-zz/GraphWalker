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
	private Stack actionStack;

	private Stack edgenameStack;
	
	public ExtendedFiniteStateMachine(SparseGraph newModel) {
		super(newModel);
		actionStack = new Stack();
		edgenameStack = new Stack();
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
			Edge e = (Edge) i.next();
			if(!accessableFilter.acceptEdge( e ))
			{
				logger.debug("Not accessable: " + Util.getCompleteEdgeName((DirectedSparseEdge) e) + " from " + getCurrentStateName());
				i.remove();
			}
			else
			{
				logger.debug("Accessable: " + Util.getCompleteEdgeName((DirectedSparseEdge) e) + " from " + getCurrentStateName());
			}
		}
		if(retur.size()==0)
		{
			throw new RuntimeException( "Cul-De-Sac, dead end found in '" + Util.getCompleteVertexName( getCurrentState() ) + "'");
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
			if(hasAction(edge))
			{
				try {
					interpreter.eval(getAction(edge));
				} catch (EvalError e) {
					throw new RuntimeException( "Malformed action sequence: " + Util.getCompleteEdgeName(edge) +" : "+ e.getMessage() );
				}
			}
		}
		return hasWalkedEdge;
	}

	private String getAction(Edge edge) {
		return (edge==null?"":(String)edge.getUserDatum(Keywords.ACTIONS_KEY));
	}

	private boolean hasAction(Edge edge) {
		return (edge==null?false:edge.containsUserDatumKey(Keywords.ACTIONS_KEY));
	}

	public void track()
	{
		super.track();
		DirectedSparseEdge edge = getLastEdge();
		String pushedName = (edge==null?"<START>":getEdgeName(edge));
		edgenameStack.push(pushedName);
		actionStack.push(getAction(edge));
	}
	
	public void popState()
	{
		super.popState();
		edgenameStack.pop();
		actionStack.pop();
		interpreter.getNameSpace().clear();
		for(int i = 0;i < actionStack.size();i++)
		{
			try {
				String action = (String) actionStack.get(i);
				if(action != null && !action.equals("")) interpreter.eval(action);
			} catch (EvalError e) {
				throw new RuntimeException( "Malformed action: " + e.getMessage() );
			}
		}
	}
}
