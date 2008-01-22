package org.tigris.mbt.generators;

import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.conditions.StopCondition;

public abstract class PathGenerator {
    private FiniteStateMachine machine;
	private StopCondition stopCondition;
	
	public abstract String[] getNext();
    
    public boolean hasNext()
    {
    	return !stopCondition.isFulfilled();
    }
    public FiniteStateMachine getMachine() {
		return machine;
	}
    public void setMachine(FiniteStateMachine machine) {
		this.machine = machine;
		if(this.stopCondition != null)
			this.stopCondition.setMachine(machine);
	}
    public void setStopCondition(StopCondition stopCondition) {
		this.stopCondition = stopCondition;
		if(this.machine != null)
			this.stopCondition.setMachine(this.machine);
	}
    public StopCondition getStopCondition() {
		return stopCondition;
	}
    public double getConditionFulfillment()
    {
    	return stopCondition.getFulfillment();
    }

    PathGenerator() {
	}
    
    PathGenerator( StopCondition stopCondition ) {
    	setStopCondition(stopCondition);
	}
    
    PathGenerator( FiniteStateMachine machine, StopCondition stopCondition )
    {
    	this(stopCondition);
    	setMachine(machine);
    }
}
