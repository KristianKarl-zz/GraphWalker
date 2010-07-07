package org.graphwalker.graph;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphwalker.Keywords;

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

	/**
	 * @param str
	 * @return
	 */
	static public String getLabel( String str ) {
		Pattern p;
		Matcher m;
		String label = "";
		if ( str.split("/").length > 1 ||
				 str.split("\\[").length > 1 ) {
			p = Pattern.compile("^(\\w+)\\s?([^/^\\[]+)?", Pattern.MULTILINE);
		} else {
			p = Pattern.compile("(.*)", Pattern.MULTILINE);			
		}
		m = p.matcher(str);
		if (m.find()) {
			label = m.group(1);
			if (label.length() <= 0) {
				throw new RuntimeException("Vertex is missing mandatory label");
			}
			if (label.matches(".*[\\s].*")) {
				throw new RuntimeException("Label of vertex: '" + label + "', containing whitespaces");
			}
			if (Keywords.isKeyWord(label)) {
				throw new RuntimeException("The label of vertex: '" + label + "', is a reserved keyword");
			}
		} else {
			throw new RuntimeException("Label must be defined for vertex");
		}
		return label;
	}
	
}
