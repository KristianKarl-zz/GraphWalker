package org.tigris.mbt.io;

import java.io.PrintStream;

import edu.uci.ics.jung.graph.impl.SparseGraph;

public abstract class AbstractModelHandler {

	protected SparseGraph graph;
	
	public abstract void load(String fileName);
	
	public abstract void save(PrintStream ps);
	
	public SparseGraph getModel()
	{
		return graph;
	}
	
	public void setModel(SparseGraph graph)
	{
		this.graph=graph;
	}
}
