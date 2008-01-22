package org.tigris.mbt.conditions;

import java.util.Iterator;
import java.util.Vector;

import org.tigris.mbt.FiniteStateMachine;

public class AlternativeCondition extends StopCondition {

	private Vector conditions;

	public boolean isFulfilled() {
		for(Iterator i = conditions.iterator();i.hasNext();)
		{
			if(((StopCondition)i.next()).isFulfilled()) return true;
		}
		return false;
	}

	public AlternativeCondition() {
		this.conditions = new Vector();
	}
	
	public void add(StopCondition conditon)
	{
		this.conditions.add(conditon);
	}

	public void setMachine(FiniteStateMachine machine) {
		super.setMachine(machine);
		for(Iterator i = conditions.iterator();i.hasNext();)
			((StopCondition)i.next()).setMachine(machine);
	}

	public double getFulfillment() {
		double retur = 0; 
		for(Iterator i = conditions.iterator();i.hasNext();)
		{
			double newFullfillment = ((StopCondition)i.next()).getFulfillment();
			if( newFullfillment > retur ) retur = newFullfillment;
		}
		return retur;
	}
}
