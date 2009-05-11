package org.tigris.mbt.generators;


import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.tigris.mbt.Util;
import org.tigris.mbt.graph.Edge;
import org.tigris.mbt.graph.Vertex;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;



public class NonOptimizedShortestPath extends PathGenerator
{
	static Logger logger = Util.setupLogger(NonOptimizedShortestPath.class);
	private List<Edge> dijkstraShortestPath;
	
	public String[] getNext() 
	{
		Util.AbortIf(!hasNext(), "Finished");
	
		Edge edge = null;			
		do {
			getDijkstraPath();
			edge = dijkstraShortestPath.remove( 0 );			
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
			Vector<Edge> unvisitedEdges = getMachine().getUncoveredEdges();
			logger.debug( "Number of unvisited edges: " + unvisitedEdges.size() );
			Object[] shuffledList = Util.shuffle( unvisitedEdges.toArray() );
			Edge e = (Edge)shuffledList[ 0 ];
			
			logger.debug( "Current state: " + getMachine().getCurrentState() );
			logger.debug( "Will try to reach unvisited edge: " + e );
			
			dijkstraShortestPath = 
				new DijkstraShortestPath<Vertex, Edge>( getMachine().getModel() ).getPath( getMachine().getCurrentState(), getMachine().getModel().getSource(e) );

			// DijkstraShortestPath.getPath returns 0 if there is no way to reach the destination. But,
			// DijkstraShortestPath.getPath also returns 0 paths if the the source and destination vertex are the same, even if there is
			// an edge there (self-loop). So we have to check for that.
			if ( dijkstraShortestPath.size() == 0 )
			{
				if ( getMachine().getCurrentState().getIndexKey() != 
					 getMachine().getModel().getSource(e).getIndexKey() )
				{
					String msg = "There is no way to reach: " + e + ", from: " + getMachine().getCurrentState();
					logger.error( msg );
					throw new RuntimeException( msg );
				}
			}
			
			dijkstraShortestPath.add( e );
			logger.debug( "Dijkstra path length to that edge: " + dijkstraShortestPath.size() );
			logger.debug( "Dijksta path:" );				
			for (Iterator<Edge> iterator = dijkstraShortestPath.iterator(); iterator
					.hasNext();) {
				Edge object = iterator.next();
				logger.debug( "  " + object);				
			}
		}		
	}
}
