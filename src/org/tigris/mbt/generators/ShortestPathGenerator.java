package org.tigris.mbt.generators;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.tigris.mbt.ExtendedFiniteStateMachine;
import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.Keywords;
import org.tigris.mbt.Util;
import org.tigris.mbt.exceptions.FoundNoEdgeException;
import org.tigris.mbt.generators.PathGenerator;

import edu.uci.ics.jung.graph.impl.AbstractElement;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserData;

public class ShortestPathGenerator extends PathGenerator {

	static Logger logger = Util.setupLogger(ShortestPathGenerator.class);

	private Stack preCalculatedPath = null;
	private DirectedSparseVertex lastState;
	private boolean extended;

	public void setMachine(FiniteStateMachine machine) {
		super.setMachine(machine);
		this.extended = machine instanceof ExtendedFiniteStateMachine;
	}
	private void resetNode(AbstractElement abstractElement)
	{
		DijkstraPoint dp = (extended?new ExtendedDijkstraPoint((ExtendedFiniteStateMachine) getMachine()):new DijkstraPoint(getMachine())); 
		abstractElement.setUserDatum(Keywords.DIJKSTRA, dp, UserData.SHARED );
	}
	
	public String[] getNext() {
		Util.AbortIf(!hasNext(), "No more lines available");
		
		if(lastState == null || lastState != getMachine().getCurrentState() || preCalculatedPath == null || preCalculatedPath.size() == 0)
		{
			for(Iterator i = getMachine().getAllStates().iterator();i.hasNext();)
			{
				resetNode((AbstractElement)i.next());
			}
			boolean oldBacktracking = getMachine().isBacktrack();
			getMachine().setBacktrack(true);
			getMachine().setCalculatingShortestPath(true);
			try {
				calculateShortestPath();
			} catch (FoundNoEdgeException e) {
				throw new RuntimeException("No possible edges available for path", e);
			}
			finally
			{
				getMachine().setBacktrack(oldBacktracking);
				getMachine().setCalculatingShortestPath(false);				
			}

			if(preCalculatedPath == null)
			{
				String unreachableStates = "";
				String reachableStates = "";
				for(Iterator i = getMachine().getAllStates().iterator();i.hasNext();)
				{
					DirectedSparseVertex dsv = (DirectedSparseVertex)i.next();
					DijkstraPoint dp = (DijkstraPoint) dsv.getUserDatum(Keywords.DIJKSTRA);
					if(dp == null || dp.getShortestPath() == null || dp.getShortestPath().size() == 0)
					{
						unreachableStates += "Unreachable vertex: " + Util.getCompleteVertexName( dsv ) + "\n";
					}
					else
					{
						reachableStates += "Reachable vertex: " + Util.getCompleteVertexName( dsv ) + " by means of " + dp + "\n";
					}
				}
				throw new RuntimeException( "No path found to the following vertices:\n" + unreachableStates +"\n\n"+
						"Paths found to the following:\n" +reachableStates);
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

	private void calculateShortestPath() throws FoundNoEdgeException
	{
		DijkstraPoint dp = ((DijkstraPoint)getMachine().getCurrentState().getUserDatum(Keywords.DIJKSTRA));
		Stack edgePath = (Stack) dp.getPath().clone();
		Set outEdges = getMachine().getCurrentOutEdges();
		for(Iterator i = outEdges.iterator();i.hasNext();)
		{
			DirectedSparseEdge e = (DirectedSparseEdge)i.next();
			edgePath.push(e);
			getMachine().walkEdge(e);
			try {
				if(hasNext())
				{
					DijkstraPoint nextDp = ((DijkstraPoint)getMachine().getCurrentState().getUserDatum(Keywords.DIJKSTRA));
					if(nextDp.compareTo(edgePath)>0)
					{
						nextDp.setPath(edgePath);
						calculateShortestPath();
					}
				}
				else if(preCalculatedPath == null || preCalculatedPath.size() > edgePath.size())
				{
					preCalculatedPath = (Stack) edgePath.clone();
				}
			} catch (FoundNoEdgeException culDeSac) {}
			getMachine().backtrack( false );
			edgePath.pop();
		}
	}

	/**
	 * Will reset the generator to its initial state.
	 */
	public void reset()
	{
		preCalculatedPath = null;
	}

	protected class DijkstraPoint implements Comparable
	{
		protected Stack edgePath = null;
		protected double completion = 0;
		protected FiniteStateMachine parent;
		
		public DijkstraPoint(FiniteStateMachine parent)
		{
			this.parent = parent;
		}
		
		public void setPath( Stack edgePath )
		{
			this.edgePath = (Stack) edgePath.clone();
			this.completion = getConditionFulfillment();
		}

		public double getCompletion() 
		{
			return completion;
		}
		
		public Stack getPath()
		{
			if(edgePath==null)edgePath = new Stack();
			return edgePath;
		}

		public Stack getShortestPath()
		{
			if(edgePath==null)edgePath = new Stack();
			return edgePath;
		}

		public int compareTo(Object o) {
			
			double newComplete = getConditionFulfillment();
			double myComplete = getCompletion();
			if(newComplete > myComplete) return 1;
			if(0.0000001 + newComplete < myComplete ) return -1;
			int a = getPath().size();
			if(a==0) return 1;
			int b = 0;
			if(o instanceof Stack)
			{
				b = ((Stack)o).size();
			} else {
				b = ((DijkstraPoint)o).getPath().size();
			}
			return a-b;
		}

		public String toString()
		{
			return edgePath.toString();
		}
	}

	protected class ExtendedDijkstraPoint extends DijkstraPoint implements Comparable
	{
		protected Hashtable edgePaths;
		protected Hashtable completions;
		
		public ExtendedDijkstraPoint( ExtendedFiniteStateMachine parent) 
		{
			super(parent);
			edgePaths = new Hashtable();
			completions = new Hashtable();
		}

		public double getCompletion() {
			String key = ((ExtendedFiniteStateMachine)parent).getCurrentDataString();
			Double compDouble = ((Double)completions.get(key));
			return (compDouble==null?0:compDouble.doubleValue()); 
		}
		
		public void setPath( Stack edgePath )
		{
			if(edgePaths == null) edgePaths = new Hashtable();
			String key = ((ExtendedFiniteStateMachine)parent).getCurrentDataString();
			edgePaths.put(key, edgePath.clone()); 
			completions.put(key, new Double(getConditionFulfillment())); 
			if(edgePaths.size()>500)
				throw new RuntimeException( "Too many internal states in "+ Util.getCompleteVertexName(parent.getCurrentState()) + " please revise model.");
		}

		public Stack getPath()
		{
			String key = ((ExtendedFiniteStateMachine)parent).getCurrentDataString();
			Stack localPath = (Stack) edgePaths.get(key);
			if(localPath == null)
			{
				localPath = new Stack();
				edgePaths.put(key, localPath);
			}
			return localPath;
		}

		public Stack getShortestPath()
		{
			System.out.println("ERROR!!");
			Stack retur = null; 
			for(Iterator i = edgePaths.values().iterator();i.hasNext();)
			{
				Stack path = (Stack) i.next();
				if(retur == null || retur.size() > path.size()) retur = path;
			}
			return retur;
		}
		
		public String toString()
		{
			return edgePaths.toString();
		}
	}
}
