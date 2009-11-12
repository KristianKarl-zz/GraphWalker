package org.tigris.mbt.io;

import java.io.PrintStream;

import org.tigris.mbt.graph.Graph;

public abstract class AbstractModelHandler {

	protected Graph graph;

	/**
	 * Do not verify labels for edges and vertices. This is used when creating
	 * manual test sequences.
	 */
	private boolean manualTestSequence;

	public boolean isManualTestSequence() {
		return manualTestSequence;
	}

	public void setManualTestSequence(boolean manualTestSequence) {
		this.manualTestSequence = manualTestSequence;
	}

	public abstract void load(String fileName);

	public abstract void save(PrintStream ps);

	public Graph getModel() {
		return graph;
	}

	public void setModel(Graph graph) {
		this.graph = graph;
	}
}
