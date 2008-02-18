package org.tigris.mbt.generators;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.Util;
import org.tigris.mbt.exceptions.FoundNoEdgeException;
import org.tigris.mbt.generators.PathGenerator;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

public class ShortestPathGenerator extends PathGenerator {

	static Logger logger = Util.setupLogger(ShortestPathGenerator.class);

	private Stack preCalculatedPath = null;
	private DirectedSparseVertex lastState;

	public void setMachine(FiniteStateMachine machine) {
		super.setMachine(machine);
	}

	public String[] getNext() {
		Util.AbortIf(!hasNext(), "No more lines available");
		if(lastState == null || lastState != getMachine().getCurrentState() || preCalculatedPath == null || preCalculatedPath.size() == 0)
		{
			boolean oldCalculatingPathValue = getMachine().isCalculatingPath();
			getMachine().setCalculatingPath(true);

			preCalculatedPath = a_star();
			
			getMachine().setCalculatingPath(oldCalculatingPathValue);				

			if(preCalculatedPath == null)
			{
				throw new RuntimeException( "No path found to " + this.getStopCondition() );
			}
			
			// reverse path
			Stack temp = new Stack();
			while( preCalculatedPath.size() > 0 )
			{
				temp.push(preCalculatedPath.pop());
			}
			preCalculatedPath = temp;
		}

		DirectedSparseEdge edge = (DirectedSparseEdge) preCalculatedPath.pop();
		getMachine().walkEdge(edge);
		lastState = getMachine().getCurrentState();
		String[] retur = {getMachine().getEdgeName(edge), getMachine().getCurrentStateName()};
		return retur;
	}

	
	private Stack a_star()
	{
		Vector closed = new Vector();

		PriorityQueue q = new PriorityQueue(10, new Comparator(){
			public int compare(Object arg0, Object arg1) {
				if(arg0 instanceof weightedPath && arg1 instanceof weightedPath)
				{
					int retur = Double.compare(((weightedPath)arg0).getWeight(), ((weightedPath)arg1).getWeight());
					if(retur == 0)
						retur = ((weightedPath)arg0).getPath().size() - ((weightedPath)arg1).getPath().size();
					return retur;
				}
				throw new RuntimeException("Could not compare '"+arg0.getClass().getName()+"' with '"+arg1.getClass().getName()+"' as they are not both instances of 'weightedPath'");
			}
		});

		Set availableOutEdges;
		try {
			availableOutEdges = getMachine().getCurrentOutEdges();
		} catch (FoundNoEdgeException e) {
			throw new RuntimeException("No available edges found at "+ getMachine().getCurrentStateName(), e );
		}
		for(Iterator i=availableOutEdges.iterator(); i.hasNext();)
		{
			DirectedSparseEdge y = (DirectedSparseEdge) i.next();
			Stack s = new Stack();
			s.push(y);
			q.add(getWeightedPath(s));
		}
		double maxWeight = 0;
		while(q.size() > 0)
		{
			weightedPath p = (weightedPath) q.poll();
			if(p.getWeight() > maxWeight) maxWeight = p.getWeight();
			if( p.getWeight() > 0.99999) // are we done yet?
				return p.getPath();

			DirectedSparseEdge x = (DirectedSparseEdge) p.getPath().peek();
			
			// have we been here before?
			if(closed.contains(Util.getCompleteName(x)+p.getSubState()+p.getWeight()))
				continue; // ignore this and move on

			// We don't want to use this edge again as this path is 
			// the fastest, and if we come here again we have used more 
			// steps to get here than we used this time. 
			closed.add(Util.getCompleteName(x)+p.getSubState()+p.getWeight());

			availableOutEdges = getPathOutEdges(p.getPath());
			if(availableOutEdges != null && availableOutEdges.size()>0)
			{
				for(Iterator i=availableOutEdges.iterator(); i.hasNext();)
				{
					DirectedSparseEdge y = (DirectedSparseEdge) i.next();
					Stack newStack = (Stack)p.getPath().clone();
					newStack.push(y);
					q.add(getWeightedPath(newStack));
				}
			}
		}
		throw new RuntimeException("No path found to satisfy stop condition "+getStopCondition() +", best path satified only "+ (int)(maxWeight*100) +"% of condition."); 
	}
	
	private weightedPath getWeightedPath(Stack path)
	{
		double weight = 0;
		String subState = ""; 

		getMachine().storeState();
		getMachine().walkEdge(path);
		weight = getConditionFulfilment();
		String currentState = getMachine().getCurrentStateName();
		if(currentState.contains("/"))
		{
			subState = currentState.split("/",2)[1]; 
		}
		getMachine().restoreState();

		return new weightedPath(path, weight, subState);
	}
    
	private Set getPathOutEdges(Stack path)
	{
		Set retur = null;
		getMachine().storeState();
		getMachine().walkEdge(path);
		try {
			retur = getMachine().getCurrentOutEdges();
		} catch (FoundNoEdgeException e) {
			// no edges found? degrade gracefully and return the default value of null.
		}
		getMachine().restoreState();
		return retur;
	}

	/**
	 * Will reset the generator to its initial state.
	 */
	public void reset()
	{
		preCalculatedPath = null;
	}

	public String toString() {
		return "SHORTEST{"+ super.toString() +"}";
	}

	private class weightedPath{
		private double weight;
		private Stack path;
		private String subState;
		
		public String getSubState() {
			return subState;
		}
		
		public void setSubState(String subState) {
			this.subState = subState;
		}
		
		public Stack getPath() {
			return path;
		}
		
		public void setPath(Stack path) {
			this.path = path;
		}
		
		public double getWeight() {
			return weight;
		}
		
		public void setWeight(double weight) {
			this.weight = weight;
		}
		
		public weightedPath(Stack path, double weight, String subState) {
			setPath(path);
			setWeight(weight);
			setSubState(subState);
		}
	}
}