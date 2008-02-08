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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.tigris.mbt.conditions.AlternativeCondition;
import org.tigris.mbt.conditions.CombinationalCondition;
import org.tigris.mbt.conditions.StopCondition;
import org.tigris.mbt.exceptions.InvalidDataException;
import org.tigris.mbt.generators.CodeGenerator;
import org.tigris.mbt.generators.PathGenerator;
import org.tigris.mbt.generators.RandomPathGenerator;
import org.tigris.mbt.io.AbstractModelHandler;
import org.tigris.mbt.io.GraphML;

import edu.uci.ics.jung.graph.impl.SparseGraph;

/**
 * The object handles the test case generation, both online and offline.
 *
 * @author krikar
 *
 */
public class ModelBasedTesting
{
	static Logger logger = Util.setupLogger( ModelBasedTesting.class );

	private AbstractModelHandler modelHandler;
	private FiniteStateMachine machine;
	private StopCondition condition;
	private PathGenerator generator;
	private String template;
	private boolean backtracking = false;
	private boolean runRandomGeneratorOnce = false;

	private String startupScript;

	public void addAlternativeCondition(int conditionType, String conditionValue)
	{
		StopCondition condition = Util.getCondition(conditionType, conditionValue);
		
		if(	getCondition() == null )
		{
			setCondition(new AlternativeCondition());
			((AlternativeCondition)getCondition()).add(condition);
		}
		else 
		{
			if( !(getCondition() instanceof AlternativeCondition) )
			{
				StopCondition old = getCondition();
				setCondition(new AlternativeCondition());
				((AlternativeCondition)getCondition()).add(old);
			}
			((AlternativeCondition)getCondition()).add(condition);
		}
	}
	
	public void addCondition(int conditionType, String conditionValue) 
	{
		StopCondition condition = Util.getCondition(conditionType, conditionValue);
		
		if(	getCondition() == null )
		{
			setCondition(condition);
		}
		else 
		{
			if( !(getCondition() instanceof CombinationalCondition) )
			{
				StopCondition old = getCondition();
				setCondition(new CombinationalCondition());
				((CombinationalCondition)getCondition()).add(old);
			}
			((CombinationalCondition)getCondition()).add(condition);
		}
	}

	public void setCondition(StopCondition condition) {
		this.condition = condition;
		if(getGenerator() != null)
			getGenerator().setStopCondition(getCondition());
		if(this.machine != null)
			getCondition().setMachine(getMachine());
	}
	
	private StopCondition getCondition()
	{
		return this.condition;
	}

	private FiniteStateMachine getMachine() 
	{
		if ( this.machine == null )
		{
			setMachine( new FiniteStateMachine() );
		}
		return this.machine;
	}

	private void setMachine(FiniteStateMachine machine) 
	{
		this.machine = machine;
		if(this.modelHandler != null)
			getMachine().setModel(getGraph());
		if(getCondition() != null)
			getCondition().setMachine(machine);
		if(getGenerator() != null)
			getGenerator().setMachine(machine);
		getMachine().setBacktrack(this.backtracking);
	}

	/**
	 * Return the instance of the graph
	 */
	public SparseGraph getGraph() {
		return this.modelHandler.getModel();
	}

	public void setGraph(SparseGraph graph) {
		if(	this.modelHandler == null )
		{
			this.modelHandler = new GraphML();
		}
		this.modelHandler.setModel(graph);
		if(this.machine != null)
			getMachine().setModel(graph);
	}

	
	/**
	 * Returns the value of an data object within the data space of the model.
	 * @param data The name of the data object, which value is to be retrieved.
	 * @return The value of the data object. The value is always returned a s string. It is
	 * the calling parties task to parse the string and convert it to correct type.
	 * @throws InvalidDataException If the retrieval of the data fails, the InvalidDataException is thrown. For example
	 * if a FiniteStateMachine is used, which has no data space, the exception is thrown.
	 */
	public String getDataValue( String data ) throws InvalidDataException
	{
		Util.AbortIf(this.machine == null, "No machine has been defined!");
		if ( this.machine instanceof ExtendedFiniteStateMachine )
		{
			return ((ExtendedFiniteStateMachine)this.machine).getDataValue( data );
		}
		throw new InvalidDataException( "Data can only be fetched from a ExtendedFiniteStateMachine. Please enable EFSM." );
	}

	public void enableExtended(boolean extended) 
	{
		setMachine( (extended?new ExtendedFiniteStateMachine():new FiniteStateMachine()));
	}

	public void setGenerator(PathGenerator generator)
	{
		this.generator = generator;
	}
	
	public void setGenerator( int generatorType )
	{
		
		setGenerator(Util.getGenerator(generatorType));

		if(this.machine != null)
			getGenerator().setMachine(getMachine());
		if(this.template != null && this.generator instanceof CodeGenerator)
			((CodeGenerator)generator).setTemplate(this.template);
		if(getCondition() != null)
			getGenerator().setStopCondition(getCondition());
	}
	
	private PathGenerator getGenerator()
	{
		return this.generator;
	}

	public boolean hasNextStep() {
		if(this.machine == null) getMachine();
		Util.AbortIf(getGenerator() == null, "No generator has been defined!");
		return getGenerator().hasNext();
	}

	public String[] getNextStep()
	{		
		if(this.machine == null) getMachine();
		Util.AbortIf(getGenerator() == null, "No generator has been defined!");
		
		PathGenerator backupGenerator = null;
		if ( runRandomGeneratorOnce )
		{
			if ( !(getGenerator() instanceof RandomPathGenerator) ) 
			{
				backupGenerator = getGenerator();				
			}
			setGenerator(Keywords.GENERATOR_RANDOM);
		}

		try
		{
			return getGenerator().getNext();
		}
		catch(RuntimeException e)
		{
			logger.fatal(e.toString());
			throw new RuntimeException( "ERROR: " + e.getMessage() );
		}
		finally
		{
			if ( runRandomGeneratorOnce )
			{
				runRandomGeneratorOnce = false;
				setGenerator(backupGenerator);
			}
		}
	}
	
	public String getCurrentState()
	{
		if(this.machine != null)
			return getMachine().getCurrentStateName();
		logger.warn( "Trying to retrieve current state without specifying machine" );
		return "";
	}

	public void backtrack( boolean popEdge ) {
		if(this.machine != null)
			getMachine().backtrack( popEdge );
		getGenerator().reset();
		runRandomGeneratorOnce = true;
	}

	public void readGraph( String graphmlFileName )
	{
		if(this.modelHandler == null)
		{
			this.modelHandler = new GraphML(); 
		}
		this.modelHandler.load( graphmlFileName );
		
		if(this.machine != null)
			getMachine().setModel(getGraph());
	}

	public void writeModel(PrintStream ps) {
		this.modelHandler.save(ps);
	}

	public boolean hasCurrentEdgeBackTracking() 
	{
		if(this.machine != null)
		{					
			return getMachine().getLastEdge().containsUserDatumKey( Keywords.BACKTRACK );
		}
		return false;
	}

	public boolean hasCurrentVertexBackTracking() 
	{
		if(this.machine != null)
		{					
			return  getMachine().getLastEdge().containsUserDatumKey( Keywords.BACKTRACK );
		}
		return false;
	}

	public void enableBacktrack(boolean backtracking) 
	{
		this.backtracking = backtracking;
		if(this.machine != null)
		{					
			getMachine().setBacktrack(backtracking);
		}
	}

	public String getStatisticsString()
	{
		if(this.machine != null)
		{			
			return getMachine().getStatisticsString();
		}
		logger.warn( "Trying to retrieve statistics without specifying machine" );
		return "";
	}
	
	public String getStatisticsCompact()
	{
		if(this.machine != null)
		{			
			return getMachine().getStatisticsStringCompact();
		}
		logger.warn( "Trying to retrieve compact statistics without specifying machine" );
		return "";
	}

	public String getStatisticsVerbose()
	{
		if(this.machine != null)
		{
			return getMachine().getStatisticsVerbose();
		}
		logger.warn( "Trying to retrieve verbose statistics without specifying machine" );
		return "";
	}

	public void setTemplate( String templateFile )
	{
		this.template = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(templateFile));
		
			for(String tempLine = in.readLine(); tempLine != null; tempLine = in.readLine())
			{
				this.template += tempLine + "\n";			
			}
		} catch (IOException e) {
			throw new RuntimeException("Template file read problem: " + e.getMessage());
		}
		
		if(getGenerator() != null && getGenerator() instanceof CodeGenerator)
			((CodeGenerator)getGenerator()).setTemplate(this.template);
	}
	
	public void executePath(String strClassName) 
	{
		if( strClassName == null || strClassName.trim().equals(""))
			throw new RuntimeException("Needed execution class name is missing as parameter.");
		Class clsClass = null;
		try {
			clsClass = Class.forName(strClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Cannot locate execution class.", e);
		}
		executePath(clsClass, null);
	}

	public void executePath(Class clsClass) 
	{
		if( clsClass == null )
			throw new RuntimeException("Needed execution class is missing as parameter.");
		executePath(clsClass, null);
	}

	public void executePath(Object objInstance) 
	{
		if( objInstance == null )
			throw new RuntimeException("Needed execution instance is missing as parameter.");
		executePath(null, objInstance);
	}

	public void executePath(Class clsClass, Object objInstance)
	{
		if(this.machine == null) getMachine(); 
		if( clsClass == null && objInstance == null )
			throw new RuntimeException("Execution instance or class is missing as parameters.");
		if( clsClass == null )
			clsClass = objInstance.getClass();
		if( objInstance == null )
			try {
				objInstance = clsClass.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException("Cannot create execution instance.", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Cannot access execution instance.", e);
			} 
		int step = 0;
		while( hasNextStep() )
		{
			String[] stepPair = getNextStep();
			
			try {
				logger.info("Step: "+ (++step) +" Navigate: "+stepPair[ 0 ]);
				executeMethod(clsClass, objInstance, stepPair[ 0 ] );
				executeMethod(clsClass, objInstance, stepPair[ 1 ] );
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Illegal argument used.", e);
			} catch (SecurityException e) {
				throw new RuntimeException("Security failure occured.", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Illegal access was stoped.", e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException("Cannot invoke target.", e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("Cannot find specified method.", e);
			}
		}
	}
	
	private void executeMethod(Class clsClass, Object objInstance, String strMethod) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException 
	{
		if(strMethod.contains("/") ) 
			strMethod = strMethod.substring(0, strMethod.indexOf("/"));
		
		if(strMethod.contains("[") ) 
			strMethod = strMethod.substring(0, strMethod.indexOf("["));
		
		if(strMethod.contains(" "))
		{
			String s1 = strMethod.substring(0, strMethod.indexOf(" "));
			String s2 = strMethod.substring(strMethod.indexOf(" ")+1);
			Class[] paramTypes = { String.class };
			Object[] paramValues = { s2 };
			clsClass.getMethod( s1, paramTypes ).invoke( objInstance, paramValues );
		}
		else
		{
			clsClass.getMethod( strMethod, null ).invoke( objInstance, null  );
		}
	}

	public void writePath() {
		writePath(System.out);
	}

	public void writePath(PrintStream out) {
		if(this.machine == null) getMachine(); 
		while( hasNextStep() )
		{
			String[] stepPair = getNextStep();
			
			out.println( stepPair[ 0 ] );
			out.println( stepPair[ 1 ] );
		}
	}
	
	/**
	 * @param childText
	 */
	public void setStartupScript(String script) {
		this.startupScript = script;
		
	}
	
	/**
	 * @return the startupScript
	 */
	public String getStartupScript() {
		return this.startupScript;
	}

}
