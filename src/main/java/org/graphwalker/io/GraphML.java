// This file is part of the GraphWalker java package
// The MIT License
//
// Copyright (c) 2010 graphwalker.org
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

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
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * A file reader for GraphML files. The reader can read one single graphml file or several in a
 * single folder. When several files are read, they will be merged. Regardless if one or several
 * files are read, they all end up into one single Graph object.<br>
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
   * Reads one single graph, or a folder containing several graphs to be merged into one graph.
   * 
   * @see org.graphwalker.io.AbstractModelHandler#load(java.lang.String)
   * @param fileOrfolder The gramphml file or folder.
   */
  @Override
  public void load(String fileOrfolder) {
    if (!"".equals(fileOrfolder)) {
      File file = Util.getFile(fileOrfolder);
      if (file.isFile()) {
        parsedGraphList.add(parseFile(fileOrfolder));
        setMerged(false);
      } else if (file.isDirectory()) {
        // Only accepts files which suffix is .graphml
        FilenameFilter graphmlFilter = new FilenameFilter() {
          @Override
          public boolean accept(File dir, String name) {
            return name.endsWith(".graphml");
          }
        };

        File[] allChildren = file.listFiles(graphmlFilter);
        for (File anAllChildren : allChildren) {
          parsedGraphList.add(parseFile(anAllChildren.getAbsolutePath()));
          setMerged(false);
        }
      } else {
        throw new RuntimeException("'" + fileOrfolder
            + "' is not a file or a directory. Please specify a valid .graphml file or a directory containing .graphml files");
      }
    }
    mergeAllGraphs();
  }

  public void load(File fileOrfolder) {
    load(fileOrfolder);
  }

  /**
   * Parses the graphml file, and returns the model as a edu.uci.ics.jung.graph.impl.Graph
   * 
   * @param fileName The graphml file
   * @return The graph
   */
  private Graph parseFile(String fileName) {
    Graph graph = new Graph();
    graph.setFileKey(fileName);
    SAXBuilder parser = new SAXBuilder();

    try {
      logger.debug("Parsing file: " + fileName);
      Document doc = parser.build(Util.getFile(fileName));

      // Parse all vertices (nodes)
      Iterator<Element> iter_node = doc.getDescendants(new ElementFilter("node"));
      while (iter_node.hasNext()) {
        Object o = iter_node.next();
        if (o instanceof Element) {
          Element element = (Element) o;
          if (element.getAttributeValue("yfiles.foldertype") != null) {
            logger.debug("  Excluded node: " + element.getAttributeValue("yfiles.foldertype"));
            continue;
          }
          Iterator<Element> iterUMLNoteIter = element.getDescendants(new ElementFilter("UMLNoteNode"));
          if (iterUMLNoteIter.hasNext()) {
            Iterator<Element> iter_label = element.getDescendants(new ElementFilter("NodeLabel"));
            if (iter_label.hasNext()) {
              Object o3 = iter_label.next();
              Element nodeLabel = (Element) o3;
              logger.debug("  Full name: '" + nodeLabel.getQualifiedName() + "'");
              logger.debug("  Name: '" + nodeLabel.getTextTrim() + "'");
              graph.setDescriptionKey(nodeLabel.getTextTrim());
            }
            continue;
          }
          logger.debug("  id: " + element.getAttributeValue("id"));

          // Used to remember which vertex to store the image location.
          Vertex currentVertex = null;

          Iterator<Element> iterNodeLabel = element.getDescendants(new ElementFilter("NodeLabel"));
          while (iterNodeLabel.hasNext()) {
            Object o2 = iterNodeLabel.next();
            if (o2 instanceof Element) {
              Element nodeLabel = (Element) o2;
              logger.debug("  Full name: '" + nodeLabel.getQualifiedName() + "'");
              logger.debug("  Name: '" + nodeLabel.getTextTrim() + "'");
              String str = nodeLabel.getTextTrim();

              Vertex v = new Vertex();
              graph.addVertex(v);
              currentVertex = v;

              // Parse description
              Iterator<Element> iter_data = element.getDescendants(new ElementFilter("data"));
              while (iter_data.hasNext()) {
                Object o3 = iter_data.next();
                if (o instanceof Element) {
                  Element data = (Element) o3;
                  if (!data.getAttributeValue("key").equals("d5")) continue;
                  v.setDesctiptionKey(data.getText());
                  break;
                }
              }

              v.setIdKey(element.getAttributeValue("id"));
              v.setVisitedKey(0);
              v.setFileKey(fileName);
              v.setFullLabelKey(str);
              v.setIndexKey(getNewVertexAndEdgeIndex());
              v.setLabelKey(Vertex.getLabel(str));
              v.setMergeKey(AbstractElement.isMerged(str));
              v.setNoMergeKey(AbstractElement.isNoMerge(str));
              v.setBlockedKey(AbstractElement.isBlocked(str));
              v.setSwitchModelKey(Vertex.isSwitchModel(str));

              Integer index = AbstractElement.getIndex(str);
              if (index != 0) {
                v.setIndexKey(index);
              }

              v.setReqTagKey(AbstractElement.getReqTags(str));
            }
          }

          // Extract any manual test instructions
          Iterator<Element> iterData = element.getDescendants(new ElementFilter("data"));
          while (iterData.hasNext() && currentVertex != null) {
            Object o2 = iterData.next();
            if (o2 instanceof Element) {
              Element data = (Element) o2;
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
          Iterator<Element> iterImage = element.getDescendants(new ElementFilter("Image"));
          while (iterImage.hasNext() && currentVertex != null) {
            Object o2 = iterImage.next();
            if (o2 instanceof Element) {
              Element image = (Element) o2;
              if (image.getAttributeValue("href") != null) {
                logger.debug("  Image: '" + image.getAttributeValue("href") + "'");
                currentVertex.setImageKey(image.getAttributeValue("href"));
              }
            }
          }
          Iterator<Element> iterGeometry = element.getDescendants(new ElementFilter("Geometry"));
          while (iterGeometry.hasNext() && currentVertex != null) {
            Object o2 = iterGeometry.next();
            if (o2 instanceof Element) {
              Element geometry = (Element) o2;
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
          Iterator<Element> iterFill = element.getDescendants(new ElementFilter("Fill"));
          while (iterFill.hasNext() && currentVertex != null) {
            Object o2 = iterFill.next();
            if (o2 instanceof Element) {
              Element fill = (Element) o2;
              logger.debug("  fill color: '" + fill.getAttributeValue("color") + "'");
              currentVertex.setFillColor(new Color(Integer.parseInt(fill.getAttributeValue("color").replace("#", ""), 16)));
            }
          }
        }
      }

      Object[] vertices = graph.getVertices().toArray();

      // Parse all edges (arrows or transitions)
      Iterator<Element> iter_edge = doc.getDescendants(new ElementFilter("edge"));
      while (iter_edge.hasNext()) {
        Object o = iter_edge.next();
        if (o instanceof Element) {
          Element element = (Element) o;
          logger.debug("  id: " + element.getAttributeValue("id"));

          Edge e = new Edge();

          Iterator<Element> iter2 = element.getDescendants(new ElementFilter("EdgeLabel"));
          Element edgeLabel = null;
          if (iter2.hasNext()) {
            Object o2 = iter2.next();
            if (o2 instanceof Element) {
              edgeLabel = (Element) o2;
              logger.debug("  Full name: '" + edgeLabel.getQualifiedName() + "'");
              logger.debug("  Name: '" + edgeLabel.getTextTrim() + "'");
              logger.debug(" Edge label x: " + edgeLabel.getAttributeValue("x"));
              logger.debug(" Edge label y: " + edgeLabel.getAttributeValue("y"));
              logger.debug(" Edge label width: " + edgeLabel.getAttributeValue("width"));
              logger.debug(" Edge label height: " + edgeLabel.getAttributeValue("height"));
              e.setLabelHeight(Float.parseFloat(edgeLabel.getAttributeValue("height")));
              e.setLabelWidth(Float.parseFloat(edgeLabel.getAttributeValue("width")));
              e.setLabelLocation(new Point2D.Float(Float.parseFloat(edgeLabel.getAttributeValue("x")), Float.parseFloat(edgeLabel.getAttributeValue("y"))));
            }
          }
          Iterator<Element> iter3 = element.getDescendants(new ElementFilter("Path"));
          Element edgePath = null;
          if (iter3.hasNext()) {
            Object o3 = iter3.next();
            if (o3 instanceof Element) {
              edgePath = (Element) o3;
              logger.debug("  Path sx: '" + edgePath.getAttributeValue("sx"));
              logger.debug("  Path sy: '" + edgePath.getAttributeValue("sy"));
              logger.debug("  Path tx: '" + edgePath.getAttributeValue("tx"));
              logger.debug("  Path ty: '" + edgePath.getAttributeValue("ty"));
              e.setPathSourceLocation(new Point2D.Float(Float.parseFloat(edgePath.getAttributeValue("sx")), Float.parseFloat(edgePath.getAttributeValue("sy"))));
              e.setPathTargetLocation(new Point2D.Float(Float.parseFloat(edgePath.getAttributeValue("tx")), Float.parseFloat(edgePath.getAttributeValue("ty"))));
            }
          }

          // Add edge path points if there is any.
          Iterator<Element> iter4 = element.getDescendants(new ElementFilter("Point"));
          Element edgePathPoint = null;
          while (iter4.hasNext()) {
            Object o4 = iter4.next();
            if (o4 instanceof Element) {
              edgePathPoint = (Element) o4;
              logger.debug("  PathPoint x: '" + edgePathPoint.getAttributeValue("x"));
              logger.debug("  PathPoint y: '" + edgePathPoint.getAttributeValue("y"));
              e.setPathPoints(new Point2D.Float(Float.parseFloat(edgePathPoint.getAttributeValue("x")), Float.parseFloat(edgePathPoint.getAttributeValue("y"))));
            }
          }

          logger.debug("  source: " + element.getAttributeValue("source"));
          logger.debug("  target: " + element.getAttributeValue("target"));

          Vertex source = null;
          Vertex dest = null;

          for (Object vertice : vertices) {
            Vertex vertex = (Vertex) vertice;

            // Find source vertex
            if (vertex.getIdKey().equals(element.getAttributeValue("source")) && vertex.getFileKey().equals(fileName)) {
              source = vertex;
            }
            if (vertex.getIdKey().equals(element.getAttributeValue("target")) && vertex.getFileKey().equals(fileName)) {
              dest = vertex;
            }
          }
          if (source == null) {
            String msg = "Could not find starting node for edge. Name: '" + element.getAttributeValue("source") + "' In file '" + fileName + "'";
            logger.error(msg);
            throw new RuntimeException(msg);
          }
          if (dest == null) {
            String msg = "Could not find end node for edge. Name: '" + element.getAttributeValue("target") + "' In file '" + fileName + "'";
            logger.error(msg);
            throw new RuntimeException(msg);
          }

          e.setIdKey(element.getAttributeValue("id"));
          e.setFileKey(fileName);
          e.setIndexKey(getNewVertexAndEdgeIndex());

          // Parse description
          Iterator<Element> iter_data = element.getDescendants(new ElementFilter("data"));
          while (iter_data.hasNext()) {
            Object o3 = iter_data.next();
            if (o instanceof Element) {
              Element data = (Element) o3;
              if (!data.getAttributeValue("key").equals("d9")) continue;
              e.setDesctiptionKey(data.getText());
              break;
            }
          }

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

            Integer index = AbstractElement.getIndex(str);
            if (index != 0) {
              e.setIndexKey(index);
            }

            e.setReqTagKey(AbstractElement.getReqTags(str));
          }
          e.setVisitedKey(0);
          logger.debug("  Added edge: '" + e.getLabelKey() + "', with id: " + e.getIndexKey());

          // Extract any manual test instructions
          Iterator<Element> iterData = element.getDescendants(new ElementFilter("data"));
          while (iterData.hasNext() && e != null) {
            Object o2 = iterData.next();
            if (o2 instanceof Element) {
              Element data = (Element) o2;
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
    } catch (IOException e) {
      throw new RuntimeException("Could not parse file: '" + fileName + "'. " + e.getMessage());
    } catch (JDOMException e) {
      throw new RuntimeException("Could not parse file: '" + fileName + "'. " + e.getMessage());
    }

    logger.debug("Finished parsing graph: " + graph);
    removeBlockedEntities(graph);
    logger.debug("Graph after removing BLOCKED entities: " + graph);

    return graph;
  }

  /**
   * Increment and return the unique index for a vertex or edge.
   * 
   * @return
   */
  private int getNewVertexAndEdgeIndex() {
    return ++vertexAndEdgeIndex;
  }

  /**
   * Removes any edges, and any vertices that contains the key word BLOCKED
   * 
   * @param graph
   */
  private void removeBlockedEntities(Graph graph) {
    Object[] vertices = graph.getVertices().toArray();
    for (Object vertice : vertices) {
      Vertex v = (Vertex) vertice;
      if (v.isBlockedKey()) {
        logger.debug("Removing this vertex because it is BLOCKED: '" + v.getLabelKey() + "'");
        graph.removeVertex(v);
      }
    }
    Object[] edges = graph.getEdges().toArray();
    for (Object edge : edges) {
      Edge e = (Edge) edge;
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
      if (v.getLabelKey().equalsIgnoreCase(Keywords.START_NODE)) {
        Edge e = (Edge) graph.getOutEdges(v).toArray()[0];
        graph.setLabelKey(graph.getDest(e).getLabelKey());
        graph.getDest(e).setGraphVertex(true);
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
      for (Object vertice : vertices) {
        Vertex v = (Vertex) vertice;

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
                throw new RuntimeException("Only one Start vertex can exist in one file, see files " + graph.getFileKey() + ", and " + g.getFileKey());
              }
            }
            if (foundSubStartGraph) {
              throw new RuntimeException("Only one Start vertex can exist in one file, see file '" + g.getFileKey() + "'");
            }

            foundMotherStartGraph = true;
            graph = g;
            g.getDest(edge).setMotherStartVertexKey(Keywords.MOTHER_GRAPH_START_VERTEX);
            logger.debug("Found the mother graph in the file: " + graph.getFileKey());
          } else {
            if (foundSubStartGraph) {
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

            if (foundMotherStartGraph) {
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
   * Look for duplicated vertices in each sub-graph. If a vertex is found, which represents the name
   * of the sub-graph (the vertex which the Start vertex points to) is duplicated in the same
   * sub-graph, this will lead to an infinite recursive loop.
   */
  private void checkForDuplicateVerticesInSubgraphs() {
    for (int i = 0; i < parsedGraphList.size(); i++) {
      Graph g = parsedGraphList.elementAt(i);

      // Exclude the mother graph
      if (graph.hashCode() == g.hashCode()) {
        continue;
      }

      logger.debug("Looking for infinit recursive loop in file: " + g.getFileKey());

      String subgraph_label = g.getLabelKey();
      Object[] vertices = g.getVertices().toArray();
      for (Object vertice : vertices) {
        Vertex v = (Vertex) vertice;
        String label = v.getLabelKey();
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
      Graph g = parsedGraphList.elementAt(i);

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

          logger.debug("A subgraph'ed vertex: '" + v1.getLabelKey() + "' in graph: " + g.getFileKey() + ", equals a node in the graph in file: '"
              + graph.getFileKey() + "'");

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
    for (Object aList1 : list1) {
      Vertex v1 = (Vertex) aList1;

      if (!v1.isMergeKey()) {
        continue;
      }

      Object[] list2 = graph.getVertices().toArray();
      Vector<Vertex> mergedVertices = new Vector<Vertex>();
      for (Object aList2 : list2) {
        Vertex v2 = (Vertex) aList2;

        if (!v1.getLabelKey().equals(v2.getLabelKey())) {
          continue;
        }
        if (v2.isNoMergeKey()) {
          continue;
        }
        if (v1.getIndexKey().equals(v2.getIndexKey())) {
          continue;
        }
        if (mergedVertices.contains(v1)) {
          continue;
        }

        logger.debug("Merging vertex(" + v1.getIndexKey() + "): '" + v1.getLabelKey() + "' with vertex (" + v2.getIndexKey() + ")");

        Object[] inEdges = graph.getInEdges(v1).toArray();
        for (Object inEdge : inEdges) {
          Edge edge = (Edge) inEdge;
          Edge new_edge = new Edge(edge);
          new_edge.setIndexKey(getNewVertexAndEdgeIndex());
          graph.addEdge(new_edge, graph.getSource(edge), v2);
        }
        Object[] outEdges = graph.getOutEdges(v1).toArray();
        for (Object outEdge : outEdges) {
          Edge edge = (Edge) outEdge;
          Edge new_edge = new Edge(edge);
          new_edge.setIndexKey(getNewVertexAndEdgeIndex());
          graph.addEdge(new_edge, v2, graph.getDest(edge));
        }
        mergedVertices.add(v1);
      }

      if (!mergedVertices.isEmpty()) {
        logger.debug("Remvoing merged vertex(" + v1.getIndexKey() + ")");
        graph.removeVertex(v1);
      }
    }
  }

  /**
   * Search for any vertices with no in edges
   * 
   * @throws RuntimeException
   */
  private void checkForVerticesWithZeroInEdges() throws RuntimeException {
    Object[] vs = graph.getVertices().toArray();
    for (Object v1 : vs) {
      Vertex v = (Vertex) v1;
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
    for (Object vertice : vertices) {
      Vertex v = (Vertex) vertice;
      if (v.getLabelKey().equalsIgnoreCase(Keywords.START_NODE)) {
        continue;
      }
      Vertex new_v = new Vertex(v);
      new_v.setIndexKey(getNewVertexAndEdgeIndex());
      dst.addVertex(new_v);
      logger.debug("Associated vertex: " + v + " to new vertex: " + new_v);
      map.put(v.getIndexKey(), new_v);
    }
    Object[] edges = src.getEdges().toArray();
    for (Object edge : edges) {
      Edge e = (Edge) edge;
      Vertex v1 = map.get(src.getSource(e).getIndexKey());
      Vertex v2 = map.get(src.getDest(e).getIndexKey());
      if (v1 == null || v2 == null) {
        continue;
      }
      Edge new_e = new Edge(e);
      dst.addEdge(new_e, v1, v2);
      new_e.setIndexKey(getNewVertexAndEdgeIndex());
    }
  }

  /**
   * Replaces the vertex targetVertex and all its in and out edges, in the graph g, with all other
   * vertices with the same name.
   * 
   * @param mainGraph
   * @param subGraph
   * @param targetVertex
   */
  private void copySubGraphs(Graph mainGraph, Graph subGraph, Vertex targetVertex) {
    // Save the target vertex out-edge list
    Vector<Edge> targetVertexOutEdgeList = new Vector<Edge>();
    logger.debug("Target vertex (" + targetVertex + ") out-edge list");
    for (Edge element : mainGraph.getOutEdges(targetVertex)) {
      logger.debug("  " + element);
      targetVertexOutEdgeList.add(element);
    }

    Vertex sourceVertex = null;
    Object[] vertices = mainGraph.getVertices().toArray();
    for (Object vertice : vertices) {
      Vertex v = (Vertex) vertice;
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
        if (v.getIndexKey().equals(targetVertex.getIndexKey())) {
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
    for (Object inEdge : inEdges) {
      Edge edge = (Edge) inEdge;
      Edge new_edge = new Edge(edge);
      mainGraph.addEdge(new_edge, mainGraph.getSource(edge), targetVertex);
      new_edge.setIndexKey(getNewVertexAndEdgeIndex());
    }
    Object[] outEdges = mainGraph.getOutEdges(sourceVertex).toArray();
    for (Object outEdge : outEdges) {
      Edge edge = (Edge) outEdge;
      Edge new_edge = new Edge(edge);
      mainGraph.addEdge(new_edge, targetVertex, mainGraph.getDest(edge));
      new_edge.setIndexKey(getNewVertexAndEdgeIndex());
    }
    logger.debug("Remvoing source vertex: " + sourceVertex);
    mainGraph.removeVertex(sourceVertex);
    targetVertex.setMergedMbtKey(true);

    // Check if there exists a Stop vertex.
    // Also check if there is only one.
    Vertex stopVertex = null;
    vertices = mainGraph.getVertices().toArray();
    for (Object vertice : vertices) {
      Vertex v = (Vertex) vertice;
      if (v.getLabelKey().equalsIgnoreCase(Keywords.STOP_NODE)) {
        if (stopVertex != null) {
          throw new RuntimeException("Found more than 1 Stop vertex in file (Only one Stop vertex per file is allowed): '" + mainGraph.getFileKey()
              + "'");
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
      for (Edge element : mainGraph.getInEdges(stopVertex)) {
        logger.debug("  " + element);
      }
      logger.debug("Target vertex (" + targetVertex + ") out-edge list");
      for (Edge element : targetVertexOutEdgeList) {
        logger.debug("  " + element);
      }

      Vector<Pair<Edge>> mergeList = MergeList(targetVertexOutEdgeList.toArray(), inEdges);
      for (Pair<Edge> pair : mergeList) {
        MergeOutEdgeAndInEdge(pair.getFirst(), pair.getSecond(), edgesToBeRemoved, mainGraph);
      }

      // Now remove the edges that has been copied.
      Object[] list = edgesToBeRemoved.toArray();
      for (Object aList : list) {
        Edge element = (Edge) aList;
        if (mainGraph.containsEdge(element)) {
          try {
            logger.debug("Removing edge: " + element);
            logger.debug(element + ", was found and removed from graph,: '" + mainGraph.getFileKey() + "'");
            mainGraph.removeEdge(element);
          } catch (IllegalArgumentException e) {
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
    for (Object anArray_A : array_A) {
      Edge a = (Edge) anArray_A;
      String aLabel = a.getLabelKey();
      for (Object anArray_B : array_B) {
        Edge b = (Edge) anArray_B;
        String bLabel = b.getLabelKey();
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
    for (Object anArray_A : array_A) {
      Edge a = (Edge) anArray_A;
      String aLabel = a.getLabelKey();
      if (aLabel == null || aLabel.length() == 0) {
        for (Object anArray_B : array_B) {
          Edge b = (Edge) anArray_B;
          String bLabel = b.getLabelKey();
          if (bLabel != null) {
            boolean alreadyMatched = false;
            for (Pair<Edge> element : matches) {
              if (b.equals(element.getSecond())) {
                alreadyMatched = true;
                break;
              }
            }

            if (!alreadyMatched) {
              logger.debug("    adding: " + a + " and " + b);
              null_matches_from_A_list.add(new Pair<Edge>(a, b));
            }
          }
        }
      }
    }

    Vector<Pair<Edge>> null_matches_from_B_list = new Vector<Pair<Edge>>();
    logger.debug("  Matching nulls from the B list with non-matched items in the first list");
    for (Object anArray_B : array_B) {
      Edge b = (Edge) anArray_B;
      String bLabel = b.getLabelKey();
      if (bLabel == null || bLabel.length() == 0) {
        for (Object anArray_A : array_A) {
          Edge a = (Edge) anArray_A;
          String aLabel = a.getLabelKey();
          if (aLabel != null) {
            boolean alreadyMatched = false;
            for (Pair<Edge> element : matches) {
              if (a.equals(element.getFirst())) {
                alreadyMatched = true;
                break;
              }
            }

            if (!alreadyMatched) {
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

    new_edge.setIndexKey(getNewVertexAndEdgeIndex());
    logger.debug("  Replacing the target vertex out-edge: " + outEdge + " (old) with: " + new_edge + "(new), using: " + inEdge);

    edgesToBeRemoved.add(inEdge);
    edgesToBeRemoved.add(outEdge);
  }

  /**
   * Writes the graph to a PrintStream, using GraphML format.
   */
  @Override
  public void save(PrintStream ps, boolean printIndex) {
    Graph g = getModel();

    ps.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
    ps.println("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\"  " + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
        + "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml " + "http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd\" "
        + "xmlns:y=\"http://www.yworks.com/xml/graphml\">");
    ps.println("  <key id=\"d0\" for=\"node\" yfiles.type=\"nodegraphics\"/>");
    ps.println("  <key id=\"d1\" for=\"edge\" yfiles.type=\"edgegraphics\"/>");
    ps.println("  <graph id=\"G\" edgedefault=\"directed\">");

    for (Vertex v : g.getVertices()) {
      ps.println("    <node id=\"n" + v.getIndexKey() + "\">");
      ps.println("      <data key=\"d0\" >");

      if (!v.getImageKey().isEmpty()) {
        ps.println("        <y:ImageNode >");
        ps.println("          <y:Geometry  x=\"241.875\" y=\"158.701171875\" width=\"" + v.getWidth() + "\" height=\"" + v.getHeight() + "\"/>");
      } else {
        ps.println("        <y:ShapeNode >");
        ps.println("          <y:Geometry  x=\"241.875\" y=\"158.701171875\" width=\"95.0\" height=\"30.0\"/>");
      }

      ps.println("          <y:Fill color=\"#CCCCFF\"  transparent=\"false\"/>");
      ps.println("          <y:BorderStyle type=\"line\" width=\"1.0\" color=\"#000000\" />");
      ps.print("          <y:NodeLabel x=\"1.5\" y=\"5.6494140625\" width=\"92.0\" height=\"18.701171875\" "
          + "visible=\"true\" alignment=\"center\" fontFamily=\"Dialog\" fontSize=\"12\" "
          + "fontStyle=\"plain\" textColor=\"#000000\" modelName=\"internal\" modelPosition=\"c\" " + "autoSizePolicy=\"content\">"
          + v.getFullLabelKey());
      if (printIndex) {
        ps.print("&#xA;INDEX=" + v.getIndexKey());
      }

      ps.println("</y:NodeLabel>");

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

    for (Edge e : g.getEdges()) {
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

        ps.println("          <y:EdgeLabel x=\"-148.25\" y=\"30.000000000000014\" width=\"169.0\" height=\"18.701171875\" "
            + "visible=\"true\" alignment=\"center\" fontFamily=\"Dialog\" fontSize=\"12\" "
            + "fontStyle=\"plain\" textColor=\"#000000\" modelName=\"free\" modelPosition=\"anywhere\" "
            + "preferredPlacement=\"on_edge\" distance=\"2.0\" ratio=\"0.5\">" + label);
        if (printIndex) {
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
