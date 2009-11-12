package org.tigris.mbt.conditions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class ReachedRequirement extends StopCondition {

	private Collection<String> requirements;

	public ReachedRequirement(String requirements) {
		String[] list = requirements.split(",");
		for (int i = 0; i < list.length; i++) {
			list[i] = list[i].trim();
		}
		this.requirements = new HashSet<String>(Arrays.asList(list));
	}

	public boolean isFulfilled() {
		return machine.getCoveredRequirements().containsAll(requirements);
	}

	public double getFulfilment() {
		Collection<String> covered = machine.getCoveredRequirements();
		covered.retainAll(requirements);
		return covered.size() / (double) requirements.size();
	}

	public String toString() {
		return "RC=" + Arrays.deepToString(requirements.toArray());
	}

	public Collection<String> getRequirements() {
		return requirements;
	}

}
