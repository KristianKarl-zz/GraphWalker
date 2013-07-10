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

package org.graphwalker;

import java.util.Vector;

import org.graphwalker.exceptions.GeneratorException;
import org.graphwalker.exceptions.StopConditionException;
import org.graphwalker.multipleModels.ModelHandler;

/**
 * Handles the common constants for the org.graphwalker package. This includes reserved key words,
 * and text strings used for storing custom data as UserDatum in graphs, vertices and edges.
 */
public class Keywords {

  /**
   * Each graph has a start (entry) vertex, and that vertex holds this datum.<br>
   * There is only one vertex in a graph, holding this datum.<br>
   * Used by MBT when merging graphs, and when generating test sequences.
   */
  public static final String START_NODE = "Start";

  /**
   * Each graph has one and only one vertex to which the START_NODE points to. The vertex which has
   * this keyword set, is that node.
   */
  public static final String GRAPH_VERTEX = "graph_vertex";

  /**
   * The stop (exit) vertex denotes an exit point in a graph.<br>
   * There can only be one vertex in a graph, holding this datum.<br>
   * Only sub-graphs can have a stop vertex.<br>
   * Used by MBT only when merging graphs.
   */
  public static final String STOP_NODE = "Stop";

  /**
   * The id taken from the graphml file. TODO Investigate if this datum is deprecated
   */
  public static final String ID_KEY = "id";

  /**
   * The graph editor yEd, can use images to depict vertices, which normally gets lost during
   * merging. So, when writing merged graphs back to file, this datum holds the path and file of
   * that image.
   */
  public static final String IMAGE_KEY = "image";

  /**
   * The graph editor yEd, can use images to depict vertices, which normally gets lost during
   * merging. So, when writing merged graphs back to file, this datum holds the width of that image.
   */
  public static final String WIDTH_KEY = "width";

  /**
   * The graph editor yEd, can use images to depict vertices, which normally gets lost during
   * merging. So, when writing merged graphs back to file, this datum holds the height of that
   * image.
   */
  public static final String HEIGHT_KEY = "height";

  /**
   * The x position of the node, as saved by the yEd editor.
   */
  public static final String X_POS = "x_pos";

  /**
   * The y position of the node, as saved by the yEd editor.
   */
  public static final String Y_POS = "y_pos";

  /**
   * When merging graphs, the source file of each graph is noted, so that in the event of an error,
   * the correct graph file can be used in meaningful error messages to the end user.
   */
  public static final String FILE_KEY = "file";

  /**
   * The name of the vertex or edge, that will result in a method or function call in the executing
   * test tool.<br>
   * * The label of an edge can be empty (or null).<br>
   * * A vertex must always have a label.<br>
   * * The label is always defined at the first line in a label.<br>
   */
  public static final String LABEL_KEY = "label";

  /**
   * This datum contain the complete text hold by a label.
   */
  public static final String FULL_LABEL_KEY = "full_label";

  /**
   * This datum contains a counter for each vertex and edge. Used by MBT during a online test. Every
   * time a vertex or an edge is traversed it is visited, thus incremented once.
   */
  public static final String VISITED_KEY = "visited";

  /**
   * Used by MBT during random walks during test sequence generation. It holds a real value between
   * 0 and 1, and represents the probability that a specific edge should be chosen. A value of 0.05,
   * would mean a 5% chance of that edge to be selected during a run.<br>
   * * Only edges uses this datum.
   */
  public static final String WEIGHT_KEY = "weight";

  /**
   * Used by MBT to store information about manual testing. This data should containing text
   * regarding an action or expected result.
   */
  public static final String DATA_KEY = "data";

  /**
   * The datum provides the edge or vertex a unique integer number, uniquely identifying the object.<br>
   * Generated by MBT when reading and merging graphs. Also used to provide better info during
   * logging at:<br>
   * * Parsing graphml files<br>
   * * Merging<br>
   * * Generating offline and online test sequences
   */
  public static final String INDEX_KEY = "index";

  /**
   * This datum is used by MBT when merging graphs. It tells MBT that the vertex containing this key
   * word, should be merged with the first occurence of a vertex with the same label.
   */
  public static final String MERGE = "merge";

  /**
   * This datum is used by MBT when merging graphs. It tells MBT that the vertex containing this key
   * word, should not merge this vertex with any subgraph.
   */
  public static final String NO_MERGE = "no merge";

  /**
   * Used internally by MBT. When graphs are merged, MBT keeps track of which vertices have been
   * merged by MBT.
   */
  public static final String MERGED_BY_MBT = "merged by mbt";

  /**
   * Used internally by MBT. A vertex in a graph with the label START, and a single empty out edge
   * is defined as the main graph (or Mother Graph)
   */
  public static final String MOTHER_GRAPH_START_VERTEX = "mother graph start vertex";

  /**
   * Used internally by MBT. A vertex in a graph with the label START, and a single non-empty out
   * edge is defined as a subgraph.
   */
  public static final String SUBGRAPH_START_VERTEX = "subgraph start vertex";

  /**
   * A vertex or an edge with the key word BLOCKED, will not participate in the resulting (merged)
   * graph, end thus be excluded.
   */
  public static final String BLOCKED = "BLOCKED";

  /**
   * An edge with the key word BACKTRACK is used to enable a simple logic that enables a end user to
   * backtrack in the graph to the previous vertex.<br>
   * This is used only during a online run.
   */
  public static final String BACKTRACK = "BACKTRACK";

  /**
   * This datum contains the label parameter used by an edge in EFSM models.
   */
  public static final String PARAMETER_KEY = "parameter";

  /**
   * This datum contains the label guard used by an edge in EFSM models.
   */
  public static final String GUARD_KEY = "guard";

  /**
   * This datum contains the actions used by an edge in EFSM models. The datum is a String
   * containing the actions.
   */
  public static final String ACTIONS_KEY = "action";

  /**
   * This datum contains the requirement tag which can be set in a vertex or edge. The datum is a
   * String containing one or more requirement tag. Multiple tags are to be separated using commas.
   */
  public static final String REQTAG_KEY = "reqtag";

  /**
   * Used when running multiple-models, see also {@link ModelHandler}. The meaning of this keyword
   * is that if a vertex has this set, the execution of the model will be paused, and the execution
   * may switch to another model with the same label as the vertex of the
   * {@link Keywords#GRAPH_VERTEX GRAPH_VERTEX} in that other model.
   */
  public static final String SWITCH_MODEL = "switch_model";

  /**
   * This datum contains the dijkstra object used to find the shortest path in models.
   */
  public static final String DIJKSTRA = "dijkstra";

  public static final int CONDITION_REACHED_EDGE = 1001;

  public static final int CONDITION_REACHED_VERTEX = 1002;

  public static final int CONDITION_EDGE_COVERAGE = 1003;

  public static final int CONDITION_VERTEX_COVERAGE = 1004;

  public static final int CONDITION_TEST_LENGTH = 1005;

  public static final int CONDITION_TEST_DURATION = 1006;

  public static final int CONDITION_REQUIREMENT_COVERAGE = 1007;

  public static final int CONDITION_REACHED_REQUIREMENT = 1008;

  public static final int CONDITION_NEVER = 1009;

  public static final int GENERATOR_RANDOM = 2001;

  public static final int GENERATOR_A_STAR = 2002;

  public static final int GENERATOR_LIST = 2003;

  public static final int GENERATOR_STUB = 2004;

  public static final int GENERATOR_REQUIREMENTS = 2005;

  public static final int GENERATOR_SHORTEST_NON_OPTIMIZED = 2006;

  public static final int GENERATOR_MANUAL_HTML = 2007;

  public static final int GENERATOR_ALL_PATH_PERMUTATIONS = 2008;

  /**
   * Holds the pre-defined key words
   */
  private static Vector<String> reservedKeyWords = new Vector<String>();

  /**
   * Defines the key words
   */
  static {
    Keywords.reservedKeyWords.add("BLOCKED");
    Keywords.reservedKeyWords.add("BACKTRACK");
    Keywords.reservedKeyWords.add("MERGE");
    Keywords.reservedKeyWords.add("NO_MERGE");
    Keywords.reservedKeyWords.add("SWITCH_MODEL");
  }

  /**
   * Returns true if the wordToCheck is a pre-defined key word.
   */
  public static boolean isKeyWord(final String wordToCheck) {
    return Keywords.reservedKeyWords.contains(wordToCheck.toUpperCase());
  }

  static class StopCondition {
    private String name;
    private String description;
    private Integer id;

    private StopCondition(final String name, final String description, final Integer id) {
      this.name = name;
      this.description = description;
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(final String name) {
      this.name = name;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(final String description) {
      this.description = description;
    }

    public Integer getId() {
      return id;
    }

    public void setId(final Integer id) {
      this.id = id;
    }
  }

  /**
   * Holds the pre-defined list of stop-condition.
   */
  private static Vector<StopCondition> stopConditions = new Vector<StopCondition>();

  /**
   * Defines the stop-condition strings
   */
  static {
    Keywords.stopConditions.add(new StopCondition("REACHED_EDGE", "REACHED_EDGE:<Edge label>", Keywords.CONDITION_REACHED_EDGE));
    Keywords.stopConditions.add(new StopCondition("REACHED_VERTEX", "REACHED_VERTEX:<Vertex label[/variable1=value1;variable2=value2;...]>",
        Keywords.CONDITION_REACHED_VERTEX));
    Keywords.stopConditions.add(new StopCondition("EDGE_COVERAGE", "EDGE_COVERAGE:<Coverage in %, between 1 and 100>",
        Keywords.CONDITION_EDGE_COVERAGE));
    Keywords.stopConditions.add(new StopCondition("VERTEX_COVERAGE", "VERTEX_COVERAGE:<Coverage in %, between 1 and 100>",
        Keywords.CONDITION_VERTEX_COVERAGE));
    Keywords.stopConditions.add(new StopCondition("TEST_LENGTH", "TEST_LENGTH:<Number of edge and vertex pairs to execute>",
        Keywords.CONDITION_TEST_LENGTH));
    Keywords.stopConditions.add(new StopCondition("TEST_DURATION", "TEST_DURATION:<Time in seconds>", Keywords.CONDITION_TEST_DURATION));
    Keywords.stopConditions.add(new StopCondition("REQUIREMENT_COVERAGE", "REQUIREMENT_COVERAGE:<Coverage in %, between 1 and 100>",
        Keywords.CONDITION_REQUIREMENT_COVERAGE));
    Keywords.stopConditions.add(new StopCondition("REACHED_REQUIREMENT", "REACHED_REQUIREMENT:<Requirement id>",
        Keywords.CONDITION_REACHED_REQUIREMENT));
    Keywords.stopConditions.add(new StopCondition("NEVER", "NEVER", Keywords.CONDITION_NEVER));
  }

  static public Vector<StopCondition> getStopConditions() {
    return Keywords.stopConditions;
  }

  static private boolean isStopCondition(final String presumedCondition) {
    if (presumedCondition == null) {
      return false;
    }
    for (StopCondition sc : Keywords.stopConditions) {
      if (sc.getName().equalsIgnoreCase(presumedCondition)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param stopCondition
   * @return if supplied with a valid stop condition returns its constant value. returns -1 if
   *         supplied with false value.
   * @throws StopConditionException
   */
  static public int getStopCondition(final String stopCondition) throws StopConditionException {
    if (!isStopCondition(stopCondition)) {
      if (stopCondition == null || stopCondition.isEmpty()) {
        throw new StopConditionException("No stop condition is given.");
      } else {
        throw new StopConditionException("Invalid stop condition: " + stopCondition);
      }
    }
    for (StopCondition sc : Keywords.stopConditions) {
      if (sc.getName().equalsIgnoreCase(stopCondition)) {
        return sc.getId();
      }
    }
    return -1;
  }

  static class Generator {
    private String name;
    private String description;
    private Integer id;
    private Boolean published;

    private Generator(final String name, final String description, final Integer id, final Boolean published) {
      this.name = name;
      this.description = description;
      this.id = id;
      this.published = published;
    }

    public String getName() {
      return name;
    }

    public void setName(final String name) {
      this.name = name;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(final String description) {
      this.description = description;
    }

    public Integer getId() {
      return id;
    }

    public void setId(final Integer id) {
      this.id = id;
    }

    public Boolean isPublished() {
      return published;
    }

    public void setPublished(final Boolean published) {
      this.published = published;
    }
  }

  /**
   * Holds the pre-defined list of generators
   */
  private static Vector<Generator> generators = new Vector<Generator>();

  /**
   * Defines the generators strings
   */
  static {
    Keywords.generators.add(new Generator("RANDOM", "RANDOM", Keywords.GENERATOR_RANDOM, true));
    Keywords.generators.add(new Generator("A_STAR", "A_STAR", Keywords.GENERATOR_A_STAR, true));
    Keywords.generators.add(new Generator("LIST", "LIST", Keywords.GENERATOR_LIST, false));
    Keywords.generators.add(new Generator("STUB", "STUB", Keywords.GENERATOR_STUB, false));
    Keywords.generators.add(new Generator("REQUIREMENTS", "REQUIREMENTS", Keywords.GENERATOR_REQUIREMENTS, false));
    Keywords.generators.add(new Generator("SHORTEST_NON_OPTIMIZED", "SHORTEST_NON_OPTIMIZED", Keywords.GENERATOR_SHORTEST_NON_OPTIMIZED, true));
    Keywords.generators.add(new Generator("ALL_PATH_PERMUTATIONS", "ALL_PATH_PERMUTATIONS", Keywords.GENERATOR_ALL_PATH_PERMUTATIONS, true));
  }

  static public Vector<Generator> getGenerators() {
    return Keywords.generators;
  }

  static private boolean isGenerator(final String presumedGenerator) {
    if (presumedGenerator == null) {
      return false;
    }
    for (Generator g : Keywords.generators) {
      if (g.getName().equalsIgnoreCase(presumedGenerator)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param generator
   * @return if supplied with a valid generator returns its constant value. returns -1 if supplied
   *         with false value.
   * @throws GeneratorException
   */
  static public int getGenerator(final String generator) throws GeneratorException {
    if (!isGenerator(generator)) {
      if (generator == null || generator.isEmpty()) {
        throw new GeneratorException("No generator is given.");
      } else {
        throw new GeneratorException("Invalid generator: " + generator);
      }
    }
    for (Generator g : Keywords.generators) {
      if (g.getName().equalsIgnoreCase(generator)) {
        return g.getId();
      }
    }
    return -1;
  }
}
