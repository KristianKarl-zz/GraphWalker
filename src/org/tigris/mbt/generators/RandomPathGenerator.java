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

	static Logger logger = Logger.getLogger(RandomPathGenerator.class);

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
		logger.debug( Util.getCompleteEdgeName(edge ) );
		String[] retur = {getMachine().getEdgeName(edge), getMachine().getCurrentStateName()};
		return retur;
	}
	
	private DirectedSparseEdge getWeightedEdge(Set availableEdges)
	{
		DirectedSparseEdge edge = null;
		Vector zeroes = new Vector();
		float sum = 0;
		float limit = random.nextFloat();

		for ( Iterator i = availableEdges.iterator(); i.hasNext();)
		{
			DirectedSparseEdge e = (DirectedSparseEdge)i.next();
			Float weight = (Float) e.getUserDatum( Keywords.WEIGHT_KEY );
			if(weight == null)
			{
				zeroes.add(e);
			} else {
				sum += weight.floatValue();
				if(sum >= limit && edge == null) edge = e;
			}
		}
		Util.AbortIf( sum > 1 ,"The weight of out edges excceds 1 for " + getMachine().getCurrentStateName() );
		if( edge == null )
		{
			edge = (DirectedSparseEdge) zeroes.get(random.nextInt(zeroes.size()));
		}
		return edge;
	}
	
	private DirectedSparseEdge getRandomEdge(Set availableEdges)
	{
		return (DirectedSparseEdge) availableEdges.toArray()[random.nextInt(availableEdges.size())];
	}

}
