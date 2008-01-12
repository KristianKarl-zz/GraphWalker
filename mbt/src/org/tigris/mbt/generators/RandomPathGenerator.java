package org.tigris.mbt.generators;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.Keywords;
import org.tigris.mbt.Util;
import org.tigris.mbt.conditions.StopCondition;
import org.tigris.mbt.exceptions.FoundNoEdgeException;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

public class RandomPathGenerator extends PathGenerator {

	static Logger logger = Logger.getLogger(RandomPathGenerator.class);

	private Random random = new Random();

	public RandomPathGenerator(FiniteStateMachine machine, StopCondition stopCondition) {
		super(machine, stopCondition);
	}

	public String[] getNext() {
		Set availableEdges;
		try {
			availableEdges = machine.getCurrentOutEdges();
		} catch (FoundNoEdgeException e) {
			throw new RuntimeException("No possible edges available for path", e);
		}
		DirectedSparseEdge edge = (machine.isWeighted() ? getWeightedEdge(availableEdges) : getRandomEdge(availableEdges));
		machine.walkEdge(edge);
		logger.debug( edge.getUserDatum( Keywords.FULL_LABEL_KEY) );
		logger.debug( Util.getCompleteEdgeName(edge ) );
		String[] retur = {machine.getEdgeName(edge), machine.getCurrentStateName()};
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
		Util.AbortIf( sum > 1 ,"The weight of out edges excceds 1 for " + machine.getCurrentStateName() );
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
