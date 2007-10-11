package org.tigris.mbt;

import edu.uci.ics.jung.graph.impl.SparseGraph;

public abstract class AbstractModelHandler {

	protected SparseGraph graph;
	
	public abstract void load(String fileName);
	
	public abstract void save(String fileName);
	
	public SparseGraph getModel()
	{
		return graph;
	}
	
	public void setModel(SparseGraph graph)
	{
		this.graph=graph;
	}
}
