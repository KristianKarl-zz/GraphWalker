/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.graphwalker.core;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.graphwalker.core.conditions.*;
import org.graphwalker.core.exceptions.GeneratorException;
import org.graphwalker.core.exceptions.StopConditionException;
import org.graphwalker.core.generators.*;
import org.graphwalker.core.graph.AbstractElement;
import org.graphwalker.core.graph.Edge;
import org.graphwalker.core.graph.Graph;
import org.graphwalker.core.graph.Vertex;
import org.graphwalker.core.io.PrintHTMLTestSequence;
import org.graphwalker.core.machines.FiniteStateMachine;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.*;

/**
 * This class has some utility functionality used by org.graphwalker The
 * functionality is:<br>
 * * Getting names with extra info for vertices and edges<br>
 * * Setting up the logger for classes<br>
 */
public class Util {

    private static Logger logger = setupLogger(Util.class);
    static private Random random = new Random();
    /** Constant <code>newline="System.getProperty(line.separator)"</code> */
    public static String newline = System.getProperty("line.separator");
    static private Timer timer = null;

    /**
     * <p>getCompleteName.</p>
     *
     * @param element a {@link org.graphwalker.core.graph.AbstractElement} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getCompleteName(final AbstractElement element) {
        if (element instanceof Edge) {
            return getCompleteEdgeName((Edge) element);
        }
        if (element instanceof Vertex) {
            return getCompleteVertexName((Vertex) element);
        }
        throw new RuntimeException("Element type not supported: '" + element.getClass().getName() + "'");
    }

    /**
     * Retries information regarding an edge, and returns it as a String. This
     * method is for logging purposes.
     *
     * @param edge The edge about which information shall be retrieved.
     * @return Returns a String with information regarding the edge, including the
     *         source and destination vertices. The format is:<br>
     *         <p/>
     *         <pre>
     *         '&lt;EDGE LABEL&gt;', INDEX=x ('&lt;SOURCE VERTEX LABEL&gt;', INDEX=y -&gt; '&lt;DEST VERTEX LABEL&gt;', INDEX=z)
     *         </pre>
     *         <p/>
     *         Where x, y and n are the unique indexes for the edge, the source
     *         vertex and the destination vertex.<br>
     *         Please note that the label of an edge can be either null, or empty
     *         ("");
     */
    private static String getCompleteEdgeName(final Edge edge) {
        return "Edge: '" + edge.getLabelKey() + "', INDEX=" + edge.getIndexKey();
    }

    /**
     * Retries information regarding a vertex, and returns it as a String. This
     * method is for logging purposes.
     *
     * @param vertex The vertex about which information shall be retrieved.
     * @return Returns a String with information regarding the vertex. The format
     *         is:<br>
     *         <p/>
     *         <pre>
     *         '&lt;VERTEX LABEL&gt;', INDEX=n
     *         </pre>
     *         <p/>
     *         Where is the unique index for the vertex.
     */
    private static String getCompleteVertexName(final Vertex vertex) {
        return "Vertex: '" + vertex.getLabelKey() + "', INDEX=" + vertex.getIndexKey();
    }

    /**
     * <p>AbortIf.</p>
     *
     * @param bool a boolean.
     * @param message a {@link java.lang.String} object.
     */
    public static void AbortIf(final boolean bool, final String message) {
        if (bool) {
            throw new RuntimeException(message);
        }
    }

    /**
     * <p>setupLogger.</p>
     *
     * @param classParam a {@link java.lang.Class} object.
     * @return a {@link org.apache.log4j.Logger} object.
     */
    public static Logger setupLogger(@SuppressWarnings("rawtypes") final Class classParam) {
        Logger logger = Logger.getLogger(classParam);
        if (new File("graphwalker.properties").canRead()) {
            PropertyConfigurator.configure("graphwalker.properties");
        } else {
            PropertyConfigurator.configure(classParam.getResource("/graphwalker.properties"));
        }
        return logger;
    }

    /**
     * Creates an adds a vertex to a graph.
     *
     * @param graph    The graph to which a vertex is to be added.
     * @param strLabel The label of the vertex.
     * @return The newly created vertex.
     */
    public static Vertex addVertexToGraph(final Graph graph, final String strLabel) {
        Vertex retur = new Vertex();
        retur.setIndexKey(graph.getEdgeCount() + graph.getVertexCount() + 1);
        if (strLabel != null) {
            retur.setLabelKey(strLabel);
        }
        graph.addVertex(retur);
        return retur;
    }

    /**
     * Creates and adds an edge to a graph.
     *
     * @param graph        The graph to which the edge is to be added.
     * @param vertexFrom   The source point of the edge.
     * @param vertexTo     The destination point of the edge.
     * @param strLabel     The label of the edge.
     * @param strParameter The parameter(s) to be passed to the method implementing the edge
     *                     in a test.
     * @param strGuard     The guard of the edge.
     * @param strAction    The action to be performed.
     * @return The newly created edge.
     */
    public static Edge addEdgeToGraph(final Graph graph, final Vertex vertexFrom, final Vertex vertexTo, final String strLabel,
                                      final String strParameter, final String strGuard, final String strAction) {
        Edge retur = new Edge();
        retur.setIndexKey(graph.getEdgeCount() + graph.getVertexCount() + 1);
        if (strLabel != null) {
            retur.setLabelKey(strLabel);
        }
        if (strParameter != null) {
            retur.setParameterKey(strParameter);
        }
        if (strGuard != null) {
            retur.setGuardKey(strGuard);
        }
        if (strAction != null) {
            retur.setActionsKey(strAction);
        }
        graph.addEdge(retur, vertexFrom, vertexTo);
        return retur;
    }

    /**
     * Adds a stop condition for the model.
     *
     * @param machine a {@link org.graphwalker.core.machines.FiniteStateMachine} object.
     * @param conditionType  The condition type.
     * @param conditionValue The value of the condition.
     * @return The newly created stop condition.
     * @throws org.graphwalker.core.exceptions.StopConditionException if any.
     */
    public static StopCondition getCondition(final FiniteStateMachine machine, final int conditionType, final String conditionValue)
            throws StopConditionException {
        StopCondition condition = null;
        try {
            switch (conditionType) {
                case Keywords.CONDITION_EDGE_COVERAGE:
                    condition = new EdgeCoverage(Double.parseDouble(conditionValue) / 100);
                    break;
                case Keywords.CONDITION_REACHED_EDGE:
                    if (conditionValue == null || conditionValue.isEmpty()) {
                        throw new StopConditionException("The name of reached edge must not be empty");
                    }
                    if (machine != null && machine.findEdge(Edge.getLabelAndParameter(conditionValue)[0]) == null) {
                        throw new StopConditionException("The name of reached edge: '" + Edge.getLabelAndParameter(conditionValue)[0]
                                + "' (in stop condition: '" + conditionValue + "') does not exists in the model.");
                    }
                    condition = new ReachedEdge(conditionValue);
                    break;
                case Keywords.CONDITION_REACHED_VERTEX:
                    if (conditionValue == null || conditionValue.isEmpty()) {
                        throw new StopConditionException("The name of reached vertex must not be empty");
                    }
                    if (machine != null && machine.getModel().findVertex(Vertex.getLabel(conditionValue)) == null) {
                        throw new StopConditionException("The name of reached vertex: '" + Vertex.getLabel(conditionValue) + "' (in stop condition: '"
                                + conditionValue + "') does not exists in the model.");
                    }
                    condition = new ReachedVertex(conditionValue);
                    break;
                case Keywords.CONDITION_VERTEX_COVERAGE:
                    condition = new VertexCoverage(Double.parseDouble(conditionValue) / 100);
                    break;
                case Keywords.CONDITION_TEST_DURATION:
                    condition = new TimeDuration(Long.parseLong(conditionValue));
                    break;
                case Keywords.CONDITION_TEST_LENGTH:
                    condition = new TestCaseLength(Integer.parseInt(conditionValue));
                    break;
                case Keywords.CONDITION_NEVER:
                    condition = new NeverCondition();
                    break;
                case Keywords.CONDITION_REQUIREMENT_COVERAGE:
                    condition = new RequirementCoverage(Double.parseDouble(conditionValue) / 100);
                    break;
                case Keywords.CONDITION_REACHED_REQUIREMENT:
                    condition = new ReachedRequirement(conditionValue);
                    break;
                default:
                    throw new StopConditionException("Unsupported stop condition selected");
            }
        } catch (NumberFormatException e) {
            if (conditionValue == null || conditionValue.isEmpty()) {
                throw new StopConditionException("Stop condition value is missing. ");
            } else {
                throw new StopConditionException("Invalid stop condition value: " + conditionValue);
            }
        }
        return condition;
    }

    /**
     * <p>getGenerator.</p>
     *
     * @param generatorType a int.
     * @return a {@link org.graphwalker.core.generators.PathGenerator} object.
     * @throws org.graphwalker.core.exceptions.GeneratorException if any.
     */
    public static PathGenerator getGenerator(final int generatorType) throws GeneratorException {
        PathGenerator generator = null;

        switch (generatorType) {
            case Keywords.GENERATOR_RANDOM:
                generator = new RandomPathGenerator();
                break;

            case Keywords.GENERATOR_ALL_PATH_PERMUTATIONS:
                generator = new AllPathPermutationsGenerator();
                break;

            case Keywords.GENERATOR_A_STAR:
                generator = new A_StarPathGenerator();
                break;

            case Keywords.GENERATOR_STUB:
                generator = new CodeGenerator();
                break;

            case Keywords.GENERATOR_LIST:
                generator = new ListGenerator();
                break;

            case Keywords.GENERATOR_REQUIREMENTS:
                generator = new RequirementsGenerator();
                break;

            case Keywords.GENERATOR_SHORTEST_NON_OPTIMIZED:
                generator = new NonOptimizedShortestPath();
                break;

            default:
                throw new GeneratorException("Unsupported generator selected.");
        }

        Util.logger.debug("Added generator: " + generator + "  " + generatorType);

        return generator;
    }

    /**
     * <p>Constructor for Util.</p>
     */
    public Util() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Load MBT settings from a xml file
     *
     * @param file a {@link java.io.File} object.
     * @throws org.graphwalker.core.exceptions.StopConditionException if any.
     * @throws org.graphwalker.core.exceptions.GeneratorException if any.
     * @throws java.io.IOException if any.
     * @throws org.jdom.JDOMException if any.
     * @throws java.lang.InterruptedException if any.
     * @return a {@link org.graphwalker.core.ModelBasedTesting} object.
     */
    public static ModelBasedTesting loadMbtAsWSFromXml(final File file) throws StopConditionException, GeneratorException, IOException,
            JDOMException, InterruptedException {
        return loadXml(file, true, false, false);
    }

    /**
     * Load MBT settings from a xml file
     *
     * @param file a {@link java.io.File} object.
     * @param dryRun Is mbt to be run in a dry run mode?
     * @throws org.graphwalker.core.exceptions.StopConditionException if any.
     * @throws org.graphwalker.core.exceptions.GeneratorException if any.
     * @throws java.io.IOException if any.
     * @throws org.jdom.JDOMException if any.
     * @throws java.lang.InterruptedException if any.
     * @return a {@link org.graphwalker.core.ModelBasedTesting} object.
     */
    public static ModelBasedTesting loadMbtFromXml(final File file, final boolean dryRun) throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
        return loadXml(file, false, dryRun, false);
    }

    /**
     * Load MBT settings from a xml file
     *
     * @param file The XML settings file
     * @throws org.graphwalker.core.exceptions.StopConditionException if any.
     * @throws org.graphwalker.core.exceptions.GeneratorException if any.
     * @throws java.io.IOException if any.
     * @throws org.jdom.JDOMException if any.
     * @throws java.lang.InterruptedException if any.
     * @return a {@link org.graphwalker.core.ModelBasedTesting} object.
     */
    public static ModelBasedTesting loadMbtFromXml(final File file) throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
        return loadXml(file, false, false, false);
    }

    /**
     * Creates a new instance of MBT and loads the settings from a xml file
     *
     * @param file The XML settings file
     * @throws org.graphwalker.core.exceptions.StopConditionException if any.
     * @throws org.graphwalker.core.exceptions.GeneratorException if any.
     * @throws java.io.IOException if any.
     * @throws org.jdom.JDOMException if any.
     * @throws java.lang.InterruptedException if any.
     * @return a {@link org.graphwalker.core.ModelBasedTesting} object.
     */
    public static ModelBasedTesting getNewMbtFromXml(final File file) throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
        return loadXml(file, false, false, true);
    }

    /**
     * Load MBT settings from a xml file
     *
     * @param file The XML settings file
     * @throws org.graphwalker.core.exceptions.StopConditionException if any.
     * @throws org.graphwalker.core.exceptions.GeneratorException if any.
     * @throws java.io.IOException if any.
     * @throws org.jdom.JDOMException if any.
     * @throws java.lang.InterruptedException if any.
     * @return a {@link org.graphwalker.core.ModelBasedTesting} object.
     */
    public ModelBasedTesting loadMbtFromXmlNonStatic(final File file) throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
        return loadXmlNonStatic(file, false, false);
    }

    /**
     * Load MBT settings from a xml file
     *
     * @param file                The XML settings file
     * @param runningSoapServices Is mbt to run in Web Services mode?
     * @param dryRun              Is mbt to be run in a dry run mode?
     * @param generateNewModel
     * @return
     * @throws StopConditionException
     * @throws GeneratorException
     * @throws IOException
     * @throws JDOMException
     * @throws InterruptedException
     */
    @SuppressWarnings("unchecked")
    private static ModelBasedTesting loadXml(final File file, final boolean runningSoapServices, final boolean dryRun, final boolean generateNewModel) throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {

        final ModelBasedTesting mbt = new ModelBasedTesting();
        mbt.setDryRun(dryRun);

        SAXBuilder parser = new SAXBuilder();
        Document doc;
        doc = parser.build(file);
        Element root = doc.getRootElement();
        List<Element> models = root.getChildren("MODEL");

        if (models.size() == 0) {
            throw new RuntimeException("Model is missing from XML");
        }

        for (Element element : models) {
            mbt.readGraph((element).getAttributeValue("PATH"));
        }

        List<Element> classPath = root.getChildren("CLASS");
        for (Element element : classPath) {
            String classPaths[] = element.getAttributeValue("PATH").split(":");
            for (String classPath2 : classPaths) {
                try {
                    ClassPathHack.addFile(getFile(classPath2));
                    Util.logger.debug("Added to classpath: " + classPath2);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage() + "\nCould not add: '" + classPath2 + "' to CLASSPATH\n"
                            + "Please review your xml file: '" + file + "' at CLASS PATH", e);
                }
            }
        }

        if (root.getAttributeValue("SCRIPT_ENGINE") != null && root.getAttributeValue("SCRIPT_ENGINE").equalsIgnoreCase("js")) {
            Util.logger.debug("Enabling JavaScript engine");
            mbt.enableJsScriptEngine(true);
        } else {
            Util.logger.debug("Using BeanShell script engine, if EFSM is enabled.");
            mbt.enableJsScriptEngine(false);
        }

        if (root.getAttributeValue("EXTENDED") != null && root.getAttributeValue("EXTENDED").equalsIgnoreCase("true")) {
            Util.logger.debug("Enabling extended FSM");
            mbt.enableExtended(true);
            mbt.setStartupScript(getScriptContent(root.getChildren("SCRIPT")));
        } else {
            Util.logger.debug("Disabling extended FSM");
            mbt.enableExtended(false);
        }

        if (root.getAttributeValue("WEIGHT") != null && root.getAttributeValue("WEIGHT").equalsIgnoreCase("true")) {
            Util.logger.debug("Using weighted edges");
            mbt.setWeighted(true);
        } else {
            Util.logger.debug("Will not use weighted edges");
            mbt.setWeighted(false);
        }

        List<Element> generators = root.getChildren("GENERATOR");

        if (generators.size() == 0) {
            throw new RuntimeException("Generator is missing from XML");
        }

        PathGenerator generator;
        if (generators.size() > 1) {
            generator = new CombinedPathGenerator();
            for (Element element : generators) {
                ((CombinedPathGenerator) generator).addPathGenerator(getGenerator(mbt.getMachine(), element));
            }
        } else {
            generator = getGenerator(mbt.getMachine(), generators.get(0));
        }
        if (generator == null) {
            throw new RuntimeException("Failed to set generator");
        }
        mbt.setGenerator(generator);
        mbt.populateMachineRequirementHashTable();
        final String reportName = root.getAttributeValue("REPORT");
        String reportTemplate = root.getAttributeValue("REPORT-TEMPLATE");

        if (reportName != null && reportTemplate != null) {
            Util.logger.debug("Will use report template: " + reportTemplate);
            mbt.getStatisticsManager().setReportTemplate(new FileInputStream(new File(reportTemplate)));
        }

        String logInterval = root.getAttributeValue("LOG-INTERVAL");

        if (logInterval != null) {
            long seconds = Integer.valueOf(logInterval).longValue();
            TimerTask logTask;
            if (reportName != null && reportTemplate != null) {
                logTask = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            mbt.getStatisticsManager().writeFullReport(new PrintStream(reportName));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException("Could not open or write report file '" + reportName + "'", e);
                        }
                    }
                };
            } else {
                logTask = new TimerTask() {
                    @Override
                    public void run() {
                        Util.logger.info(mbt.getStatisticsCompact());
                    }
                };
            }
            Util.timer = new Timer();
            Util.timer.schedule(logTask, 500, seconds * 1000);
        }

        if (!runningSoapServices) {
            try {

                String executor = root.getAttributeValue("EXECUTOR");
                Util.logger.debug("Executor is: " + executor);
                String executorParam = null;
                if (executor.contains(":")) {
                    executorParam = executor.substring(executor.indexOf(':') + 1);
                    executor = executor.substring(0, executor.indexOf(':'));
                }

                if (executor.equalsIgnoreCase("offline")) {
                    PrintStream out = System.out;
                    if (executorParam != null && !executorParam.equals("")) {
                        try {
                            out = new PrintStream(executorParam);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException("file '" + executorParam + "' could not be created or is writeprotected.", e);
                        }
                    }
                    if (!mbt.isUseGUI()) {
                        mbt.writePath(out);
                        if (out != System.out) {
                            out.close();
                        }
                    }
                } else if (executor.equalsIgnoreCase("manual")) {
                    PrintStream out = System.out;
                    if (executorParam != null && !executorParam.equals("")) {
                        try {
                            out = new PrintStream(executorParam);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException("file '" + executorParam + "' could not be created or is writeprotected.", e);
                        }
                    }
                    if (!mbt.isUseGUI()) {
                        Vector<String[]> testSequence = new Vector<String[]>();
                        mbt.writePath(testSequence);

                        new PrintHTMLTestSequence(testSequence, out);
                        if (out != System.out) {
                            out.close();
                        }
                    }
                } else if (executor.equalsIgnoreCase("java")) {
                    if (executorParam == null || executorParam.equals("")) {
                        throw new RuntimeException("No java class specified for execution");
                    }

                    if (!mbt.isUseGUI()) {
                        mbt.executePath(executorParam);
                    } else {
                        mbt.setJavaExecutorClass(executorParam);
                    }
                } else if (executor.equalsIgnoreCase("online")) {
                    if (mbt.isDryRun()) {
                        Util.logger.debug("Executing a dry run");
                        mbt.executePath(null, null);
                    }
                    if (!mbt.isUseGUI()) {
                        mbt.interractivePath();
                    }

                } else if (executor.equalsIgnoreCase("none") || executor.equals("")) {
                    // no execution (for debug purpose)
                } else {
                    throw new RuntimeException("Unknown executor '" + executor + "'");
                }

            } finally {
                if (Util.timer != null) {
                    Util.timer.cancel();
                }
                if (reportName != null && reportTemplate != null && !mbt.isUseGUI()) {
                    mbt.getStatisticsManager().writeFullReport(reportName);
                }
            }
        }
        return mbt;
    }

    @SuppressWarnings("unchecked")
    private ModelBasedTesting loadXmlNonStatic(final File file, final boolean runningSoapServices, final boolean dryRun) throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
        final ModelBasedTesting mbt = new ModelBasedTesting();
        mbt.setDryRun(dryRun);

        SAXBuilder parser = new SAXBuilder();
        Document doc;
        doc = parser.build(file);
        Element root = doc.getRootElement();
        List<Element> models = root.getChildren("MODEL");

        if (models.size() == 0) {
            throw new RuntimeException("Model is missing from XML");
        }

        for (Element element : models) {
            mbt.readGraph((element).getAttributeValue("PATH"));
        }

        List<Element> classPath = root.getChildren("CLASS");
        for (Element element : classPath) {
            String classPaths[] = element.getAttributeValue("PATH").split(":");
            for (String classPath2 : classPaths) {
                try {
                    ClassPathHack.addFile(getFile(classPath2));
                    Util.logger.debug("Added to classpath: " + classPath2);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage() + "\nCould not add: '" + classPath2 + "' to CLASSPATH\n"
                            + "Please review your xml file: '" + file + "' at CLASS PATH", e);
                }
            }
        }

        if (root.getAttributeValue("SCRIPT_ENGINE") != null && root.getAttributeValue("SCRIPT_ENGINE").equalsIgnoreCase("js")) {
            Util.logger.debug("Enabling JavaScript engine");
            mbt.enableJsScriptEngine(true);
        } else {
            Util.logger.debug("Using BeanShell script engine, if EFSM is enabled.");
            mbt.enableJsScriptEngine(false);
        }

        if (root.getAttributeValue("EXTENDED") != null && root.getAttributeValue("EXTENDED").equalsIgnoreCase("true")) {
            Util.logger.debug("Enabling extended FSM");
            mbt.enableExtended(true);
            mbt.setStartupScript(getScriptContent(root.getChildren("SCRIPT")));
        } else {
            Util.logger.debug("Disabling extended FSM");
            mbt.enableExtended(false);
        }

        if (root.getAttributeValue("WEIGHT") != null && root.getAttributeValue("WEIGHT").equalsIgnoreCase("true")) {
            Util.logger.debug("Using weighted edges");
            mbt.setWeighted(true);
        } else {
            Util.logger.debug("Will not use weighted edges");
            mbt.setWeighted(false);
        }

        List<Element> generators = root.getChildren("GENERATOR");

        if (generators.size() == 0) {
            throw new RuntimeException("Generator is missing from XML");
        }

        PathGenerator generator;
        if (generators.size() > 1) {
            generator = new CombinedPathGenerator();
            for (Element element : generators) {
                ((CombinedPathGenerator) generator).addPathGenerator(getGenerator(mbt.getMachine(), element));
            }
        } else {
            generator = getGenerator(mbt.getMachine(), generators.get(0));
        }
        if (generator == null) {
            throw new RuntimeException("Failed to set generator");
        }
        mbt.setGenerator(generator);

        final String reportName = root.getAttributeValue("REPORT");
        String reportTemplate = root.getAttributeValue("REPORT-TEMPLATE");

        if (reportName != null && reportTemplate != null) {
            Util.logger.debug("Will use report template: " + reportTemplate);
            mbt.getStatisticsManager().setReportTemplate(new FileInputStream(new File(reportTemplate)));
        }

        String logInterval = root.getAttributeValue("LOG-INTERVAL");

        if (logInterval != null) {
            long seconds = Integer.valueOf(logInterval).longValue();
            TimerTask logTask;
            if (reportName != null && reportTemplate != null) {
                logTask = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            mbt.getStatisticsManager().writeFullReport(new PrintStream(reportName));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException("Could not open or write report file '" + reportName + "'", e);
                        }
                    }
                };
            } else {
                logTask = new TimerTask() {
                    @Override
                    public void run() {
                        Util.logger.info(mbt.getStatisticsCompact());
                    }
                };
            }
            Util.timer = new Timer();
            Util.timer.schedule(logTask, 500, seconds * 1000);
        }

        if (!runningSoapServices) {
            try {

                String executor = root.getAttributeValue("EXECUTOR");
                Util.logger.debug("Executor is: " + executor);
                String executorParam = null;
                if (executor.contains(":")) {
                    executorParam = executor.substring(executor.indexOf(':') + 1);
                    executor = executor.substring(0, executor.indexOf(':'));
                }

                if (executor.equalsIgnoreCase("offline")) {
                    PrintStream out = System.out;
                    if (executorParam != null && !executorParam.equals("")) {
                        try {
                            out = new PrintStream(executorParam);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException("file '" + executorParam + "' could not be created or is writeprotected.", e);
                        }
                    }
                    if (!mbt.isUseGUI()) {
                        mbt.writePath(out);
                        if (out != System.out) {
                            out.close();
                        }
                    }
                } else if (executor.equalsIgnoreCase("manual")) {
                    PrintStream out = System.out;
                    if (executorParam != null && !executorParam.equals("")) {
                        try {
                            out = new PrintStream(executorParam);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException("file '" + executorParam + "' could not be created or is writeprotected.", e);
                        }
                    }
                    if (!mbt.isUseGUI()) {
                        Vector<String[]> testSequence = new Vector<String[]>();
                        mbt.writePath(testSequence);

                        new PrintHTMLTestSequence(testSequence, out);
                        if (out != System.out) {
                            out.close();
                        }
                    }
                } else if (executor.equalsIgnoreCase("java")) {
                    if (executorParam == null || executorParam.equals("")) {
                        throw new RuntimeException("No java class specified for execution");
                    }

                    if (!mbt.isUseGUI()) {
                        mbt.executePath(executorParam);
                    } else {
                        mbt.setJavaExecutorClass(executorParam);
                    }
                } else if (executor.equalsIgnoreCase("online")) {
                    if (mbt.isDryRun()) {
                        Util.logger.debug("Executing a dry run");
                        mbt.executePath(null, null);
                    }
                    if (!mbt.isUseGUI()) {
                        mbt.interractivePath();
                    }

                    // } else if (executor.equalsIgnoreCase("none") ||
                    // executor.equals("")) {
                    // no execution (for debug purpose)
                } else {
                    throw new RuntimeException("Unknown executor '" + executor + "'");
                }

            } finally {
                if (Util.timer != null) {
                    Util.timer.cancel();
                }
                if (reportName != null && reportTemplate != null && !mbt.isUseGUI()) {
                    mbt.getStatisticsManager().writeFullReport(reportName);
                }
            }
        }
        return mbt;
    }

    private static String getScriptContent(final List<Element> scripts) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (Element element : scripts) {
            String internal = element.getTextTrim();
            if (internal != null && !internal.equals("")) {
                stringBuilder.append(internal);
                stringBuilder.append(System.getProperty("line.separator"));
            }
            String external = element.getAttributeValue("PATH");
            if (external != null && !external.equals("")) {
                stringBuilder.append(readFile(Util.getFile(external)));
                stringBuilder.append(System.getProperty("line.separator"));
            }
        }
        return stringBuilder.toString();
    }

    @SuppressWarnings("unchecked")
    private static PathGenerator getGenerator(final FiniteStateMachine machine, final Element generator) throws StopConditionException, GeneratorException, IOException {
        int generatorType = Keywords.getGenerator(generator.getAttributeValue("TYPE"));
        PathGenerator generatorObject = getGenerator(generatorType);
        if (generatorObject instanceof CodeGenerator) {
            String[] template = {"", "", ""};
            String templateFile = generator.getAttributeValue("VALUE");
            if (templateFile != null) {
                template[1] = readFile(Util.getFile(templateFile.trim()));
            } else {
                Element templateElement = generator.getChild("TEMPLATE");
                if (templateElement != null) {
                    template[0] = templateElement.getChildTextTrim("HEADER");
                    template[1] = templateElement.getChildTextTrim("BODY");
                    template[2] = templateElement.getChildTextTrim("FOOTER");
                } else {
                    throw new RuntimeException("No Template is specified for the stub generator.");
                }
            }
            ((CodeGenerator) generatorObject).setTemplate(template);
        } else {
            StopCondition stopCondition = getCondition(machine, generator.getChildren());
            if (stopCondition != null) {
                generatorObject.setStopCondition(stopCondition);
            }
        }
        return generatorObject;
    }

    private static StopCondition getCondition(final FiniteStateMachine machine, final List<Element> conditions) throws StopConditionException {
        StopCondition condition = null;
        if (conditions.size() > 1) {
            condition = new CombinationalCondition();
            for (Element element : conditions) {
                ((CombinationalCondition) condition).add(getCondition(machine, element));
            }
        } else if (conditions.size() == 1) {
            condition = getCondition(machine, conditions.get(0));
        }
        return condition;
    }

    @SuppressWarnings("unchecked")
    private static StopCondition getCondition(final FiniteStateMachine machine, final Element condition) throws StopConditionException {
        StopCondition stopCondition = null;
        if (condition.getName().equalsIgnoreCase("AND")) {
            stopCondition = new CombinationalCondition();
            for (Element element : (Iterable<Element>) condition.getChildren()) {
                ((CombinationalCondition) stopCondition).add(getCondition(machine, element));
            }
        } else if (condition.getName().equalsIgnoreCase("OR")) {
            stopCondition = new AlternativeCondition();
            for (Element element : (Iterable<Element>) condition.getChildren()) {
                ((AlternativeCondition) stopCondition).add(getCondition(machine, element));
            }
        } else if (condition.getName().equalsIgnoreCase("CONDITION")) {
            int type = Keywords.getStopCondition(condition.getAttributeValue("TYPE"));
            String value = condition.getAttributeValue("VALUE");
            stopCondition = getCondition(machine, type, value);
        }
        return stopCondition;
    }

    /**
     * <p>readFile.</p>
     *
     * @param file a {@link java.io.File} object.
     * @return a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public static String readFile(final File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            while (bufferedReader.ready()) {
                stringBuilder.append(bufferedReader.readLine());
                stringBuilder.append(System.getProperty("line.separator"));
            }
            bufferedReader.close();
        } catch (IOException e) {
            throw e; // TODO: remove the IOE from method signature and throw a new
            // exception extending runtime
        } finally {
            closeQuietly(reader);
        }
        return stringBuilder.toString();
    }

    /**
     * <p>getInput.</p>
     *
     * @return a char.
     */
    public static char getInput() {
        char c = 0;
        try {
            while (c != '0' && c != '1' && c != '2') {
                int tmp = System.in.read();
                c = (char) tmp;
            }
        } catch (IOException e) {
            //
        }
        return c;
    }

    /**
     * This functions shuffle the array, and returns the shuffled array
     *
     * @param array an array of {@link java.lang.Object} objects.
     * @return an array of {@link java.lang.Object} objects.
     */
    public static Object[] shuffle(final Object[] array) {
        for (int i = 0; i < array.length; i++) {
            Object leftObject = array[i];
            int index = Util.random.nextInt(array.length);
            Object rightObject = array[index];

            array[i] = rightObject;
            array[index] = leftObject;
        }
        return array;
    }

    /**
     * <p>getInternetAddr.</p>
     *
     * @param nic a {@link java.lang.String} object.
     * @return a {@link java.net.InetAddress} object.
     */
    public static InetAddress getInternetAddr(final String nic) {
        // Find the real network interface
        NetworkInterface iface = null;
        InetAddress ia = null;
        boolean foundNIC = false;
        try {
            for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements() && foundNIC == false; ) {
                iface = ifaces.nextElement();
                Util.logger.debug("Interface: " + iface.getDisplayName());
                for (Enumeration<InetAddress> ips = iface.getInetAddresses(); ips.hasMoreElements() && foundNIC == false; ) {
                    ia = ips.nextElement();
                    Util.logger.debug(ia.getCanonicalHostName() + " " + ia.getHostAddress());
                    if (!ia.isLoopbackAddress()) {
                        Util.logger.debug("  Not a loopback address...");
                        if (!ia.getHostAddress().contains(":") && nic.equals(iface.getDisplayName())) {
                            Util.logger.debug("  Host address does not contain ':'");
                            Util.logger.debug("  Interface: " + iface.getName() + " seems to be InternetInterface. I'll take it...");
                            foundNIC = true;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            Util.logger.error(e.getMessage());
        } finally {
            if (!foundNIC && nic != null) {
                Util.logger.error("Could not bind to network interface: " + nic);
                throw new RuntimeException("Could not bind to network interface: " + nic);
            } else if (!foundNIC) {
                Util.logger.error("Could not bind to any network interface");
                throw new RuntimeException("Could not bind to any network interface: ");
            }
        }
        return ia;
    }

    /**
     * <p>readWSPort.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String readWSPort() {
        PropertiesConfiguration conf = null;
        if (new File("graphwalker.properties").canRead()) {
            try {
                conf = new PropertiesConfiguration("graphwalker.properties");
            } catch (ConfigurationException e) {
                Util.logger.error(e.getMessage());
            }
        } else {
            conf = new PropertiesConfiguration();
            try {
                conf.load(Util.class.getResourceAsStream("/graphwalker.properties"));
            } catch (ConfigurationException e) {
                Util.logger.error(e.getMessage());
            }
        }
        String port = conf.getString("graphwalker.ws.port");
        Util.logger.debug("Read graphwalker.ws.port from graphwalker.properties: " + port);
        if (port == null) {
            port = "9090";
            Util.logger.debug("Setting port to: 9090");
        }
        return port;
    }

    /**
     * <p>readSoapGuiStartupState.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public static Boolean readSoapGuiStartupState() {
        PropertiesConfiguration conf = null;
        if (new File("graphwalker.properties").canRead()) {
            try {
                conf = new PropertiesConfiguration("graphwalker.properties");
            } catch (ConfigurationException e) {
                Util.logger.error(e.getMessage());
            }
        } else {
            conf = new PropertiesConfiguration();
            try {
                conf.load(Util.class.getResourceAsStream("/graphwalker.properties"));
            } catch (ConfigurationException e) {
                Util.logger.error(e.getMessage());
            }
        }
        Boolean soapGuiState = false;
        try {
            soapGuiState = conf.getBoolean("org.graphwalker.GUI.startSOAP");
        } catch (NoSuchElementException e) {
            Util.logger.debug("org.graphwalker.GUI.startSOAP not found in graphwalker.properties");
            soapGuiState = false;
        }
        Util.logger.debug("Read org.graphwalker.GUI.startSOAP from graphwalker.properties: " + soapGuiState);
        return soapGuiState;
    }

    /**
     * <p>logStackTraceToError.</p>
     *
     * @param e a {@link java.lang.Exception} object.
     */
    public static void logStackTraceToError(final Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();
        Util.logger.error(sw.toString());
    }

    /**
     * <p>logStackTraceToError.</p>
     *
     * @param t a {@link java.lang.Throwable} object.
     */
    public static void logStackTraceToError(final Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.close();
        Util.logger.error(sw.toString());
    }

    /**
     * <p>getFile.</p>
     *
     * @param resourceName a {@link java.lang.String} object.
     * @return a {@link java.io.File} object.
     */
    public static File getFile(final String resourceName) {
        Util.logger.debug("Try to get file: " + resourceName);
        File file = new File(resourceName);
        if (file.exists()) {
            Util.logger.debug("File exists on file system.");
            return file;
        }
        ClassLoader cloader = Thread.currentThread().getContextClassLoader();
        Util.logger.debug("Class loader class name: " + cloader.getClass().getName());
        URL resource = cloader.getResource(resourceName);

        if (resource == null) {
            throw new IllegalArgumentException("Could not get resource: " + resourceName);
        }

        return new File(resource.getPath());
    }

    /**
     * <p>closeQuietly.</p>
     *
     * @param reader a {@link java.io.Reader} object.
     */
    public static void closeQuietly(Reader reader) {
        if (null != reader) {
            try {
                reader.close();
            } catch (Exception e) {
                // ignore all exceptions
            }
        }
    }

    /**
     * <p>closeQuietly.</p>
     *
     * @param inputStream a {@link java.io.InputStream} object.
     */
    public static void closeQuietly(InputStream inputStream) {
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (Exception e) {
                // ignore all exceptions
            }
        }
    }
}
