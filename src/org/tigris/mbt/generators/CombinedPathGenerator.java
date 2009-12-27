package org.tigris.mbt.generators;

import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.tigris.mbt.conditions.StopCondition;
import org.tigris.mbt.machines.FiniteStateMachine;

public class CombinedPathGenerator extends PathGenerator {

	static Logger logger = Logger.getLogger(CombinedPathGenerator.class);

	private Vector<PathGenerator> generatorList = new Vector<PathGenerator>();
	private int currentGenerator = 0;

	public CombinedPathGenerator() {
		super();
	}

	public CombinedPathGenerator(PathGenerator generator) {
		super();
		addPathGenerator(generator);
	}

	public void addPathGenerator(PathGenerator generator) {
		logger.debug("Adding PathGenerator: " + generator);
		generatorList.add(generator);
	}

	public void setMachine(FiniteStateMachine machine) {
		for (Iterator<PathGenerator> i = generatorList.iterator(); i.hasNext();) {
			i.next().setMachine(machine);
		}
	}

	public void setStopCondition(StopCondition stopCondition) {
		for (Iterator<PathGenerator> i = generatorList.iterator(); i.hasNext();) {
			i.next().setStopCondition(stopCondition);
		}
	}

	private PathGenerator getActivePathGenerator() {
		return generatorList.get(currentGenerator);
	}

	private boolean hasPath() {
		return generatorList.size() > currentGenerator;
	}

	private void scrapActivePathGenerator() {
		logger.debug("Removing PathGenerator: " + getActivePathGenerator());
		currentGenerator++;
	}

	public boolean hasNext() {
		boolean nextIsAvailable = false;
		while (hasPath() && !nextIsAvailable) {
			nextIsAvailable = getActivePathGenerator().hasNext();
			if (!nextIsAvailable)
				scrapActivePathGenerator();
		}
		return nextIsAvailable;
	}

	public String[] getNext() throws InterruptedException {
		String[] retur = { "", "" };

		boolean nextIsAvailable = false;
		while (hasPath() && !nextIsAvailable) {
			nextIsAvailable = getActivePathGenerator().hasNext();
			if (!nextIsAvailable)
				scrapActivePathGenerator();
		}
		if (!nextIsAvailable)
			return retur;
		return getActivePathGenerator().getNext();
	}

	public String toString() {
		String retur = "";
		for (Iterator<PathGenerator> i = generatorList.iterator(); i.hasNext();) {
			retur += i.next().toString() + "\n";
		}
		return retur.trim();
	}

}
