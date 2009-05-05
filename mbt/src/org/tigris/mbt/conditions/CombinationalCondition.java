package org.tigris.mbt.conditions;

import java.util.Iterator;
import java.util.Vector;

import org.tigris.mbt.FiniteStateMachine;

public class CombinationalCondition extends StopCondition {

	private Vector<StopCondition> conditions;

	public boolean isFulfilled() {
		for(Iterator<StopCondition> i = conditions.iterator();i.hasNext();)
		{
			if(!i.next().isFulfilled()) return false;
		}
		return true;
	}

	public CombinationalCondition() {
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
			retur += i.next().getFulfilment();
		}
		return retur / (double)conditions.size();
	}
	
	public String toString() {
		String retur = "(";
		for(Iterator<StopCondition> i = conditions.iterator();i.hasNext();)
		{
			retur += i.next().toString();
			if(i.hasNext()) retur += " AND ";
		}
		return retur + ")";
	}

}
