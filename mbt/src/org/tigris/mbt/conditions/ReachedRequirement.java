package org.tigris.mbt.conditions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class ReachedRequirement extends StopCondition {
	
	private Collection requirements;

	public ReachedRequirement(String requirements)
	{
		this.requirements = new HashSet(Arrays.asList(requirements.split(",")));
	}
	
	public boolean isFulfilled() {
		return machine.getCoveredRequirements().containsAll(requirements);
	}

	public double getFulfilment() {
		Collection covered = machine.getCoveredRequirements();
		covered.retainAll(requirements);
		return covered.size() / (double)requirements.size();
	}
	public String toString() {
		return "RC="+ Arrays.deepToString(requirements.toArray());
	}

}
