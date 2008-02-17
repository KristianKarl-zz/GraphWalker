package org.tigris.mbt.generators;


import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.tigris.mbt.Keywords;
import org.tigris.mbt.Util;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;


public class NonOptimizedShortestPath extends PathGenerator
{
	static Logger logger = Util.setupLogger(ShortestPathGenerator.class);
	private List shortestPathToVertex = null ;

	public String[] getNext() 
	{
		Util.AbortIf(!hasNext(), "Finished");
		
		if ( shortestPathToVertex == null || shortestPathToVertex.size() == 0 )
		{
			Vector unvisitedEdges = getMachine().getUnvisitedEdges();
			logger.debug( "Number of unvisited edges: " + unvisitedEdges.size() );
			Object[] shuffledList = Util.shuffle( unvisitedEdges.toArray() );
			DirectedSparseEdge e = (DirectedSparseEdge)shuffledList[ 0 ];
			
			logger.debug( "Current state: " + Util.getCompleteName( getMachine().getCurrentState() ) );
			logger.debug( "Unvisited edge: " + Util.getCompleteName( e ) );
			
			shortestPathToVertex = 
				new DijkstraShortestPath( 
						getMachine().getModel() ).getPath( 
								getMachine().getCurrentState(), 
								e.getSource() );
			
			logger.debug( "Dijkstra path length: " + shortestPathToVertex.size() );
			
			// DijkstraShortestPath.getPath returns 0 if there is no way to reach the destination. But,
			// DijkstraShortestPath.getPath also returns 0 paths if the the source and destination vertex are the same, even if there is
			// an edge there (self-loop). So we have to check for that.
			if ( shortestPathToVertex.size() == 0 )
			{
				if ( e.getSource().getUserDatum( Keywords.INDEX_KEY ) != e.getDest().getUserDatum( Keywords.INDEX_KEY ) )
				{
					shortestPathToVertex.add( e );
				}
				else
				{
					String msg = "There is no way to reach: " + Util.getCompleteName( e ) +
		                         ", from: " + Util.getCompleteName( getMachine().getCurrentState() );
					logger.warn( msg );
				}
			}
		}
		DirectedSparseEdge edge = (DirectedSparseEdge)shortestPathToVertex.remove( 0 );
		
		getMachine().walkEdge( edge );
		String[] retur = { getMachine().getEdgeName( edge ),
				           getMachine().getCurrentStateName() };
		return retur;
	}
}
