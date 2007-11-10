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

import org.apache.log4j.Logger;
import org.tigris.mbt.conditions.CombinationalCondition;
import org.tigris.mbt.conditions.EdgeCoverage;
import org.tigris.mbt.conditions.ReachedEdge;
import org.tigris.mbt.conditions.ReachedState;
import org.tigris.mbt.conditions.StateCoverage;
import org.tigris.mbt.conditions.StopCondition;
import org.tigris.mbt.conditions.TestCaseLength;
import org.tigris.mbt.conditions.TimeDuration;
import org.tigris.mbt.generators.CodeGenerator;
import org.tigris.mbt.generators.ListGenerator;
import org.tigris.mbt.generators.PathGenerator;
import org.tigris.mbt.generators.RandomPathGenerator;
import org.tigris.mbt.generators.ShortestPathGenerator;

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

	public void enableBacktrack(boolean backtracking) {
		getMachine().setBacktrack(backtracking);
	}

	public String getStatisticsString()
	{
		return getMachine().getStatisticsString();
	}
	
	public String getStatisticsCompact()
	{
		return getMachine().getStatisticsStringCompact();
	}

	public String getStatisticsVerbose()
	{
		return getMachine().getStatisticsVerbose();
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
}
