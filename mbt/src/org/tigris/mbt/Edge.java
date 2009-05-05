package org.tigris.mbt;

public class Edge extends AbstractElement {
	
	private String guardKey = new String();
	private float weightKey = 0;

	public Edge() {
		super();
	}

	public Edge(Edge edge) {
		super(edge);
		this.guardKey = edge.guardKey;
		this.weightKey = edge.weightKey;
	}

	public float getWeightKey() {
		return weightKey;
	}

	public void setWeightKey(float weightKey) {
		if ( weightKey < 0 || weightKey > 1 )
			throw new RuntimeException("The value of weight, must be between 0 <= weight <= 1");
		this.weightKey = weightKey;
	}

	public String getGuardKey() {
		return guardKey;
	}

	public void setGuardKey(String guardKey) {
		this.guardKey = guardKey;
	}

}
