package org.tigris.mbt.conditions;

import java.util.ArrayList;

import org.tigris.mbt.FiniteStateMachine;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

public class ReachedEdge extends StopCondition {

	private ArrayList allEdges;
	private DirectedSparseEdge endEdge;
	private int[] proximity;
	private int maxDistance;
	private String edgeName;
//	private String subState;

	public boolean isFulfilled() {
		return getFulfilment() >= 0.99999;
	}

	public void setMachine(FiniteStateMachine machine) {
		super.setMachine(machine);
		if(this.endEdge == null)
			this.endEdge = machine.findEdge(edgeName);
		if(this.endEdge == null)
			throw new RuntimeException("State '"+edgeName+"' not found in model");
		this.proximity = getFloydWarshall();
		this.maxDistance = max(this.proximity);
	}
	
	public ReachedEdge(DirectedSparseEdge endEdge)
	{
		this.endEdge = endEdge;
//		this.subState = "";
	}

	public ReachedEdge(String edgeName) 
	{
		String[] state = edgeName.split("/", 2);
		this.edgeName = state[0];
//		this.subState = (state.length>1?state[1]:"");
	}

	public double getFulfilment() {
		int distance = this.maxDistance;
		if(getMachine().getLastEdge() != null)
			distance = proximity[allEdges.indexOf(getMachine().getLastEdge())];
/**XXX should we use substate information in this condition?
 *  
 */
		
//		if(getMachine() instanceof ExtendedFiniteStateMachine)
//		{
//			/**TODO should probably be the inner state of the last state visited as
//			 * the current state contains action information from the edge itself
//			 */ 
//			
//			String currentState = getMachine().getCurrentStateName();
//			String currentSubState = "";
//			if(currentState.contains("/"))
//			{
//				currentSubState = currentState.split("/",2)[1];
//			} 
//			double maxDiff = Math.max(currentSubState.length(), this.subState.length());
//			double substateFulfilment = (double)1 - ((double)Util.getLevenshteinDistance(currentSubState, this.subState)) / maxDiff;
//			return (substateFulfilment + ((double)1)-( (double)distance / (double)maxDistance))/2;
//		}
		
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
		allEdges = new ArrayList(getMachine().getAllEdges());
		int n = allEdges.size();
		int[][] retur = new int[n][n];
		for(int i = 0; i<n;i++)
			for(int j = 0; j<n;j++)
			{
				int x = 99999;
				if(i==j)
				{
					x = 0;
				}
				else if(((DirectedSparseEdge)allEdges.get(j)).getDest().isSource((DirectedSparseEdge)allEdges.get(i)))
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
		int startIndex = allEdges.indexOf(endEdge);
		if(startIndex >= 0)
			return path[startIndex];
		throw new RuntimeException("edge no longer in Graph!");
	}
	
	public String toString() {
		return "EDGE='"+ endEdge +"'";
	}
}
