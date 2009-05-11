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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.tigris.mbt.GUI.App;
import org.tigris.mbt.conditions.AlternativeCondition;
import org.tigris.mbt.conditions.CombinationalCondition;
import org.tigris.mbt.conditions.ReachedRequirement;
import org.tigris.mbt.conditions.StopCondition;
import org.tigris.mbt.events.MbtEvent;
import org.tigris.mbt.exceptions.InvalidDataException;
import org.tigris.mbt.generators.CodeGenerator;
import org.tigris.mbt.generators.PathGenerator;
import org.tigris.mbt.graph.AbstractElement;
import org.tigris.mbt.graph.Edge;
import org.tigris.mbt.graph.Graph;
import org.tigris.mbt.graph.Vertex;
import org.tigris.mbt.io.AbstractModelHandler;
import org.tigris.mbt.io.GraphML;
import org.tigris.mbt.machines.ExtendedFiniteStateMachine;
import org.tigris.mbt.machines.FiniteStateMachine;
import org.tigris.mbt.statistics.EdgeCoverageStatistics;
import org.tigris.mbt.statistics.EdgeSequenceCoverageStatistics;
import org.tigris.mbt.statistics.RequirementCoverageStatistics;
import org.tigris.mbt.statistics.StateCoverageStatistics;

import bsh.EvalError;


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
	private String[] template;
	private boolean backtracking = false;
	private boolean runRandomGeneratorOnce = false;
	private boolean dryRun = false;
	private boolean useGUI = false;	
	private MbtEvent notifyApp = null;
	private String javaExecutorClass = null;
	

	// Private constructor prevents instantiation from other classes
	private ModelBasedTesting() {}

	/**
	 * ModelBasedTestingHolder is loaded on the first execution of ModelBasedTesting.getInstance() 
	 * or the first access to ModelBasedTestingHolder.INSTANCE, not before.
	 */
	@SuppressWarnings("synthetic-access")
	private static class ModelBasedTestingHolder { 
		private static final ModelBasedTesting INSTANCE = new ModelBasedTesting();
	}

	@SuppressWarnings("synthetic-access")
	public static ModelBasedTesting getInstance() {
		return ModelBasedTestingHolder.INSTANCE;
	}

	
	/**
	 * Clears everything.
	 * Removes any defined machine, generators, stop conditions etc.
	 */
	public void reset() {
		modelHandler = null;
		machine = null;
		condition = null;
		generator = null;
		template = null;
		backtracking = false;
		runRandomGeneratorOnce = false;
		dryRun = false;
		javaExecutorClass = null;
	}
	
	public boolean isUseGUI() {
		return useGUI;
	}

	public void setUseGUI() {
		useGUI = true;
		notifyApp = App.getInstance(); 
	}

	public boolean isDryRun() {
		return dryRun;
	}

	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	private String startupScript = "";

	private StatisticsManager statisticsManager;
	
	private void setupStatisticsManager()
	{
		if(this.statisticsManager == null)
			this.statisticsManager = new StatisticsManager();
		this.statisticsManager.addStatisicsCounter("State Coverage", new StateCoverageStatistics( getGraph() ));
		this.statisticsManager.addStatisicsCounter("Edge Coverage", new EdgeCoverageStatistics( getGraph() ));
		this.statisticsManager.addStatisicsCounter("2-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics( getGraph(), 2 ));
		this.statisticsManager.addStatisicsCounter("3-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics( getGraph(), 3 ));
		this.statisticsManager.addStatisicsCounter("Requirements Coverage", new RequirementCoverageStatistics( getGraph() ));
		this.statisticsManager.addProgress(getMachine().getCurrentState());
	}
	
	/**
	 * @return the statisticsManager
	 */
	public StatisticsManager getStatisticsManager() {
		if(this.statisticsManager == null)
		{
			this.statisticsManager = new StatisticsManager();
			if(this.machine != null)
				setupStatisticsManager();
		}
		return this.statisticsManager;
	}
	
	public void addAlternativeCondition(int conditionType, String conditionValue)
	{
		StopCondition condition = Util.getCondition(conditionType, conditionValue);
		
		// If requirement stop condition, check if requirement exists in model
		if ( condition instanceof ReachedRequirement )			
		{
			Collection<String> reqs = ((ReachedRequirement)condition).getRequirements();
			for ( Iterator<String> iterator = reqs.iterator(); iterator.hasNext(); ) {
				String req = iterator.next();
				Util.AbortIf( getMachine().getAllRequirements().containsKey( req ) == false, 
						"Requirement: '" + req + "' do not exist in the model" );				
			}
		}
		
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

	public FiniteStateMachine getMachine() 
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
		getMachine().setBacktrackEnabled(this.backtracking);
	}

	/**
	 * Return the instance of the graph
	 */
	public Graph getGraph() {
		if ( this.modelHandler == null )
			return null;
		return this.modelHandler.getModel();
	}

	public void setGraph(Graph graph) {
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

	
	/**
	 * Executes an action, and returns any outcome as a string.
	 * @param data The name of the data object and the method, which value is to be retrieved.
	 * @return The value of the data object's method. The value is always returned a s string. It is
	 * the calling parties task to parse the string and convert it to correct type.
	 * @throws InvalidDataException If the retrieval of the data fails, the InvalidDataException is thrown. For example
	 * if a FiniteStateMachine is used, which has no data space, the exception is thrown.
	 */
	public String execAction( String action ) throws InvalidDataException
	{
		Util.AbortIf(this.machine == null, "No machine has been defined!");
		if ( this.machine instanceof ExtendedFiniteStateMachine )
		{
			return ((ExtendedFiniteStateMachine)this.machine).execAction( action );
		}
		throw new InvalidDataException( "Data can only be fetched from a ExtendedFiniteStateMachine. Please enable EFSM." );
	}

	
	/**
	 * Tells mbt that a requirement (if any), has passed or failed. MBT will look at the most recent edge or
	 * vertex and check if they have requirement. If none is found, then no action is taken. If req. is found
	 * mbt will log the information using the logger.   
	 * @param pass Tells mbt if the requirement has pass (true), or failed (false). 
	 */
	public void passRequirement( boolean pass )
	{
		Util.AbortIf(this.machine == null, "No machine has been defined!");
		Vertex v = getMachine().getCurrentState();
		if ( !v.getReqTagKey().isEmpty() )
		{
			String str = "REQUIREMENT: '" + v.getReqTagKey() + "' has ";
			if ( pass )
				str += "PASSED, at " + v;
			else
				str += "FAILED, at " + v;
			logger.info( str );
		}
	}

	public void enableExtended(boolean extended) 
	{
		if(extended)
		{
			setMachine(new ExtendedFiniteStateMachine());
			if(!getStartupScript().equals(""))
			try {
				((ExtendedFiniteStateMachine)getMachine()).eval(getStartupScript());
			} catch (EvalError e) {
				throw new RuntimeException("Execution of startup script generated an error.",e);
			}
		} else {
			setMachine( new FiniteStateMachine() );
		}
	}

	public void setGenerator(PathGenerator generator)
	{
		this.generator = generator;

		if(this.machine != null)
			getGenerator().setMachine(getMachine());
		if(this.template != null && this.generator instanceof CodeGenerator)
			((CodeGenerator)generator).setTemplate(this.template);
		if(getCondition() != null)
			getGenerator().setStopCondition(getCondition());
	}
	
	public void setGenerator( int generatorType )
	{
		setGenerator(Util.getGenerator(generatorType));
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
		if ( isUseGUI() ) {
			while ( App.getInstance().status.isPaused() ) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					break;
				}
			}
		}

		if ( this.machine == null ){
			getMachine();
		}
		getStatisticsManager();
		Util.AbortIf( getGenerator() == null, "No generator has been defined!" );
		
		PathGenerator backupGenerator = null;
		if ( runRandomGeneratorOnce )
		{
			backupGenerator = getGenerator();				
			setGenerator(Keywords.GENERATOR_RANDOM);
		}

		try
		{
			return getGenerator().getNext();
		}
		catch ( RuntimeException e )
		{
			logger.fatal(e.toString());
			throw new RuntimeException( "ERROR: "+e.getMessage(), e);
		}
		finally
		{
			if ( runRandomGeneratorOnce )
			{
				runRandomGeneratorOnce = false;
				setGenerator(backupGenerator);
			}
			if ( notifyApp != null )
				notifyApp.getNextEvent();
			if ( isUseGUI() ) {
				if ( App.getInstance().status.isNext() ) {
					App.getInstance().status.setPaused();
				}
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

	public void backtrack() {
		if(this.machine != null)
			getMachine().backtrack();
		getGenerator().reset();
	}

	public void readGraph( String graphmlFileName )
	{
		if(this.modelHandler == null) {
			this.modelHandler = new GraphML(); 
		}
		this.modelHandler.load( graphmlFileName );
		
		if ( this.machine != null ) {
			getMachine().setModel( getGraph() );
		}
		
		if ( isUseGUI() ) {
			App.getInstance().updateLayout();
		}
	}

	public void writeModel( PrintStream ps ) {
		this.modelHandler.save( ps );
	}

	public boolean hasCurrentEdgeBackTracking() 
	{
		if(this.machine != null && getMachine().getLastEdge() != null)
		{					
			return getMachine().getLastEdge().isBacktrackKey();
		}
		return false;
	}

	public boolean hasCurrentVertexBackTracking() 
	{
		if(this.machine != null && getMachine().getLastEdge() != null)
		{					
			return  getMachine().getLastEdge().isBacktrackKey();
		}
		return false;
	}

	public void enableBacktrack(boolean backtracking) 
	{
		this.backtracking = backtracking;
		if(this.machine != null)
		{					
			getMachine().setBacktrackEnabled(backtracking);
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
	
	public void setTemplate( String[] template )
	{
		this.template = template;
		
		if(getGenerator() != null && getGenerator() instanceof CodeGenerator)
			((CodeGenerator)getGenerator()).setTemplate(this.template);
		
	}
	
	public void setTemplate( String templateFile )
	{
		String template = Util.readFile( templateFile );
		String header = "", body = "", footer = "";
		Pattern p = Pattern.compile( "HEADER<\\{\\{([.\\s\\S]+)\\}\\}>HEADER([.\\s\\S]+)FOOTER<\\{\\{([.\\s\\S]+)\\}\\}>FOOTER" );
		Matcher m = p.matcher( template );
		if ( m.find() ) {
			header = m.group( 1 );
			body = m.group( 2 );
			footer = m.group( 3 );
			setTemplate( new String[]{header, body, footer} );
		}
		else {
			setTemplate( new String[]{"", template, ""} );			
		}
	}
	
	public void interractivePath()
	{
		interractivePath(System.in);
	}
	
	public void interractivePath(InputStream in)
	{
		Vector<String> stepPair = new Vector<String>();
		String req = "";
		
		for( char input = '0'; true; input = Util.getInput() )
		{
			logger.debug("Recieved: '"+ input+"'");

			switch (input) {
			case '2':
				return;
			case '1':
				backtrack();
				runRandomGeneratorOnce = true;
				stepPair.clear();
			case '0':

				if( !hasNextStep() && ( stepPair.size() == 0 ) ) {
					return;
				}
				if( stepPair.size() == 0 )
				{
					stepPair = new Vector<String>( Arrays.asList( getNextStep() ) );
					req = getRequirement( getMachine().getLastEdge() );
				}
				else
				{
					req = getRequirement( getMachine().getCurrentState() );
				}
				
				if ( req.length() > 0 )
				{
					req = "/" + req;
				}
				
				System.out.print( (String) stepPair.remove(0) + req );
				
				String addInfo = "";
				if( ( stepPair.size() == 1 && hasCurrentEdgeBackTracking() ) || 
					( stepPair.size() == 0 && hasCurrentVertexBackTracking() ) )
				{
					System.out.println( " BACKTRACK" );
					addInfo = " BACKTRACK";
				}
				else
					System.out.println();
				
				if ( stepPair.size() == 1 )
				{
					logExecution( getMachine().getLastEdge(), addInfo );
					getStatisticsManager().addProgress( getMachine().getLastEdge() );
				}
				else
				{
					logExecution( getMachine().getCurrentState(), addInfo );
					getStatisticsManager().addProgress( getMachine().getCurrentState() );
				}

				break;

			default:
				throw new RuntimeException("Unsupported input recieved.");
			} 
		}
	}
	
	public String getRequirement( AbstractElement element )
	{
		String req = "";
		if( element instanceof Edge )
		{
			if ( !getMachine().getLastEdge().getReqTagKey().isEmpty() )
			{
				req = "REQUIREMENT: '" + getMachine().getLastEdge().getReqTagKey() + "'";
			}
			return req;
		}
		else if( element instanceof Vertex )
		{
			if ( !getMachine().getCurrentState().getReqTagKey().isEmpty() )
			{
				req = "REQUIREMENT: '" + getMachine().getCurrentState().getReqTagKey() + "'";
			}
			return req;
		}
		return "";
	}

	public void logExecution( AbstractElement element, String additionalInfo ) 
	{
		String req = " " + getRequirement( element );
		if( element instanceof Edge )
		{
			logger.info( "Edge: " + getMachine().getLastEdge() + req + additionalInfo );
			return;
		}
		else if( element instanceof Vertex )
		{
			logger.info( "Vertex: " + getMachine().getCurrentState() + req + 
					    ( getMachine().hasInternalVariables() ? " DATA: " + getMachine().getCurrentDataString() : "" ) 
					    + additionalInfo );
			return;
		}
	}

	public void setJavaExecutorClass( String executorClass ) {
		javaExecutorClass = executorClass;
	}

	public String getJavaExecutorClass() {
		return javaExecutorClass;
	}

	public void executePath() {
		if ( getJavaExecutorClass() != null ) {
			executePath( getJavaExecutorClass() );
			return;
		}
		
		while( hasNextStep() )
		{
			String[] stepPair = getNextStep();
			
			if ( stepPair[0].trim()!= "" ) {
				logExecution( getMachine().getLastEdge(), "" );
				getStatisticsManager().addProgress(getMachine().getLastEdge());
			}
			if ( stepPair[1].trim()!="" ) {
				logExecution( getMachine().getCurrentState(), "" );
				getStatisticsManager().addProgress(getMachine().getCurrentState());
			}

		}

	}
	
	public void executePath( String strClassName ) 
	{
		if ( getJavaExecutorClass() == null ) {
			setJavaExecutorClass( strClassName );
		}
		
		if ( isDryRun() )
		{
			logger.debug( "Executing a dry run" );
			executePath( null, null);
		}
		if( strClassName == null || strClassName.trim().equals(""))
			throw new RuntimeException("Needed execution class name is missing as parameter.");
		Class<?> clsClass = null;
		try {
			clsClass = Class.forName(strClassName);
		} catch (ClassNotFoundException e) {
	        throw new RuntimeException("Cannot locate execution class: " + strClassName + ".\n Current class path is: " + System.getProperty("java.class.path"), e);
		}
		executePath( clsClass, null );
	}

	public void executePath( Class<?> clsClass ) 
	{
		if( clsClass == null )
			throw new RuntimeException("Needed execution class is missing as parameter.");
		executePath( clsClass, null );
	}

	public void executePath( Object objInstance ) 
	{
		if( objInstance == null )
			throw new RuntimeException("Needed execution instance is missing as parameter.");
		executePath( null, objInstance );
	}

	public void executePath( Class<?> clsClass, Object objInstance )
	{
		if(this.machine == null) getMachine();
		
		if ( isDryRun() )
		{
			logger.debug( "Executing a dry run" );
			while( hasNextStep() )
			{
				String[] stepPair = getNextStep();
				
				logExecution( getMachine().getLastEdge(), "" );
				System.out.println( "Do edge: " + getMachine().getLastEdge() );
				System.out.println( "Data: " + stepPair[ 0 ] );
				try {
					System.in.read();
				} catch (IOException e) {}
				getStatisticsManager().addProgress(getMachine().getLastEdge());
				
				logExecution( getMachine().getCurrentState(), "" );
				System.out.println( "Do vertex: " + getMachine().getCurrentState() );
				System.out.println( "Data: " + stepPair[ 1 ] );
				try {
					System.in.read();
				} catch (IOException e) {}
				getStatisticsManager().addProgress(getMachine().getCurrentState());
			}	
			return;
		}
		
		if( clsClass == null && objInstance == null )
			throw new RuntimeException("Execution instance or class is missing as parameters.");
		if( clsClass == null )
			clsClass = objInstance.getClass();
		if( objInstance == null )
			try {
				objInstance = clsClass.getConstructor(new Class[]{ModelBasedTesting.class}).newInstance(new Object[]{this});
			} catch (SecurityException e) {
				throw new RuntimeException("Execution instance generated exception.", e);
			} catch (NoSuchMethodException e) {
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Execution instance generated exception.", e);
			} catch (InstantiationException e) {
				throw new RuntimeException("Cannot create execution instance.", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Cannot access execution instance.", e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException("Execution instance generated exception.", e);
			}
			try {
				if(objInstance == null)
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
				logExecution( getMachine().getLastEdge(), "" );
				executeMethod(clsClass, objInstance, stepPair[ 0 ], true );
				getStatisticsManager().addProgress(getMachine().getLastEdge());
				
				logExecution( getMachine().getCurrentState(), "" );
				executeMethod(clsClass, objInstance, stepPair[ 1 ], false );
				getStatisticsManager().addProgress(getMachine().getCurrentState());
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Illegal argument used.", e);
			} catch (SecurityException e) {
				throw new RuntimeException("Security failure occured.", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Illegal access was stoped.", e);
			}
		}
	}
	
	private void executeMethod(Class<?> clsClass, Object objInstance, String strMethod, boolean isEdge) throws IllegalArgumentException, SecurityException, IllegalAccessException 
	{
		if ( strMethod.contains( "/" ) ) { 
			strMethod = strMethod.substring(0, strMethod.indexOf("/"));
		}
		
		if ( strMethod.contains( "[" ) ) { 
			strMethod = strMethod.substring(0, strMethod.indexOf("["));
		}
		
		if ( strMethod.contains( " " ) ) {
			String s1 = strMethod.substring(0, strMethod.indexOf(" "));
			String s2 = strMethod.substring(strMethod.indexOf(" ")+1);
			Class<?>[] paramTypes = { String.class };
			Object[] paramValues = { s2 };
			
			try {
				clsClass.getMethod( s1, paramTypes ).invoke( objInstance, paramValues );
			} 
			catch (InvocationTargetException e)
			{
				if ( isEdge ) {
					logger.error("InvocationTargetException for: " +  getMachine().getLastEdge() );
				}
				else {
					logger.error("InvocationTargetException for: " + getMachine().getCurrentState() );
				}
				throw new RuntimeException("InvocationTargetException.", e);
			} 
			catch (NoSuchMethodException e) 
			{
				if ( isEdge ) {
					logger.error("NoSuchMethodException for: " +  getMachine().getLastEdge() );
				}
				else {
					logger.error("NoSuchMethodException for: " + getMachine().getCurrentState() );
				}
				throw new RuntimeException("NoSuchMethodException.", e);
			}
		}
		else
		{
			if ( isEdge && strMethod.isEmpty() ) {
				return;
			}
			try {
				Method m = clsClass.getMethod( strMethod, null );
				m.invoke( objInstance, null  );
			} 
			catch ( InvocationTargetException e ) {
				if ( isEdge ) {
					logger.error("InvocationTargetException for: " +  getMachine().getLastEdge() + " : " + e.getMessage() );
				}
				else {
					logger.error("InvocationTargetException for: " + getMachine().getCurrentState() + " : " + e.getMessage() );
				}
				throw new RuntimeException("InvocationTargetException.", e);
			} 
			catch ( NoSuchMethodException e ) {
				if ( isEdge ) {
					logger.error("NoSuchMethodException for: " +  getMachine().getLastEdge() );
				}
				else {
					logger.error("NoSuchMethodException for: " + getMachine().getCurrentState() );
				}
				throw new RuntimeException("NoSuchMethodException.", e);
			}
		}
	}

	public void writePath() {
		writePath( System.out );
	}

	public void writePath( PrintStream out ) {
		if ( this.machine == null ) {
			getMachine(); 
		}
		while( hasNextStep() )
		{
			String[] stepPair = getNextStep();
			
			if(stepPair[0].trim()!="")
			{
				out.println( stepPair[0] );
				logExecution( getMachine().getLastEdge(), "" );
				getStatisticsManager().addProgress(getMachine().getLastEdge());
			}
			if(stepPair[1].trim()!="")
			{
				out.println( stepPair[1] );
				logExecution( getMachine().getCurrentState(), "" );
				getStatisticsManager().addProgress(getMachine().getCurrentState());
			}

		}
	}
	
	/**
	 * @param childText
	 */
	public void setStartupScript(String script) {
		this.startupScript = script;
		if(this.machine != null && this.machine instanceof ExtendedFiniteStateMachine)
		{
			try {
				((ExtendedFiniteStateMachine)this.machine).eval(script);
			} catch (EvalError e) {
				throw new RuntimeException("Execution of startup script generated an error.",e);
			}
		}
	}
	
	/**
	 * @return the startupScript
	 */
	public String getStartupScript() {
		return this.startupScript;
	}

	public String toString() {
		return getGenerator().toString();
	}

	public void setWeighted(boolean b) {
		getMachine().setWeighted(b);
	}
}
