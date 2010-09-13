package org.graphwalker.io;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.graphwalker.Keywords;
import org.graphwalker.Util;
import org.graphwalker.graph.AbstractElement;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * A file reader for GraphML files. The reader can read one single graphml file
 * or several in a single folder. When several files are read, they will be
 * merged. Regardless if one or several files are read, they all end up into one
 * single Graph object.<br>
 * <br>
 * <strong>Example: Single graphml file</strong><br>
 * GraphML graphML = new GraphML();<br>
 * Graph graph = graphML.load( "graph.graphml" );<br>
 * <br>
 * <strong>Example: Folder containing several graphml files</strong><br>
 * GraphML graphML = new GraphML();<br>
 * Graph graph = graphML.load( "/home/user/graphml_folder/" );<br>
 * 
 */
public class GraphML extends AbstractModelHandler {

	/**
	 * A counter for creating unique indexes for edges and vertices.
	 */
	private int vertexAndEdgeIndex;

	/**
	 * The logger
	 */
	private Logger logger;

	/**
	 * Default constructor. Initializes the default logger.
	 */
	public GraphML() {
		logger = Util.setupLogger(GraphML.class);

		models = new Vector<Graph>();
		vertexAndEdgeIndex = 0;
	}

	/**
	 * Reads one single graph, or a folder containing several graphs to be merged
	 * into one graph.
	 * 
	 * @see org.graphwalker.io.AbstractModelHandler#load(java.lang.String)
	 * @param fileOrfolder
	 *          The gramphml file or folder.
	 */
	public void load(String fileOrfolder) {
		if (fileOrfolder != "") {
			File file = new File(fileOrfolder);
			if (file.isFile()) {
				models.add(parseFile(fileOrfolder));
			} else if (file.isDirectory()) {
				// Only accepts files which suffix is .graphml
				FilenameFilter graphmlFilter = new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(".graphml");
					}
				};

				File[] allChildren = file.listFiles(graphmlFilter);
				for (int i = 0; i < allChildren.length; ++i) {
					models.add(parseFile(allChildren[i].getAbsolutePath()));
				}
			} else {
				throw new RuntimeException("'" + fileOrfolder
				    + "' is not a file or a directory. Please specify a valid .graphml file or a directory containing .graphml files");
			}
		}
		activateMainGraph();
	}

	/**
	 * Parses the graphml file, and returns the model as a
	 * edu.uci.ics.jung.graph.impl.Graph
	 * 
	 * @param fileName
	 *          The graphml file
	 * @return The graph
	 */
	@SuppressWarnings("unchecked")
	private Graph parseFile(String fileName) {
		Graph graph = new Graph();
		graph.setFileKey(fileName);
		SAXBuilder parser = new SAXBuilder("org.apache.crimson.parser.XMLReaderImpl", false);

		try {
			logger.debug("Parsing file: " + fileName);
			Document doc = parser.build(fileName);

			// Parse all vertices (nodes)
			Iterator<Object> iter_node = doc.getDescendants(new org.jdom.filter.ElementFilter("node"));
			while (iter_node.hasNext()) {
				Object o = iter_node.next();
				if (o instanceof org.jdom.Element) {
					org.jdom.Element element = (org.jdom.Element) o;
					if (element.getAttributeValue("yfiles.foldertype") != null) {
						logger.debug("  Excluded node: " + element.getAttributeValue("yfiles.foldertype"));
						continue;
					}
					Iterator<Object> iterUMLNoteIter = element.getDescendants(new org.jdom.filter.ElementFilter("UMLNoteNode"));
					if (iterUMLNoteIter.hasNext()) {
						logger.debug("  Excluded node: UMLNoteNode");
						continue;
					}
					logger.debug("  id: " + element.getAttributeValue("id"));

					// Used to remember which vertex to store the image location.
					Vertex currentVertex = null;

					Iterator<Object> iterNodeLabel = element.getDescendants(new org.jdom.filter.ElementFilter("NodeLabel"));
					while (iterNodeLabel.hasNext()) {
						Object o2 = iterNodeLabel.next();
						if (o2 instanceof org.jdom.Element) {
							org.jdom.Element nodeLabel = (org.jdom.Element) o2;
							logger.debug("  Full name: '" + nodeLabel.getQualifiedName() + "'");
							logger.debug("  Name: '" + nodeLabel.getTextTrim() + "'");
							String str = nodeLabel.getTextTrim();

							Vertex v = new Vertex();
							graph.addVertex(v);
							currentVertex = v;

							v.setIdKey(element.getAttributeValue("id"));
							v.setVisitedKey(new Integer(0));
							v.setFileKey(fileName);
							v.setFullLabelKey(str);
							v.setIndexKey(new Integer(getNewVertexAndEdgeIndex()));
							v.setLabelKey(Vertex.getLabel(str));
							v.setMergeKey(AbstractElement.isMerged(str));
							v.setNoMergeKey(AbstractElement.isNoMerge(str));
							v.setBlockedKey(AbstractElement.isBlocked(str));

							Integer index = AbstractElement.getIndex(str);
							if (index != 0) {
								v.setIndexKey(index);
							}

							v.setReqTagKey(AbstractElement.getReqTags(str));
						}
					}

					// Extract any manual test instructions
					Iterator<Object> iterData = element.getDescendants(new org.jdom.filter.ElementFilter("data"));
					while (iterData.hasNext() && currentVertex != null) {
						Object o2 = iterData.next();
						if (o2 instanceof org.jdom.Element) {
							org.jdom.Element data = (org.jdom.Element) o2;
							if (!data.getContent().isEmpty() && data.getContent(0) != null) {
								String text = data.getContent(0).getValue().trim();
								if (!text.isEmpty()) {
									logger.debug("  Data: '" + text + "'");
									currentVertex.setManualInstructions(text);
								}
							}
						}
					}

					// Using the yEd editor, an image can be used to depict the vertex.
					// When merging multiple
					// graphs into one, the code below, stores the image location, which
					// will be used when
					// writing that merged graphml file.
					Iterator<Object> iterImage = element.getDescendants(new org.jdom.filter.ElementFilter("Image"));
					while (iterImage.hasNext() && currentVertex != null) {
						Object o2 = iterImage.next();
						if (o2 instanceof org.jdom.Element) {
							org.jdom.Element image = (org.jdom.Element) o2;
							if (image.getAttributeValue("href") != null) {
								logger.debug("  Image: '" + image.getAttributeValue("href") + "'");
								currentVertex.setImageKey(image.getAttributeValue("href"));
							}
						}
					}
					Iterator<Object> iterGeometry = element.getDescendants(new org.jdom.filter.ElementFilter("Geometry"));
					while (iterGeometry.hasNext() && currentVertex != null) {
						Object o2 = iterGeometry.next();
						if (o2 instanceof org.jdom.Element) {
							org.jdom.Element geometry = (org.jdom.Element) o2;
							logger.debug("  width: '" + geometry.getAttributeValue("width") + "'");
							logger.debug("  height: '" + geometry.getAttributeValue("height") + "'");
							logger.debug("  x position: '" + geometry.getAttributeValue("x") + "'");
							logger.debug("  y position: '" + geometry.getAttributeValue("y") + "'");
							currentVertex.setWidth(Float.parseFloat(geometry.getAttributeValue("width")));
							currentVertex.setHeight(Float.parseFloat(geometry.getAttributeValue("height")));
							currentVertex.setLocation(new Point2D.Float(Float.parseFloat(geometry.getAttributeValue("x")), Float.parseFloat(geometry
							    .getAttributeValue("y"))));
						}
					}
					Iterator<Object> iterFill = element.getDescendants(new org.jdom.filter.ElementFilter("Fill"));
					while (iterFill.hasNext() && currentVertex != null) {
						Object o2 = iterFill.next();
						if (o2 instanceof org.jdom.Element) {
							org.jdom.Element fill = (org.jdom.Element) o2;
							logger.debug("  fill color: '" + fill.getAttributeValue("color") + "'");
							currentVertex.setFillColor(new Color(Integer.parseInt(fill.getAttributeValue("color").replace("#", ""), 16)));
						}
					}
				}
			}

			Object[] vertices = graph.getVertices().toArray();

			// Parse all edges (arrows or transitions)
			Iterator<Object> iter_edge = doc.getDescendants(new org.jdom.filter.ElementFilter("edge"));
			while (iter_edge.hasNext()) {
				Object o = iter_edge.next();
				if (o instanceof org.jdom.Element) {
					org.jdom.Element element = (org.jdom.Element) o;
					logger.debug("  id: " + element.getAttributeValue("id"));

					Iterator<Object> iter2 = element.getDescendants(new org.jdom.filter.ElementFilter("EdgeLabel"));
					org.jdom.Element edgeLabel = null;
					if (iter2.hasNext()) {
						Object o2 = iter2.next();
						if (o2 instanceof org.jdom.Element) {
							edgeLabel = (org.jdom.Element) o2;
							logger.debug("  Full name: '" + edgeLabel.getQualifiedName() + "'");
							logger.debug("  Name: '" + edgeLabel.getTextTrim() + "'");
						}
					}
					logger.debug("  source: " + element.getAttributeValue("source"));
					logger.debug("  target: " + element.getAttributeValue("target"));

					Vertex source = null;
					Vertex dest = null;

					for (int i = 0; i < vertices.length; i++) {
						Vertex vertex = (Vertex) vertices[i];

						// Find source vertex
						if (vertex.getIdKey().equals(element.getAttributeValue("source")) && vertex.getFileKey().equals(fileName)) {
							source = vertex;
						}
						if (vertex.getIdKey().equals(element.getAttributeValue("target")) && vertex.getFileKey().equals(fileName)) {
							dest = vertex;
						}
					}
					if (source == null) {
						String msg = "Could not find starting node for edge. Name: '" + element.getAttributeValue("source") + "' In file '" + fileName
						    + "'";
						logger.error(msg);
						throw new RuntimeException(msg);
					}
					if (dest == null) {
						String msg = "Could not find end node for edge. Name: '" + element.getAttributeValue("target") + "' In file '" + fileName + "'";
						logger.error(msg);
						throw new RuntimeException(msg);
					}

					Edge e = new Edge();
					e.setIdKey(element.getAttributeValue("id"));
					e.setFileKey(fileName);
					e.setIndexKey(new Integer(getNewVertexAndEdgeIndex()));
					if (!graph.addEdge(e, source, dest)) {
						String msg = "Failed adding edge: " + e + ", to graph: " + graph;
						logger.error(msg);
						throw new RuntimeException(msg);
					}

					if (edgeLabel != null) {
						// The label of an edge has the following format:
						// Label Parameter [Guard] / Action1;Action2;ActionN;
						// Keyword
						// Where the Label, Parameter. Guard, Actions and Keyword are
						// optional.

						String str = edgeLabel.getText();

						e.setFullLabelKey(str);
						String[] guardAndAction = Edge.getGuardAndActions(str);
						String[] labelAndParameter = Edge.getLabelAndParameter(str);
						e.setGuardKey(guardAndAction[0]);
						e.setActionsKey(guardAndAction[1]);
						e.setLabelKey(labelAndParameter[0]);
						e.setParameterKey(labelAndParameter[1]);
						e.setWeightKey(Edge.getWeight(str));
						e.setBlockedKey(AbstractElement.isBlocked(str));
						e.setBacktrackKey(Edge.isBacktrack(str));

						Integer index = AbstractElement.getIndex(str);
						if (index != 0) {
							e.setIndexKey(index);
						}

						e.setReqTagKey(AbstractElement.getReqTags(str));
					}
					e.setVisitedKey(new Integer(0));
					logger.debug("  Added edge: '" + e.getLabelKey() + "', with id: " + e.getIndexKey());

					// Extract any manual test instructions
					Iterator<Object> iterData = element.getDescendants(new org.jdom.filter.ElementFilter("data"));
					while (iterData.hasNext() && e != null) {
						Object o2 = iterData.next();
						if (o2 instanceof org.jdom.Element) {
							org.jdom.Element data = (org.jdom.Element) o2;
							if (!data.getContent().isEmpty() && data.getContent(0) != null) {
								String text = data.getContent(0).getValue().trim();
								if (!text.isEmpty()) {
									logger.debug("  Data: '" + text + "'");
									e.setManualInstructions(text);
								}
							}
						}
					}
				}
			}
		} catch (RuntimeException e) {
			throw new RuntimeException("Could not parse file: '" + fileName + "'. " + e.getMessage());
		} catch (JDOMException e) {
			throw new RuntimeException("Could not parse file: '" + fileName + "'. " + e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException("Could not parse file: '" + fileName + "'. " + e.getMessage());
		}

		removeBlockedEntities(graph);
		setGraphLabel(graph);

		return graph;
	}

	/**
	 * Sets the label for the graph.
	 * The method searches for the START vertex, and uses the destination vertex name of
	 * the single out-edge to set the name of the graph.
	 * @param graph The graph for which the label key is to be set
	 */
	private void setGraphLabel(Graph graph) {
		for (Vertex v : graph.getVertices()) {
			if (v.getLabelKey().equalsIgnoreCase(Keywords.START_NODE)) {
				if (graph.getOutEdges(v).size() != 1) {
					throw new RuntimeException("The START vertex must have 1 out-edge. No more, no less.");
				}
				for (Edge e : graph.getOutEdges(v)) {					
					graph.setLabelKey(graph.getDest(e).getLabelKey());
					graph.setMainGraph(e.getLabelKey().isEmpty() == false);
					return;
				}
			}
		}
		throw new RuntimeException("Did not find a START vertex.");
	}

	/**
	 * Increment and return the unique index for a vertex or edge.
	 */
	private int getNewVertexAndEdgeIndex() {
		return ++vertexAndEdgeIndex;
	}

	/**
	 * Removes any edges, and any vertices that contains the key word BLOCKED
	 */
	private void removeBlockedEntities(Graph graph) {
		logger.debug("Finished parsing graph: " + graph);
		Object[] vertices = graph.getVertices().toArray();
		for (int i = 0; i < vertices.length; i++) {
			Vertex v = (Vertex) vertices[i];
			if (v.isBlockedKey()) {
				logger.debug("Removing this vertex because it is BLOCKED: '" + v.getLabelKey() + "'");
				graph.removeVertex(v);
			}
		}
		Object[] edges = graph.getEdges().toArray();
		for (int i = 0; i < edges.length; i++) {
			Edge e = (Edge) edges[i];
			if (e.isBlockedKey()) {
				logger.debug("Removing this edge because it is BLOCKED: '" + e.getLabelKey() + "'");
				graph.removeEdge(e);
			}
		}
		logger.debug("Graph after removing BLOCKED entities: " + graph);
	}

	/**
	 * Writes the graph to a PrintStream, using GraphML format.
	 */
	public void save(PrintStream ps) {
		Graph g = getActiveModel();

		ps.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		ps.println("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\"  "
		    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
		    + "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml "
		    + "http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd\" " + "xmlns:y=\"http://www.yworks.com/xml/graphml\">");
		ps.println("  <key id=\"d0\" for=\"node\" yfiles.type=\"nodegraphics\"/>");
		ps.println("  <key id=\"d1\" for=\"edge\" yfiles.type=\"edgegraphics\"/>");
		ps.println("  <graph id=\"G\" edgedefault=\"directed\">");

		for (Iterator<Vertex> vertexIterator = g.getVertices().iterator(); vertexIterator.hasNext();) {
			Vertex v = vertexIterator.next();

			ps.println("    <node id=\"n" + v.getIndexKey() + "\">");
			ps.println("      <data key=\"d0\" >");

			if (!v.getImageKey().isEmpty()) {
				ps.println("        <y:ImageNode >");
				ps.println("          <y:Geometry  x=\"241.875\" y=\"158.701171875\" width=\"" + v.getWidth() + "\" height=\"" + v.getHeight()
				    + "\"/>");
			} else {
				ps.println("        <y:ShapeNode >");
				ps.println("          <y:Geometry  x=\"241.875\" y=\"158.701171875\" width=\"95.0\" height=\"30.0\"/>");
			}

			ps.println("          <y:Fill color=\"#CCCCFF\"  transparent=\"false\"/>");
			ps.println("          <y:BorderStyle type=\"line\" width=\"1.0\" color=\"#000000\" />");
			ps.println("          <y:NodeLabel x=\"1.5\" y=\"5.6494140625\" width=\"92.0\" height=\"18.701171875\" "
			    + "visible=\"true\" alignment=\"center\" fontFamily=\"Dialog\" fontSize=\"12\" "
			    + "fontStyle=\"plain\" textColor=\"#000000\" modelName=\"internal\" modelPosition=\"c\" " + "autoSizePolicy=\"content\">"
			    + v.getFullLabelKey() + "&#xA;INDEX=" + v.getIndexKey() + "</y:NodeLabel>");

			if (!v.getImageKey().isEmpty()) {
				ps.println("          <y:Image href=\"" + v.getImageKey() + "\"/>");
				ps.println("        </y:ImageNode>");
			} else {
				ps.println("          <y:Shape type=\"rectangle\"/>");
				ps.println("        </y:ShapeNode>");
			}

			ps.println("      </data>");
			ps.println("    </node>");
		}

		for (Iterator<Edge> edgeIterator = g.getEdges().iterator(); edgeIterator.hasNext();) {
			Edge e = edgeIterator.next();
			Pair<Vertex> p = g.getEndpoints(e);
			Vertex src = p.getFirst();
			Vertex dest = p.getSecond();

			ps.println("    <edge id=\"" + e.getIndexKey() + "\" source=\"n" + src.getIndexKey() + "\" target=\"n" + dest.getIndexKey() + "\">");
			ps.println("      <data key=\"d1\" >");
			ps.println("        <y:PolyLineEdge >");
			ps.println("          <y:Path sx=\"-23.75\" sy=\"15.0\" tx=\"-23.75\" ty=\"-15.0\">");
			ps.println("            <y:Point x=\"273.3125\" y=\"95.0\"/>");
			ps.println("            <y:Point x=\"209.5625\" y=\"95.0\"/>");
			ps.println("            <y:Point x=\"209.5625\" y=\"143.701171875\"/>");
			ps.println("            <y:Point x=\"265.625\" y=\"143.701171875\"/>");
			ps.println("          </y:Path>");
			ps.println("          <y:LineStyle type=\"line\" width=\"1.0\" color=\"#000000\" />");
			ps.println("          <y:Arrows source=\"none\" target=\"standard\"/>");

			if (!e.getFullLabelKey().isEmpty()) {
				String label = e.getFullLabelKey();
				label = label.replaceAll("&", "&amp;");
				label = label.replaceAll("<", "&lt;");
				label = label.replaceAll(">", "&gt;");
				label = label.replaceAll("'", "&apos;");
				label = label.replaceAll("\"", "&quot;");

				ps.println("          <y:EdgeLabel x=\"-148.25\" y=\"30.000000000000014\" width=\"169.0\" height=\"18.701171875\" "
				    + "visible=\"true\" alignment=\"center\" fontFamily=\"Dialog\" fontSize=\"12\" "
				    + "fontStyle=\"plain\" textColor=\"#000000\" modelName=\"free\" modelPosition=\"anywhere\" "
				    + "preferredPlacement=\"on_edge\" distance=\"2.0\" ratio=\"0.5\">" + label + "&#xA;INDEX=" + e.getIndexKey() + "</y:EdgeLabel>");
			}

			ps.println("          <y:BendStyle smoothed=\"false\"/>");
			ps.println("        </y:PolyLineEdge>");
			ps.println("      </data>");
			ps.println("    </edge>");

		}

		ps.println("  </graph>");
		ps.println("</graphml>");

	}
}
