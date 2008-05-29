package org.tigris.mbt.generators;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.tigris.mbt.Keywords;
import org.tigris.mbt.Util;
import org.tigris.mbt.exceptions.FoundNoEdgeException;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

public class RandomPathGenerator extends PathGenerator {

	static Logger logger = Util.setupLogger(RandomPathGenerator.class);

	private Random random = new Random();

	public String[] getNext() {
		Set availableEdges;
		try {
			availableEdges = getMachine().getCurrentOutEdges();
		} catch (FoundNoEdgeException e) {
			throw new RuntimeException("No possible edges available for path", e);
		}
		DirectedSparseEdge edge = (getMachine().isWeighted() ? getWeightedEdge(availableEdges) : getRandomEdge(availableEdges));
		getMachine().walkEdge(edge);
		logger.debug( edge.getUserDatum( Keywords.FULL_LABEL_KEY) );
		logger.debug( Util.getCompleteName(edge ) );
		String[] retur = {getMachine().getEdgeName(edge), getMachine().getCurrentStateName()};
		return retur;
	}
	
    private DirectedSparseEdge getWeightedEdge(Set availableEdges)
    {
        Object[] edges = availableEdges.toArray();
        DirectedSparseEdge edge = null;
        float probabilities[]   = new float[ availableEdges.size() ];
        int   numberOfZeros     = 0;
        float sum               = 0;

        for ( int i = 0; i < edges.length; i++ )
        {
            edge = (DirectedSparseEdge)edges[ i ];

            if ( edge.containsUserDatumKey( Keywords.WEIGHT_KEY ) )
            {
                Float weight = (Float)edge.getUserDatum( Keywords.WEIGHT_KEY );
                probabilities[ i ] = weight.floatValue();
                sum += probabilities[ i ];
            }
            else
            {
                numberOfZeros++;
                probabilities[ i ] = 0;
            }
        }

        if ( sum > 1 )
        {
            throw new RuntimeException( "The sum of all weights in edges from vertex: '" + (String)edge.getSource().getUserDatum( Keywords.LABEL_KEY ) + "', adds up to more than 1.00" );
        }

        float rest = ( 1 - sum ) / numberOfZeros;
        int index = random.nextInt( 100 );
        logger.debug( "Randomized integer index = " + index );

        float weight = 0;
        for ( int i = 0; i < edges.length; i++ )
        {
            if ( probabilities[ i ] == 0 )
            {
                probabilities[ i ] = rest;
            }
            logger.debug( "The edge: '" + (String)((DirectedSparseEdge)edges[ i ]).getUserDatum( Keywords.LABEL_KEY ) + "' is given the probability of " + probabilities[ i ] * 100 + "%"  );

            weight = weight + probabilities[ i ] * 100;
            logger.debug( "Current weight is: " + weight  );
            if ( index < weight )
            {
                edge = (DirectedSparseEdge)edges[ i ];
                logger.debug( "Selected edge is: " + Util.getCompleteName( edge ) );
                break;
            }
        }

        return edge;
    }
	
	private DirectedSparseEdge getRandomEdge(Set availableEdges)
	{
		return (DirectedSparseEdge) availableEdges.toArray()[random.nextInt(availableEdges.size())];
	}
	
	public String toString() {
		return "RANDOM{"+ super.toString() +"}";
	}
}
