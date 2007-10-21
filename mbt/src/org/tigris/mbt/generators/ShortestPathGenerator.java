package org.tigris.mbt.generators;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;

import org.tigris.mbt.ExtendedFiniteStateMachine;
import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.Keywords;
import org.tigris.mbt.Util;
import org.tigris.mbt.conditions.StopCondition;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserData;

public class ShortestPathGenerator extends PathGenerator {

	private Stack preCalculatedPath = null;
	private DirectedSparseVertex lastState;

	public ShortestPathGenerator(FiniteStateMachine machine, StopCondition stopCondition) {
		super(machine, stopCondition);
	}

	public String[] getNext() {
		Util.AbortIf(!hasNext(), "No more lines available");
		
		if(lastState == null || lastState != machine.getCurrentState() || preCalculatedPath.size() == 0)
		{
			machine.pushState();
			
			if(machine instanceof ExtendedFiniteStateMachine)
			{
				for(Iterator i = machine.getAllStates().iterator();i.hasNext();)
				{
					DirectedSparseVertex dsv = (DirectedSparseVertex)i.next();
					dsv.setUserDatum(Keywords.DIJKSTRA, new ExtendedDijkstraPoint((ExtendedFiniteStateMachine) machine), UserData.SHARED );
				}
			}
			else
			{
				for(Iterator i = machine.getAllStates().iterator();i.hasNext();)
				{
					DirectedSparseVertex dsv = (DirectedSparseVertex)i.next();
					dsv.setUserDatum(Keywords.DIJKSTRA, new DijkstraPoint(), UserData.SHARED );
				}
			}

			calculateShortestPath();
			// reverse path
			Util.AbortIf(preCalculatedPath == null, "No path found!");
			Stack temp = new Stack();
			while( preCalculatedPath.size() > 0 )
			{
				temp.push(preCalculatedPath.pop());
			}
			preCalculatedPath = temp;
			machine.popState();
		}

		DirectedSparseEdge edge = (DirectedSparseEdge) preCalculatedPath.pop();
		machine.walkEdge(edge);
		lastState = machine.getCurrentState();
		String[] retur = {machine.getEdgeName(edge), machine.getCurrentStateName()};
		return retur;
	}

	private void calculateShortestPath()
	{
		DijkstraPoint dp = ((DijkstraPoint)machine.getCurrentState().getUserDatum(Keywords.DIJKSTRA));
		Stack edgePath = (Stack) dp.getPath().clone();
		for(Iterator i = machine.getCurrentOutEdges().iterator();i.hasNext();)
		{
			DirectedSparseEdge e = (DirectedSparseEdge)i.next();
			edgePath.push(e);
			machine.pushState();
			machine.walkEdge(e);
			if(hasNext())
			{
				DijkstraPoint nextDp = ((DijkstraPoint)machine.getCurrentState().getUserDatum(Keywords.DIJKSTRA));
				if(nextDp.compareTo(edgePath)>0)
				{
					nextDp.setPath(edgePath);
					calculateShortestPath();
				}
			}
			else
			{
				preCalculatedPath = (Stack) edgePath.clone();
			}
			machine.popState();
			edgePath.pop();
		}
	}

	protected class DijkstraPoint implements Comparable
	{
		protected Stack edgePath = null;
		
		public DijkstraPoint()
		{
		}
		
		public void setPath( Stack edgePath )
		{
			this.edgePath = (Stack) edgePath.clone();
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
		
		
	}

	protected class ExtendedDijkstraPoint extends DijkstraPoint implements Comparable
	{
		protected Hashtable edgePaths;
		protected ExtendedFiniteStateMachine parent;
		
		public ExtendedDijkstraPoint()
		{
			edgePaths = new Hashtable();
		}
		
		public ExtendedDijkstraPoint( ExtendedFiniteStateMachine parent) {
			this();
			this.parent = parent;
		}

		public void setPath( Stack edgePath )
		{
			if(edgePaths == null) edgePaths = new Hashtable();
			edgePaths.put(parent.getCurrentDataString(), edgePath.clone()); 
		}

		public Stack getPath()
		{
			String key = parent.getCurrentDataString();
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
			Stack retur = null; 
			for(Iterator i = edgePaths.values().iterator();i.hasNext();)
			{
				Stack path = (Stack) i.next();
				if(retur == null || retur.size() > path.size()) retur = path;
			}
			return retur;
		}
	}
}
