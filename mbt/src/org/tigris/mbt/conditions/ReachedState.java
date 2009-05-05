package org.tigris.mbt.conditions;

import java.util.ArrayList;

import org.tigris.mbt.ExtendedFiniteStateMachine;
import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.Util;
import org.tigris.mbt.Vertex;

public class ReachedState extends StopCondition {

	private ArrayList<Vertex> allStates;
	private Vertex endState;
	private int[] proximity;
	private int maxDistance;
	private String stateName;
	private String subState;

	public boolean isFulfilled() {
		return getFulfilment() >= 0.99999;
	}

	public void setMachine(FiniteStateMachine machine) {
		super.setMachine(machine);
		if(this.endState == null)
			this.endState = machine.findState(stateName);
		if(this.endState == null)
			throw new RuntimeException("State '"+stateName+"' not found in model");
		this.proximity = getFloydWarshall();
		this.maxDistance = max(this.proximity);
	}

	public ReachedState(Vertex endState)
	{
		this.endState = endState;
		this.subState = "";
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
			String currentState = getMachine().getCurrentStateName();
			String currentSubState = "";
			if(currentState.contains("/"))
			{
				currentSubState = currentState.split("/",2)[1];
			} 
			double maxDiff = Math.max(currentSubState.length(), this.subState.length());
			double substateFulfilment = (double)1 - ((double)Util.getLevenshteinDistance(currentSubState, this.subState)) / maxDiff;
			return (substateFulfilment + ((double)1)-( (double)distance / (double)maxDistance))/2;
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
		allStates = new ArrayList<Vertex>(getMachine().getAllStates());
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
				else if ( getMachine().getModel().isPredecessor( allStates.get( i ), allStates.get( j ) ) )
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
		throw new RuntimeException("state no longer in Graph!");
	}
	
	public String toString() {
		return "STATE='"+ endState +"'";
	}

}
