package org.tigris.mbt.conditions;

import java.util.Iterator;
import java.util.Vector;

public class AlternativeCondition implements StopCondition {

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
}
