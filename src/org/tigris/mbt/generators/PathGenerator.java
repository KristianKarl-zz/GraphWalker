package org.tigris.mbt.generators;

import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.conditions.StopCondition;

public abstract class PathGenerator {
    protected FiniteStateMachine machine;
	private StopCondition stopCondition;
	
	public abstract String[] getNext();
    
    public boolean hasNext()
    {
    	return !stopCondition.isFulfilled();
    }
    
    PathGenerator(FiniteStateMachine machine, StopCondition stopCondition)
    {
    	this.machine = machine;
    	this.stopCondition = stopCondition;
    }
}
