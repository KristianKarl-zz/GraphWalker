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
import org.tigris.mbt.conditions.CombinationalCondition;
import org.tigris.mbt.conditions.EdgeCoverage;
import org.tigris.mbt.conditions.ReachedEdge;
import org.tigris.mbt.conditions.ReachedRequirement;
import org.tigris.mbt.conditions.ReachedState;
import org.tigris.mbt.conditions.RequirementCoverage;
import org.tigris.mbt.conditions.StateCoverage;
import org.tigris.mbt.conditions.StopCondition;
import org.tigris.mbt.conditions.TestCaseLength;
import org.tigris.mbt.conditions.TimeDuration;
import org.tigris.mbt.generators.CodeGenerator;
import org.tigris.mbt.generators.ListGenerator;
import org.tigris.mbt.generators.PathGenerator;
import org.tigris.mbt.generators.RandomPathGenerator;
import org.tigris.mbt.generators.RequirementsGenerator;
import org.tigris.mbt.generators.ShortestPathGenerator;
import org.tigris.mbt.io.AbstractModelHandler;
import org.tigris.mbt.io.GraphML;

import edu.uci.ics.jung.graph.impl.SparseGraph;

/**
 * The object handles the test case generation, both online and offline.
 */
public class ModelBasedTesting
{
	static Logger logger = Util.setupLogger( ModelBasedTesting.class );

	private AbstractModelHandler modelHandler;
	private FiniteStateMachine machine;
	private StopCondition condition;
	private PathGenerator generator;
	private String template;

	public void addCondition(int conditionType, String conditionValue) 
	{
		StopCondition condition = null;
		switch (conditionType) {
		case Keywords.CONDITION_EDGE_COVERAGE:
			condition = new EdgeCoverage(getMachine(), Double.parseDouble(conditionValue)/100);
			break;
		case Keywords.CONDITION_REACHED_EDGE:
			condition = new ReachedEdge(getMachine(), conditionValue);
			break;
		case Keywords.CONDITION_REACHED_STATE:
			condition = new ReachedState(getMachine(), conditionValue);
			break;
		case Keywords.CONDITION_STATE_COVERAGE:
			condition = new StateCoverage(getMachine(), Double.parseDouble(conditionValue)/100);
			break;
		case Keywords.CONDITION_TEST_DURATION:
			condition = new TimeDuration(Long.parseLong(conditionValue));
			break;
		case Keywords.CONDITION_TEST_LENGTH:
			condition = new TestCaseLength(getMachine(), Integer.parseInt(conditionValue));
			break;
		case Keywords.CONDITION_REQUIREMENT_COVERAGE:
			condition = new RequirementCoverage(getMachine(), Double.parseDouble(conditionValue)/100);
			break;
		case Keywords.CONDITION_REACHED_REQUIREMENT:
			condition = new ReachedRequirement(getMachine(), conditionValue);
			break;
		}
		
		Util.AbortIf(condition == null , "Unsupported stop condition selected: "+ conditionType);
		
		if(	this.condition == null )
		{
			this.condition = condition;
		}
		else
		{
			if( !(this.condition instanceof CombinationalCondition) )
			{
				StopCondition old= this.condition;
				this.condition = new CombinationalCondition();
				((CombinationalCondition)this.condition).add(old);
			}
			((CombinationalCondition)this.condition).add(condition);
		}
	}

	private StopCondition getCondition()
	{
		return this.condition;
	}

	private FiniteStateMachine getMachine() 
	{
		if ( this.machine == null )
		{
			setMachine( new FiniteStateMachine( getGraph() ) );
		}
		return this.machine;
	}

	private void setMachine(FiniteStateMachine machine) 
	{
		this.machine = machine;
	}

	/**
	 * Return the instance of the graph
	 */
	public SparseGraph getGraph() {
		return this.modelHandler.getModel();
	}

	public void enableExtended(boolean extended) 
	{
		if( extended )
		{
			setMachine( new ExtendedFiniteStateMachine( getGraph() ) );
		}
		else
		{
			setMachine( new FiniteStateMachine( getGraph() ) );
		}
	}

	public void setGenerator( int generatorType )
	{
		switch (generatorType) 
		{
			case Keywords.GENERATOR_RANDOM:
				this.generator = new RandomPathGenerator(getMachine(), getCondition() );
				break;

			case Keywords.GENERATOR_SHORTEST:
				this.generator = new ShortestPathGenerator(getMachine(), getCondition());
				break;
			
			case Keywords.GENERATOR_STUB:
				this.generator = new CodeGenerator(getMachine(), this.template);
				break;
				
			case Keywords.GENERATOR_LIST:
				this.generator = new ListGenerator( getMachine() );
				break;
				
			case Keywords.GENERATOR_REQUIREMENTS:
				this.generator = new RequirementsGenerator( getMachine() );
				break;
		}
		Util.AbortIf( this.generator == null, "Not implemented yet!" );
	}
	
	private PathGenerator getGenerator()
	{
		return this.generator;
	}

	public boolean hasNextStep() {
		Util.AbortIf(getGenerator() == null, "No generator has been defined!");
		return getGenerator().hasNext();
	}

	public String[] getNextStep() {
		Util.AbortIf(getGenerator() == null, "No generator has been defined!");
		try
		{
			return getGenerator().getNext();
		}
		catch(RuntimeException e)
		{
			logger.fatal(e.toString());
			throw new RuntimeException( "ERROR: " + e.getMessage() );
		}
	}
	
	public String getCurrentState()
	{
		return getMachine().getCurrentStateName();
	}

	public void backtrack() {
		getMachine().backtrack();
	}

	public void readGraph( String graphmlFileName )
	{
		if(this.modelHandler == null)
		{
			this.modelHandler = new GraphML(); 
		}
		this.modelHandler.load( graphmlFileName );
	}

	public void writeModel(PrintStream ps) {
		this.modelHandler.save(ps);
	}

	public void enableBacktrack(boolean backtracking) 
	{
		if ( getMachine() != null )
		{					
			getMachine().setBacktrack(backtracking);
		}
		logger.warn( "Machine not initialialized" );
	}

	public String getStatisticsString()
	{
		if ( getMachine() != null )
		{			
			return getMachine().getStatisticsString();
		}
		logger.warn( "Machine not initialialized" );
		return "";
	}
	
	public String getStatisticsCompact()
	{
		if ( getMachine() != null )
		{			
			return getMachine().getStatisticsStringCompact();
		}
		logger.warn( "Machine not initialialized" );
		return "";
	}

	public String getStatisticsVerbose()
	{
		if ( getMachine() != null )
		{
			return getMachine().getStatisticsVerbose();
		}
		logger.warn( "Machine not initialialized" );
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
	}
	
	public void execute(String strClassName) 
	{
		if( strClassName == null || strClassName.trim().equals(""))
			throw new RuntimeException("Needed execution class name is missing as parameter.");
		Class clsClass = null;
		try {
			clsClass = Class.forName(strClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Cannot locate execution class.", e);
		}
		execute(clsClass, null);
	}

	public void execute(Class clsClass) 
	{
		if( clsClass == null )
			throw new RuntimeException("Needed execution class is missing as parameter.");
		execute(clsClass, null);
	}

	public void execute(Object objInstance) 
	{
		if( objInstance == null )
			throw new RuntimeException("Needed execution instance is missing as parameter.");
		execute(null, objInstance);
	}

	public void execute(Class clsClass, Object objInstance)
	{
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
		
		while( hasNextStep() )
		{
			String[] stepPair = getNextStep();
			
			try {
				executeMethod(clsClass, objInstance, stepPair[ 0 ] );
				executeMethod(clsClass, objInstance, stepPair[ 1 ] );
				System.out.println((int)(100*generator.getConditionFulfillment()) +"% done");
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
}
