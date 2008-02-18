package org.tigris.mbt.conditions;

import java.util.ArrayList;

import org.tigris.mbt.ExtendedFiniteStateMachine;
import org.tigris.mbt.FiniteStateMachine;

import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

public class ReachedState extends StopCondition {

	private ArrayList allStates;
	private DirectedSparseVertex endState;
	private int[] proximity;
	private int maxDistance;
	private String stateName;
	private String subState;

	public boolean isFulfilled() {
		return getFulfilment() >= 0.99999;
	}

	public void setMachine(FiniteStateMachine machine) {
		super.setMachine(machine);
		this.endState = machine.findState(stateName);
		if(this.endState == null)
			throw new RuntimeException("State '"+stateName+"' not found in model");
		this.proximity = getFloydWarshall();
		this.maxDistance = max(this.proximity);
	}
	
	public ReachedState(String stateName) {
		
		String[] state = stateName.split("/", 2);
		this.stateName = state[0];
		this.subState = (state.length>1?state[1]:"");
	}

	public double getFulfilment() {
		int distance = proximity[allStates.indexOf(getMachine().getCurrentState())];
		if(getMachine() instanceof ExtendedFiniteStateMachine)
		{
			double substateFulfillment = 0;
			String currentState = getMachine().getCurrentStateName();
			String currentSubState = "";
			if(currentState.contains("/"))
			{
				currentSubState = currentState.split("/",2)[1];
			}
			//FIXME Fix this to reflect how equal the sub-states are in % /Tejle
			if(currentSubState.equals(this.subState))
				substateFulfillment = 1;
			
			return (substateFulfillment + ((double)1)-( (double)distance / (double)maxDistance))/2;
		}
		return ((double)1)-( (double)distance / (double)maxDistance);
	}
	
	private int max(int[] t) {
	    int maximum = t[0];
	    for (int i=1; i<t.length; i++) {
	        if (t[i] > maximum) {
	            maximum = t[i];
	        }
	    }
	    return maximum;
	}
	
	private int[][] getFloydWarshallMatrix()
	{
		allStates = new ArrayList(getMachine().getAllStates());
		int n = allStates.size();
		int[][] retur = new int[n][n];
		for(int i = 0; i<n;i++)
			for(int j = 0; j<n;j++)
			{
				int x = 99999;
				if(i==j)
				{
					x = 0;
				}
				else if(((DirectedSparseVertex)allStates.get(j)).isPredecessorOf((DirectedSparseVertex)allStates.get(i)))
				{
					x = 1;
				}
				retur[i][j] = x;
			}
		return retur;
	}
	
	private int[] getFloydWarshall()
	{
		int path[][] = getFloydWarshallMatrix();
		int n = path.length;
		for( int k=0; k < n; k++)
		{
			for( int i=0; i < n; i++)
				for( int j=0; j < n; j++)
				{
					path[i][j] = Math.min ( path[i][j], path[i][k]+path[k][j] );					
				}
		}
		int startIndex = allStates.indexOf(endState);
		if(startIndex >= 0)
			return path[startIndex];
		throw new RuntimeException("Startstate no longer in Graph!");
	}
	
	public String toString() {
		return "STATE='"+ endState +"'";
	}

}
