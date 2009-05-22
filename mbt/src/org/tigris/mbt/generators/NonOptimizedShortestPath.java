package org.tigris.mbt.generators;


import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.tigris.mbt.Keywords;
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
	
		DirectedSparseEdge edge = null;			
		do {
			getDijkstraPath();
			edge = (DirectedSparseEdge)dijkstraShortestPath.remove( 0 );			
		} while ( !isEdgeAvailable( edge ) );
		
		getMachine().walkEdge( edge );
		String[] retur = { getMachine().getEdgeName( edge ),
				           getMachine().getCurrentStateName() };
		return retur;			
	}
	
	private void getDijkstraPath()
	{
		// Is there a path to walk, given from DijkstraShortestPath?
		if ( dijkstraShortestPath == null || dijkstraShortestPath.size() == 0 )
		{
			Vector unvisitedEdges = getMachine().getUncoveredEdges();
			logger.debug( "Number of unvisited edges: " + unvisitedEdges.size() );
			Object[] shuffledList = Util.shuffle( unvisitedEdges.toArray() );
			DirectedSparseEdge e = (DirectedSparseEdge)shuffledList[ 0 ];
			
			logger.debug( "Current state: " + Util.getCompleteName( getMachine().getCurrentState() ) );
			logger.debug( "Will try to reach unvisited edge: " + Util.getCompleteName( e ) );
			
			dijkstraShortestPath = 
				new DijkstraShortestPath( getMachine().getModel() ).getPath( getMachine().getCurrentState(), e.getSource() );

			// DijkstraShortestPath.getPath returns 0 if there is no way to reach the destination. But,
			// DijkstraShortestPath.getPath also returns 0 paths if the the source and destination vertex are the same, even if there is
			// an edge there (self-loop). So we have to check for that.
			if ( dijkstraShortestPath.size() == 0 )
			{
				if ( getMachine().getCurrentState().getUserDatum( Keywords.INDEX_KEY ) != e.getSource().getUserDatum( Keywords.INDEX_KEY ) )
				{
					String msg = "There is no way to reach: " + Util.getCompleteName( e ) + ", from: " + Util.getCompleteName( getMachine().getCurrentState() );
					logger.error( msg );
					throw new RuntimeException( msg );
				}
			}
			
			dijkstraShortestPath.add( e );
			logger.debug( "Dijkstra path length to that edge: " + dijkstraShortestPath.size() );
			logger.debug( "Dijksta path:" );				
			for (Iterator iterator = dijkstraShortestPath.iterator(); iterator
					.hasNext();) {
				DirectedSparseEdge object = (DirectedSparseEdge) iterator.next();
				logger.debug( "  " + Util.getCompleteName( object ) );				
			}
		}		
	}
}
