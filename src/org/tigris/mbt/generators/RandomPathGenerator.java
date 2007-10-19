package org.tigris.mbt.generators;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.Keywords;
import org.tigris.mbt.Util;
import org.tigris.mbt.conditions.StopCondition;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

public class RandomPathGenerator extends PathGenerator {

	private Random random = new Random();

	public RandomPathGenerator(FiniteStateMachine machine, StopCondition stopCondition) {
		super(machine, stopCondition);
	}

	public String[] getNext() {
		Util.AbortIf(!hasNext(), "No more lines available");
		Set availableEdges = machine.getCurrentOutEdges();
		DirectedSparseEdge edge = null;
		Util.AbortIf(availableEdges.size() == 0, "Found a dead end: '" + machine.getCurrentStateName() + "'"); 
		if( machine.isWeighted() )
		{
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
		}else{
			edge = (DirectedSparseEdge) availableEdges.toArray()[random.nextInt(availableEdges.size())];
		}
		machine.walkEdge(edge);
		String[] retur = {machine.getEdgeName(edge), machine.getCurrentStateName()};
		return retur;
	}

}
