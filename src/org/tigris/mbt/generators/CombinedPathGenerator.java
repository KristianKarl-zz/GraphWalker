package org.tigris.mbt.generators;

import java.util.Stack;

import org.apache.log4j.Logger;

public class CombinedPathGenerator extends PathGenerator {

	static Logger logger = Logger.getLogger(CombinedPathGenerator.class);

	private Stack paths = new Stack();

	public CombinedPathGenerator(PathGenerator generator) {
		super(null);
		addPathGenerator(generator);
	}
	
	public void addPathGenerator(PathGenerator generator)
	{
		logger.debug("Adding PathGenerator: " + generator);
		paths.add(generator);
	}

	private PathGenerator getActivePathGenerator()
	{
		return (PathGenerator) paths.peek();
	}

	private boolean hasPath()
	{
		return paths.size() > 0;
	}
	
	private void scrapActivePathGenerator()
	{
		logger.debug("Removing PathGenerator: " + getActivePathGenerator());
		paths.pop();
	}
	
	public boolean hasNext() 
	{
		boolean nextIsAvailable = false;
		while(hasPath() && !nextIsAvailable)
		{
			nextIsAvailable = getActivePathGenerator().hasNext();
			if(!nextIsAvailable) scrapActivePathGenerator();
		}
		return nextIsAvailable;
	}
	
	public String[] getNext() {
		String[] retur = {"",""};

		boolean nextIsAvailable = false;
		while(hasPath() && !nextIsAvailable)
		{
			nextIsAvailable = getActivePathGenerator().hasNext();
			if(!nextIsAvailable) scrapActivePathGenerator();
		}
		if(!nextIsAvailable) return retur;
		return getActivePathGenerator().getNext();
	}
}
