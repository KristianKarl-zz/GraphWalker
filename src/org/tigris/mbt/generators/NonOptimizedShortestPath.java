package org.tigris.mbt.generators;


import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.tigris.mbt.Util;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;


public class NonOptimizedShortestPath extends PathGenerator
{
	static Logger logger = Util.setupLogger(NonOptimizedShortestPath.class);
	private List dijkstraShortestPath;
	
	public String[] getNext() 
	{
		Util.AbortIf(!hasNext(), "Finished");
	
		
		// Is there a path to walk, given from DijkstraShortestPath?
		if ( dijkstraShortestPath == null || dijkstraShortestPath.size() == 0 )
		{
			Vector unvisitedEdges = getMachine().getUncoveredEdges();
			logger.debug( "Number of unvisited edges: " + unvisitedEdges.size() );
			Object[] shuffledList = Util.shuffle( unvisitedEdges.toArray() );
			DirectedSparseEdge e = (DirectedSparseEdge)shuffledList[ 0 ];
			
			logger.debug( "Current state: " + Util.getCompleteName( getMachine().getCurrentState() ) );
			logger.debug( "Unvisited edge: " + Util.getCompleteName( e ) );
			
			dijkstraShortestPath = 
				new DijkstraShortestPath( 
						getMachine().getModel() ).getPath( 
								getMachine().getCurrentState(), 
								e.getSource() );
			dijkstraShortestPath.add( e );
			logger.debug( "Dijkstra path length: " + dijkstraShortestPath.size() );
		}
		DirectedSparseEdge edge = (DirectedSparseEdge)dijkstraShortestPath.remove( 0 );
		
		getMachine().walkEdge( edge );
		String[] retur = { getMachine().getEdgeName( edge ),
				           getMachine().getCurrentStateName() };
		return retur;
	}
}
