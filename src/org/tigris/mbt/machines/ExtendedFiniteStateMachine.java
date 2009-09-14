package org.tigris.mbt.machines;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.tigris.mbt.Util;
import org.tigris.mbt.exceptions.FoundNoEdgeException;
import org.tigris.mbt.exceptions.InvalidDataException;
import org.tigris.mbt.filters.AccessableEdgeFilter;
import org.tigris.mbt.graph.Edge;
import org.tigris.mbt.graph.Graph;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.Primitive;
import bsh.UtilEvalError;

public class ExtendedFiniteStateMachine extends FiniteStateMachine {

	private PrintStream Void;

	static Logger logger = Util.setupLogger(ExtendedFiniteStateMachine.class);
	
	private Interpreter interpreter = new Interpreter();
	private AccessableEdgeFilter accessableFilter;

	private Stack<CannedNameSpace> namespaceStack;

	private PrintStream oldPrintStream;
	
	public ExtendedFiniteStateMachine(Graph model)
	{
		this();
		setModel(model);
	}
	
	public ExtendedFiniteStateMachine() {
		super();
		namespaceStack = new Stack<CannedNameSpace>();
		accessableFilter = new AccessableEdgeFilter(interpreter);
		Void = new VoidPrintStream();
	}

	public void eval( String script )
	{
		try {
			interpreter.eval(script);
		} catch ( EvalError e ) {
			logger.error( "Problem when running: '" + script + "' in BeanShell" );
			logger.error( "EvalError: "  + e );
			logger.error( e.getCause() );
			throw new RuntimeException("Execution of startup script generated an error.",e);
		}
	}
	
	public String getCurrentStateName()
	{
		return super.getCurrentStateName() + (hasInternalVariables()?"/" + getCurrentDataString():"");
	}

	public Set<Edge> getCurrentOutEdges() throws FoundNoEdgeException
	{
		Set<Edge>	retur = super.getCurrentOutEdges();
		for(Iterator<Edge> i = retur.iterator();i.hasNext();)
		{
			Edge e = i.next();
			if ( !accessableFilter.acceptEdge( getModel(), e ) )
			{
				logger.debug("Not accessable: " + e + " from " + getCurrentStateName());
				i.remove();
			}
			else
			{
				logger.debug("Accessable: " + e + " from " + getCurrentStateName());
			}
		}
		if ( retur.size() == 0 )
		{
			throw new FoundNoEdgeException("Cul-De-Sac, dead end found in '" + getCurrentState() + "'");
		}
		return retur;
	}

	public boolean hasInternalVariables()
	{
		return interpreter.getNameSpace().getVariableNames().length > 1;
	}
	
	public Hashtable<String, Object> getCurrentData() {
		Hashtable<String, Object> retur = new Hashtable<String, Object>();
		if(!hasInternalVariables()) return retur;

		int i = 0;
		NameSpace ns = interpreter.getNameSpace();
		String[] variableNames = interpreter.getNameSpace().getVariableNames();
		try {
			for ( ; i < variableNames.length; i++ )
			{
				if ( !variableNames[i].equals( "bsh" ) )
				{
					retur.put(variableNames[i], Primitive.unwrap( ns.getVariable(variableNames[i])));
				}
			}
		} catch ( UtilEvalError e ) {
			throw new RuntimeException( "Malformed model data: " + variableNames[i] + "\nBeanShell error message: '" + e.getMessage() + "'" ); 
		}
		return retur;
	}
	
	/**
	 * Walks the data space, and return the value of the data, if found.
	 * @param dataName
	 * @return
	 * @throws InvalidDataException is thrown if the data is not found in the data space
	 */
	public String getDataValue( String dataName ) throws InvalidDataException
	{
		Hashtable<String, Object> dataTable = getCurrentData();
		if( dataTable.containsKey( dataName ) ) {
			if( dataTable.get(dataName) instanceof Object[] ) {
				return Arrays.deepToString( (Object[]) dataTable.get(dataName) );				
			}
			else {
				return dataTable.get(dataName).toString();
			}
		}
		throw new InvalidDataException( "The data name: '" + dataName + "', does not exist in the namespace." );
	}

	/**
	 * Executes an action, and returns any outcome as a string.
	 * @param action
	 * @return
	 * @throws InvalidDataException is thrown if the data is not found in the data space
	 */
	public String execAction( String action ) throws InvalidDataException
	{
		Object res = null;
		try {
			res = interpreter.eval(action);
		} catch (EvalError e) {
			throw new InvalidDataException( "The action: '" + action + "', does not evaluate correctly. Detail: " + e.getMessage() );
		}
		return res.toString();
	}

	public String getCurrentDataString() 
	{
		String retur = "";
		
		Hashtable<String, Object> dataTable = getCurrentData();
		Enumeration<String> e = dataTable.keys();
		while(e.hasMoreElements())
		{
			String key = (String) e.nextElement();
			String data = "";
			if( dataTable.get(key) instanceof Object[] ) {
				data = Arrays.deepToString( (Object[]) dataTable.get(key) );				
			}
			else {
				data = dataTable.get(key).toString();
			}
			retur +=  key + "=" + data + ";";
		}
		return retur;
	}
	
	public boolean walkEdge(Edge edge)
	{
		boolean hasWalkedEdge = super.walkEdge(edge);
		if ( hasWalkedEdge ) {
			if ( hasAction( edge ) ) {
				PrintStream ps = System.out;
				System.setOut(Void);
				
				logger.debug( "The classpath is: " + Util.printClassPath() );
				
				try {
					interpreter.eval( getAction( edge ) );
				} catch ( EvalError e ) {
					logger.error( "Problem when running: '" + getAction( edge ) + "' in BeanShell" );
					logger.error( "EvalError: "  + e );
					logger.error( e.getCause() );
					throw new RuntimeException( "Malformed action sequence\n\t" + edge + "\n\tAction sequence: " + edge.getActionsKey() + "\n\tBeanShell error message: '" + e.getMessage() + "'\nDetails: " + e.getCause() );
				}
				finally {
					System.setOut(ps);
				}
			}
		}
		return hasWalkedEdge;
	}

	private String getAction(Edge edge) {
		return (edge==null?"":edge.getActionsKey());
	}

	private boolean hasAction(Edge edge) {
		return (edge==null?false:!edge.getActionsKey().isEmpty());
	}
	protected void track()
	{
		super.track();
		namespaceStack.push(new CannedNameSpace(interpreter.getNameSpace()));
	}
	
	protected void popState()
	{
		super.popState();
		interpreter.setNameSpace(((CannedNameSpace)namespaceStack.pop()).unpack());
	}

	private class CannedNameSpace
	{
		byte [] store;
		
		public CannedNameSpace(NameSpace objNameSpace) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(objNameSpace);
			} catch (IOException e) {
				throw new RuntimeException("Unable to store backtrack information due to a IOException.",e);
			}
			store = baos.toByteArray();
		}
		
		public NameSpace unpack()
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(store);
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(bais);
				return (NameSpace) ois.readObject();
			} catch (IOException e) {
				throw new RuntimeException("Unable to restore backtrack information due to a IOException.",e);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Unable to restore backtrack information as the NameSpace Class could not be found.",e);
			}
		}
	}
	
	public void setCalculatingPath(boolean calculatingPath) {
		super.setCalculatingPath(calculatingPath);
		if(calculatingPath && this.oldPrintStream != System.out)
		{
			this.oldPrintStream = System.out;
			System.setOut(new VoidPrintStream());
		} else if(!calculatingPath && this.oldPrintStream != System.out)
		{
			System.setOut(this.oldPrintStream);
		}
	}
	
	private class VoidPrintStream extends PrintStream
	{
		public VoidPrintStream()
		{
			super(System.out);
		}

		public void write(byte[] buf, int off, int len) {}
	}
}
