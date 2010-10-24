package org.graphwalker.io;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
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
	 * Indicator if the graph list needs to be merged
	 */
	private boolean merged;

	/**
	 * List of parsed graphs
	 */
	private Vector<Graph> parsedGraphList;

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

		parsedGraphList = new Vector<Graph>();
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
			File file = Util.getFile(fileOrfolder);
			if (file.isFile()) {
				parsedGraphList.add(parseFile(fileOrfolder));
				setMerged(false);
			} else if (file.isDirectory()) {
				// Only accepts files which suffix is .graphml
				FilenameFilter graphmlFilter = new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(".graphml");
					}
				};

				File[] allChildren = file.listFiles(graphmlFilter);
				for (int i = 0; i < allChildren.length; ++i) {
					parsedGraphList.add(parseFile(allChildren[i].getAbsolutePath()));
					setMerged(false);
				}
			} else {
				throw new RuntimeException("'" + fileOrfolder
				    + "' is not a file or a directory. Please specify a valid .graphml file or a directory containing .graphml files");
			}
		}
		mergeAllGraphs();
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
		SAXBuilder parser = new SAXBuilder();

		try {
			logger.debug("Parsing file: " + fileName);
			Document doc = parser.build(Util.getFile(fileName));

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
							if  ( index != 0 ) {
								v.setIndexKey( index );
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
								if ( !text.isEmpty() ) {
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
						String[] guardAndAction =  Edge.getGuardAndActions(str);
						String[] labelAndParameter =  Edge.getLabelAndParameter(str);
						e.setGuardKey(guardAndAction[0]);
						e.setActionsKey(guardAndAction[1]);
						e.setLabelKey(labelAndParameter[0]);
						e.setParameterKey(labelAndParameter[1]);
						e.setWeightKey(Edge.getWeight(str));
						e.setBlockedKey(AbstractElement.isBlocked(str));
						
						Integer index = AbstractElement.getIndex(str);
						if  ( index != 0 ) {
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
								if ( !text.isEmpty() ) {
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

		logger.debug("Finished parsing graph: " + graph);
		removeBlockedEntities(graph);
		logger.debug("Graph after removing BLOCKED entities: " + graph);

		return graph;
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
	}

	/**
	 * Merge all file graphs into one graph.
	 */
	private void mergeAllGraphs() {
		if (!isMerged()) {
			findMotherAndSubgraphs();
			checkForDuplicateVerticesInSubgraphs();
			mergeSubgraphs();
			mergeVerticesMarked_MERGE();
			checkForVerticesWithZeroInEdges();

			logger.info("Done merging");
			setMerged(true);
		}
		setGraphName();
	}

	private void setGraphName() {
		for (Vertex v : graph.getVertices()) {
			if ( v.getLabelKey().equalsIgnoreCase(Keywords.START_NODE)) {
				Edge e = (Edge)graph.getOutEdges(v).toArray()[0];
				graph.setLabelKey(graph.getDest(e).getLabelKey());
			}	    
    }
  }

	private void setMerged(boolean merged) {
		this.merged = merged;
	}

	private boolean isMerged() {
		return this.merged;
	}

	/**
	 * Search for the mother graph, and all subgraphs
	 */
	private void findMotherAndSubgraphs() {
		boolean foundMotherStartGraph = false;
		boolean foundSubStartGraph = false;
		graph = null;

		for (Iterator<Graph> iter = parsedGraphList.iterator(); iter.hasNext();) {
			Graph g = iter.next();
			foundSubStartGraph = false;

			logger.debug("Analyzing graph: " + g.getFileKey());

			Object[] vertices = g.getVertices().toArray();
			for (int i = 0; i < vertices.length; i++) {
				Vertex v = (Vertex) vertices[i];

				// Find all vertices that are start nodes (START_NODE)
				if (v.getLabelKey().equalsIgnoreCase(Keywords.START_NODE)) {
					if (g.getOutEdges(v).size() != 1) {
						throw new RuntimeException("A Start vertex can only have one out edge, look in file: " + g.getFileKey());
					}
					Edge edge = (Edge) g.getOutEdges(v).toArray()[0];
					if (!edge.getLabelKey().isEmpty()) {
						if (foundMotherStartGraph) {
							if (graph.getFileKey().equals(g.getFileKey())) {
								throw new RuntimeException("Only one Start vertex can exist in one file, see file '" + graph.getFileKey() + "'");
							} else {
								throw new RuntimeException("Only one Start vertex can exist in one file, see files " + graph.getFileKey() + ", and "
								    + g.getFileKey());
							}
						}
						if (foundSubStartGraph == true) {
							throw new RuntimeException("Only one Start vertex can exist in one file, see file '" + g.getFileKey() + "'");
						}

						foundMotherStartGraph = true;
						graph = g;
						g.getDest(edge).setMotherStartVertexKey(Keywords.MOTHER_GRAPH_START_VERTEX);
						logger.debug("Found the mother graph in the file: " + graph.getFileKey());
					} else {
						if (foundSubStartGraph == true) {
							throw new RuntimeException("Only one Start vertex can exist in one file, see file '" + g.getFileKey() + "'");
						}

						// Verify that current subgraph is not already defined
						for (Iterator<Graph> iter_g = parsedGraphList.iterator(); iter_g.hasNext();) {
							if (iter.hashCode() == iter_g.hashCode()) {
								continue;
							}

							Graph tmp_graph = iter_g.next();
							if (!tmp_graph.getLabelKey().isEmpty()) {
								String name = tmp_graph.getLabelKey();
								if (name.equals(g.getDest(edge).getLabelKey())) {
									throw new RuntimeException("Found 2 subgraphs using the same name: '" + g.getDest(edge).getLabelKey()
									    + "', they are defined in files: '" + g.getFileKey() + "', and :'" + tmp_graph.getFileKey() + "'");
								}
							}
						}

						if (foundMotherStartGraph == true) {
							if (graph.getFileKey().equals(g.getFileKey())) {
								throw new RuntimeException("Only one Start vertex can exist in one file, see file '" + graph.getFileKey() + "'");
							}
						}

						// Since the edge does not contain a label, this is a subgraph
						// Mark the destination node of the edge to a subgraph starting node
						foundSubStartGraph = true;
						g.getDest(edge).setSubGraphStartVertexKey(Keywords.SUBGRAPH_START_VERTEX);
						g.setLabelKey(g.getDest(edge).getLabelKey());
						logger.debug("Found sub-graph: '" + g.getLabelKey() + "', in file '" + g.getFileKey() + "'");
						logger.debug("Added SUBGRAPH_START_VERTEX to vertex: " + g.getDest(edge).getIndexKey());
					}
				}
			}
		}

		if (graph == null) {
			throw new RuntimeException("Did not find a Start vertex with an out edge with a label.");
		}
	}

	/**
	 * Look for duplicated vertices in each sub-graph. If a vertex is found, which
	 * represents the name of the sub-graph (the vertex which the Start vertex
	 * points to) is duplicated in the same sub-graph, this will lead to an
	 * infinite recursive loop.
	 */
	private void checkForDuplicateVerticesInSubgraphs() {
		for (int i = 0; i < parsedGraphList.size(); i++) {
			Graph g = (Graph) parsedGraphList.elementAt(i);

			// Exclude the mother graph
			if (graph.hashCode() == g.hashCode()) {
				continue;
			}

			logger.debug("Looking for infinit recursive loop in file: " + g.getFileKey());

			String subgraph_label = (String) g.getLabelKey();
			Object[] vertices = g.getVertices().toArray();
			for (int j = 0; j < vertices.length; j++) {
				Vertex v = (Vertex) vertices[j];
				String label = (String) v.getLabelKey();
				if (label.equals(subgraph_label)) {
					if (!v.getSubGraphStartVertexKey().isEmpty()) {
						continue;
					}
					if (v.isNoMergeKey()) {
						continue;
					}

					logger.error("Vertex: " + label + ", with id: " + v.getIndexKey() + ", is a duplicate in a subgraph");
					throw new RuntimeException("Found a subgraph containing a duplicate vertex with name: '" + v.getLabelKey() + "', in file: '"
					    + g.getFileKey() + "'");

				}
			}
			logger.debug("Nope! Did not find any infinit recursive loops.");
		}
	}

	private void mergeSubgraphs() {
		for (int i = 0; i < parsedGraphList.size(); i++) {
			Graph g = (Graph) parsedGraphList.elementAt(i);

			if (graph.hashCode() == g.hashCode()) {
				continue;
			}
			logger.debug("Analysing graph in file: " + g.getFileKey());

			Object[] vertices = graph.getVertices().toArray();
			for (int j = 0; j < vertices.length; j++) {
				Vertex v1 = (Vertex) vertices[j];
				logger.debug("Investigating vertex(" + v1.getIndexKey() + "): '" + v1.getLabelKey() + "'");

				if (v1.getLabelKey().equals(g.getLabelKey())) {
					if (v1.isMergeKey()) {
						logger.debug("The vertex is marked MERGE, and will not be replaced by a subgraph.");
						continue;
					}
					if (v1.isNoMergeKey()) {
						logger.debug("The vertex is marked NO_MERGE, and will not be replaced by a subgraph.");
						continue;
					}
					if (v1.isMergedMbtKey()) {
						logger.debug("The vertex is marked MERGED_BY_MBT, and will not be replaced by a subgraph.");
						continue;
					}

					logger.debug("A subgraph'ed vertex: '" + v1.getLabelKey() + "' in graph: " + g.getFileKey()
					    + ", equals a node in the graph in file: '" + graph.getFileKey() + "'");

					appendGraph(graph, g);
					copySubGraphs(graph, g, v1);

					vertices = graph.getVertices().toArray();
					i = -1;
					j = -1;
				}
			}
		}
	}

	/**
	 * Merge all vertices marked MERGE
	 */
	private void mergeVerticesMarked_MERGE() {
		Object[] list1 = graph.getVertices().toArray();
		for (int i = 0; i < list1.length; i++) {
			Vertex v1 = (Vertex) list1[i];

			if (v1.isMergeKey() == false) {
				continue;
			}

			Object[] list2 = graph.getVertices().toArray();
			Vector<Vertex> mergedVertices = new Vector<Vertex>();
			for (int j = 0; j < list2.length; j++) {
				Vertex v2 = (Vertex) list2[j];

				if (v1.getLabelKey().equals(v2.getLabelKey()) == false) {
					continue;
				}
				if (v2.isNoMergeKey()) {
					continue;
				}
				if (v1.getIndexKey() == v2.getIndexKey()) {
					continue;
				}
				if (mergedVertices.contains(v1)) {
					continue;
				}

				logger.debug("Merging vertex(" + v1.getIndexKey() + "): '" + v1.getLabelKey() + "' with vertex (" + v2.getIndexKey() + ")");

				Object[] inEdges = graph.getInEdges(v1).toArray();
				for (int x = 0; x < inEdges.length; x++) {
					Edge edge = (Edge) inEdges[x];
					Edge new_edge = new Edge(edge);
					new_edge.setIndexKey(new Integer(getNewVertexAndEdgeIndex()));
					graph.addEdge(new_edge, graph.getSource(edge), v2);
				}
				Object[] outEdges = graph.getOutEdges(v1).toArray();
				for (int x = 0; x < outEdges.length; x++) {
					Edge edge = (Edge) outEdges[x];
					Edge new_edge = new Edge(edge);
					new_edge.setIndexKey(new Integer(getNewVertexAndEdgeIndex()));
					graph.addEdge(new_edge, v2, graph.getDest(edge));
				}
				mergedVertices.add(v1);
			}

			if (mergedVertices.isEmpty() == false) {
				logger.debug("Remvoing merged vertex(" + v1.getIndexKey() + ")");
				graph.removeVertex(v1);
			}
		}
	}

	/**
	 * Search for any vertices with no in edges
	 */
	private void checkForVerticesWithZeroInEdges() throws RuntimeException {
		Object[] vs = graph.getVertices().toArray();
		for (int i = 0; i < vs.length; i++) {
			Vertex v = (Vertex) vs[i];
			if (!v.getLabelKey().equalsIgnoreCase(Keywords.START_NODE)) {
				if (graph.getInEdges(v).toArray().length == 0) {
					String msg = "No in-edges! " + v + " is not reachable," + " from file: '" + v.getFileKey() + "'";
					logger.error(msg);
					throw new RuntimeException(msg);
				}
			}
		}
	}

	/**
	 * Copies the graph src, into the graph dst
	 * 
	 * @param dst
	 * @param src
	 */
	private void appendGraph(Graph dst, Graph src) {
		HashMap<Integer, Vertex> map = new HashMap<Integer, Vertex>();
		Object[] vertices = src.getVertices().toArray();
		for (int i = 0; i < vertices.length; i++) {
			Vertex v = (Vertex) vertices[i];
			if (v.getLabelKey().equalsIgnoreCase(Keywords.START_NODE)) {
				continue;
			}
			Vertex new_v = new Vertex(v);
			new_v.setIndexKey(new Integer(getNewVertexAndEdgeIndex()));
			dst.addVertex(new_v);
			logger.debug("Associated vertex: " + v + " to new vertex: " + new_v);
			map.put((Integer) v.getIndexKey(), new_v);
		}
		Object[] edges = src.getEdges().toArray();
		for (int i = 0; i < edges.length; i++) {
			Edge e = (Edge) edges[i];
			Vertex v1 = map.get(src.getSource(e).getIndexKey());
			Vertex v2 = map.get(src.getDest(e).getIndexKey());
			if (v1 == null || v2 == null) {
				continue;
			}
			Edge new_e = new Edge(e);
			dst.addEdge(new_e, v1, v2);
			new_e.setIndexKey(new Integer(getNewVertexAndEdgeIndex()));
		}
	}

	/**
	 * Replaces the vertex targetVertex and all its in and out edges, in the graph
	 * g, with all other vertices with the same name.
	 * 
	 * @param mainGraph
	 * @param subGraph
	 * @param targetVertex
	 */
	private void copySubGraphs(Graph mainGraph, Graph subGraph, Vertex targetVertex) {
		// Save the target vertex out-edge list
		Vector<Edge> targetVertexOutEdgeList = new Vector<Edge>();
		logger.debug("Target vertex (" + targetVertex + ") out-edge list");
		for (Iterator<Edge> iter = mainGraph.getOutEdges(targetVertex).iterator(); iter.hasNext();) {
			Edge element = iter.next();
			logger.debug("  " + element);
			targetVertexOutEdgeList.add(element);
		}

		Vertex sourceVertex = null;
		Object[] vertices = mainGraph.getVertices().toArray();
		for (int i = 0; i < vertices.length; i++) {
			Vertex v = (Vertex) vertices[i];
			if (v.getLabelKey().equals(targetVertex.getLabelKey())) {
				if (v.getSubGraphStartVertexKey().isEmpty()) {
					continue;
				}
				if (v.isMergeKey()) {
					continue;
				}
				if (v.isNoMergeKey()) {
					continue;
				}
				if (v.isMergedMbtKey()) {
					continue;
				}
				if (v.getIndexKey() == targetVertex.getIndexKey()) {
					continue;
				}

				sourceVertex = v;
				break;
			}
		}

		if (sourceVertex == null) {
			return;
		}

		logger.debug("Start merging target vertex: " + targetVertex + " with source vertex: " + sourceVertex);

		Object[] inEdges = mainGraph.getInEdges(sourceVertex).toArray();
		for (int i = 0; i < inEdges.length; i++) {
			Edge edge = (Edge) inEdges[i];
			Edge new_edge = new Edge(edge);
			mainGraph.addEdge(new_edge, mainGraph.getSource(edge), targetVertex);
			new_edge.setIndexKey(new Integer(getNewVertexAndEdgeIndex()));
		}
		Object[] outEdges = mainGraph.getOutEdges(sourceVertex).toArray();
		for (int i = 0; i < outEdges.length; i++) {
			Edge edge = (Edge) outEdges[i];
			Edge new_edge = new Edge(edge);
			mainGraph.addEdge(new_edge, targetVertex, mainGraph.getDest(edge));
			new_edge.setIndexKey(new Integer(getNewVertexAndEdgeIndex()));
		}
		logger.debug("Remvoing source vertex: " + sourceVertex);
		mainGraph.removeVertex(sourceVertex);
		targetVertex.setMergedMbtKey(true);

		// Check if there exists a Stop vertex.
		// Also check if there is only one.
		Vertex stopVertex = null;
		vertices = mainGraph.getVertices().toArray();
		for (int i = 0; i < vertices.length; i++) {
			Vertex v = (Vertex) vertices[i];
			if (v.getLabelKey().equalsIgnoreCase(Keywords.STOP_NODE)) {
				if (stopVertex != null) {
					throw new RuntimeException("Found more than 1 Stop vertex in file (Only one Stop vertex per file is allowed): '"
					    + mainGraph.getFileKey() + "'");
				}
				stopVertex = v;
			}
		}

		// All edges going to the Stop vertex, needs to be merged to the destination
		// vertex.
		// The destination vertex, is pointed to by the vertex which is expanded by
		// the sub graph.
		if (stopVertex != null) {
			Vector<Edge> edgesToBeRemoved = new Vector<Edge>();
			inEdges = mainGraph.getInEdges(stopVertex).toArray();

			logger.debug("Stop vertex in-edge list");
			for (Iterator<Edge> iter = mainGraph.getInEdges(stopVertex).iterator(); iter.hasNext();) {
				Edge element = iter.next();
				logger.debug("  " + element);
			}
			logger.debug("Target vertex (" + targetVertex + ") out-edge list");
			for (Iterator<Edge> iter = targetVertexOutEdgeList.iterator(); iter.hasNext();) {
				Edge element = iter.next();
				logger.debug("  " + element);
			}

			Vector<Pair<Edge>> mergeList = MergeList(targetVertexOutEdgeList.toArray(), inEdges);
			for (Iterator<Pair<Edge>> iterator = mergeList.iterator(); iterator.hasNext();) {
				Pair<Edge> pair = iterator.next();
				MergeOutEdgeAndInEdge((Edge) pair.getFirst(), (Edge) pair.getSecond(), edgesToBeRemoved, mainGraph);
			}

			// Now remove the edges that has been copied.
			Object[] list = edgesToBeRemoved.toArray();
			for (int i = 0; i < list.length; i++) {
				Edge element = (Edge) list[i];
				if (mainGraph.containsEdge(element)) {
					try {
						logger.debug("Removing edge: " + element);
						logger.debug(element + ", was found and removed from graph,: '" + mainGraph.getFileKey() + "'");
						mainGraph.removeEdge(element);
					} catch (java.lang.IllegalArgumentException e) {
						logger.debug(element + ", was not found in graph: '" + mainGraph.getFileKey()
						    + "', this is ok, since it probably been removed before. (I know, not ver good progamming practice here)");
					}
				}
			}
			logger.debug("Removing the Stop vertex: " + stopVertex.getIndexKey());
			mainGraph.removeVertex(stopVertex);
		}
	}

	private Vector<Pair<Edge>> MergeList(Object[] array_A, Object[] array_B) {
		logger.debug("Vector twoLists( Object[] array_A, Object[] array_B )");
		Vector<Pair<Edge>> matches = new Vector<Pair<Edge>>();
		logger.debug("  Looking for exact matches");
		for (int i = 0; i < array_A.length; i++) {
			Edge a = (Edge) array_A[i];
			String aLabel = (String) a.getLabelKey();
			for (int j = 0; j < array_B.length; j++) {
				Edge b = (Edge) array_B[j];
				String bLabel = (String) b.getLabelKey();
				if (aLabel != null && aLabel.length() == 0) {
					aLabel = null;
				}
				if (bLabel != null && bLabel.length() == 0) {
					bLabel = null;
				}
				if (aLabel == null && bLabel == null) {
					logger.debug("    adding: " + a + " and " + b);
					matches.add(new Pair<Edge>(a, b));
				} else if (aLabel != null && bLabel != null) {
					if (aLabel.equals(bLabel)) {
						logger.debug("    adding: " + a + " and " + b);
						matches.add(new Pair<Edge>(a, b));
					}
				}
			}
		}

		Vector<Pair<Edge>> null_matches_from_A_list = new Vector<Pair<Edge>>();
		logger.debug("  Matching nulls from the A list with non-matched items in the second list");
		for (int i = 0; i < array_A.length; i++) {
			Edge a = (Edge) array_A[i];
			String aLabel = (String) a.getLabelKey();
			if (aLabel == null || aLabel.length() == 0) {
				for (int j = 0; j < array_B.length; j++) {
					Edge b = (Edge) array_B[j];
					String bLabel = (String) b.getLabelKey();
					if (bLabel != null) {
							boolean alreadyMatched = false;
						for (Iterator<Pair<Edge>> iter = matches.iterator(); iter.hasNext();) {
							Pair<Edge> element = iter.next();
							if (b.equals(element.getSecond())) {
								alreadyMatched = true;
								break;
							}
						}

						if (alreadyMatched == false) {
							logger.debug("    adding: " + a + " and " + b);
							null_matches_from_A_list.add(new Pair<Edge>(a, b));
						}
					}
				}
			}
		}

		Vector<Pair<Edge>> null_matches_from_B_list = new Vector<Pair<Edge>>();
		logger.debug("  Matching nulls from the B list with non-matched items in the first list");
		for (int i = 0; i < array_B.length; i++) {
			Edge b = (Edge) array_B[i];
			String bLabel = (String) b.getLabelKey();
			if (bLabel == null || bLabel.length() == 0) {
				for (int j = 0; j < array_A.length; j++) {
					Edge a = (Edge) array_A[j];
					String aLabel = a.getLabelKey();
					if (aLabel != null) {
						boolean alreadyMatched = false;
						for (Iterator<Pair<Edge>> iter = matches.iterator(); iter.hasNext();) {
							Pair<Edge> element = iter.next();
							if (a.equals(element.getFirst())) {
								alreadyMatched = true;
								break;
							}
						}

						if (alreadyMatched == false) {
							logger.debug("    adding: " + a + " and " + b);
							null_matches_from_B_list.add(new Pair<Edge>(a, b));
						}
					}
				}
			}
		}
		matches.addAll(null_matches_from_A_list);
		matches.addAll(null_matches_from_B_list);
		return matches;
	}

	private void MergeOutEdgeAndInEdge(Edge outEdge, Edge inEdge, Vector<Edge> edgesToBeRemoved, Graph graph) {
		logger.debug("MergeOutEdgeAndInEdge");

		if (outEdge == null) {
			throw new RuntimeException("Internal progamming error");
		}
		if (inEdge == null) {
			throw new RuntimeException("Internal progamming error");
		}

		logger.debug("  outEdge: " + outEdge);
		logger.debug("  inEdge: " + inEdge);

		Edge new_edge = new Edge(inEdge, outEdge);
		graph.addEdge(new_edge, graph.getSource(inEdge), graph.getDest(outEdge));

		new_edge.setIndexKey(new Integer(getNewVertexAndEdgeIndex()));
		logger.debug("  Replacing the target vertex out-edge: " + outEdge + " (old) with: " + new_edge + "(new), using: " + inEdge);

		edgesToBeRemoved.add(inEdge);
		edgesToBeRemoved.add(outEdge);
	}

	/**
	 * Writes the graph to a PrintStream, using GraphML format.
	 */
	public void save(PrintStream ps, boolean printIndex) {
		Graph g = getModel();

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
			ps.print("          <y:NodeLabel x=\"1.5\" y=\"5.6494140625\" width=\"92.0\" height=\"18.701171875\" "
			    + "visible=\"true\" alignment=\"center\" fontFamily=\"Dialog\" fontSize=\"12\" "
			    + "fontStyle=\"plain\" textColor=\"#000000\" modelName=\"internal\" modelPosition=\"c\" " + "autoSizePolicy=\"content\">" + v.getFullLabelKey() );
			if ( printIndex ) {
				ps.print( "&#xA;INDEX=" + v.getIndexKey() );
			}
				
			ps.println( "</y:NodeLabel>");

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
			Pair<Vertex> p = graph.getEndpoints(e);
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

				ps
				    .println("          <y:EdgeLabel x=\"-148.25\" y=\"30.000000000000014\" width=\"169.0\" height=\"18.701171875\" "
				        + "visible=\"true\" alignment=\"center\" fontFamily=\"Dialog\" fontSize=\"12\" "
				        + "fontStyle=\"plain\" textColor=\"#000000\" modelName=\"free\" modelPosition=\"anywhere\" "
				        + "preferredPlacement=\"on_edge\" distance=\"2.0\" ratio=\"0.5\">" + label );
				if ( printIndex ) {
					ps.print("&#xA;INDEX=" + e.getIndexKey());
				}
				ps.println("</y:EdgeLabel>");
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
