package org.tigris.mbt.graph;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Vertex extends AbstractElement {

	private String motherStartVertexKey = new String();
	private String subGraphStartVertexKey = new String();
	private Color fillColor = new Color(0);
	private Point2D location = new Point2D.Float();
	private float width = 0;
	private float height = 0;

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void setLocation(Point2D location) {
		this.location = location;
	}

	public Point2D getLocation() {
		return location;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public Vertex() {
		super();
	}

	public Vertex(Vertex vertex) {
		super(vertex);
		this.motherStartVertexKey = vertex.motherStartVertexKey;
		this.subGraphStartVertexKey = vertex.subGraphStartVertexKey;
		this.fillColor = vertex.fillColor;
		this.location = vertex.location;
		this.width = vertex.width;
		this.height = vertex.height;
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
