package org.tigris.mbt;

public class Vertex extends AbstractElement {
	
	private String motherStartVertexKey = new String();
	private String subGraphStartVertexKey = new String();

	public Vertex() {
		super();
	}

	public Vertex(Vertex vertex) {
		super(vertex);
		this.motherStartVertexKey = vertex.motherStartVertexKey;
		this.subGraphStartVertexKey = vertex.subGraphStartVertexKey;
	}

	public String getSubGraphStartVertexKey() {
		return subGraphStartVertexKey;
	}

	public void setSubGraphStartVertexKey(String subGraphStartVertexKey) {
		this.subGraphStartVertexKey = subGraphStartVertexKey;
	}

	public String getMotherStartVertexKey() {
		return motherStartVertexKey;
	}

	public void setMotherStartVertexKey(String motherStartVertexKey) {
		this.motherStartVertexKey = motherStartVertexKey;
	}

}
