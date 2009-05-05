package org.tigris.mbt.conditions;

import java.util.Iterator;
import java.util.Vector;

import org.tigris.mbt.FiniteStateMachine;

public class AlternativeCondition extends StopCondition {

	private Vector<StopCondition> conditions;

	public boolean isFulfilled() {
		for(Iterator<StopCondition> i = conditions.iterator();i.hasNext();)
		{
			if(i.next().isFulfilled()) return true;
		}
		return false;
	}

	public AlternativeCondition() {
		this.conditions = new Vector<StopCondition>();
	}
	
	public void add(StopCondition conditon)
	{
		this.conditions.add(conditon);
	}

	public void setMachine(FiniteStateMachine machine) {
		super.setMachine(machine);
		for(Iterator<StopCondition> i = conditions.iterator();i.hasNext();)
			i.next().setMachine(machine);
	}

	public double getFulfilment() {
		double retur = 0; 
		for(Iterator<StopCondition> i = conditions.iterator();i.hasNext();)
		{
			double newFullfillment = i.next().getFulfilment();
			if( newFullfillment > retur ) retur = newFullfillment;
		}
		return retur;
	}
	
	public String toString() {
		String retur = "(";
		for(Iterator<StopCondition> i = conditions.iterator();i.hasNext();)
		{
			retur += i.next().toString();
			if(i.hasNext()) retur += " OR ";
		}
		return retur + ")";
	}
}
