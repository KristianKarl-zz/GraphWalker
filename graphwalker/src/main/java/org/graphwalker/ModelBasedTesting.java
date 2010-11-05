//This file is part of the Model-based Testing java package
//Copyright (C) 2005  Kristian Karl
//
//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

package org.graphwalker;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.graphwalker.GUI.App;
import org.graphwalker.GUI.Status;
import org.graphwalker.conditions.AlternativeCondition;
import org.graphwalker.conditions.ReachedRequirement;
import org.graphwalker.conditions.StopCondition;
import org.graphwalker.conditions.TimeDuration;
import org.graphwalker.events.MbtEvent;
import org.graphwalker.exceptions.GeneratorException;
import org.graphwalker.exceptions.GuiStoppedExecution;
import org.graphwalker.exceptions.InvalidDataException;
import org.graphwalker.exceptions.StopConditionException;
import org.graphwalker.generators.CodeGenerator;
import org.graphwalker.generators.NonOptimizedShortestPath;
import org.graphwalker.generators.PathGenerator;
import org.graphwalker.graph.AbstractElement;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;
import org.graphwalker.io.AbstractModelHandler;
import org.graphwalker.io.GraphML;
import org.graphwalker.machines.ExtendedFiniteStateMachine;
import org.graphwalker.machines.FiniteStateMachine;
import org.graphwalker.statistics.EdgeCoverageStatistics;
import org.graphwalker.statistics.EdgeSequenceCoverageStatistics;
import org.graphwalker.statistics.RequirementCoverageStatistics;
import org.graphwalker.statistics.VertexCoverageStatistics;

/**
 * The object handles the test case generation, both online and offline.
 * 
 * @author krikar
 * 
 */
public class ModelBasedTesting {
	private static Logger logger = Util.setupLogger(ModelBasedTesting.class);

	private AbstractModelHandler modelHandler;
	private FiniteStateMachine machine;
	private StopCondition condition;
	private PathGenerator generator;
	private String[] template;
	private boolean useStatisticsManager = false;
	private boolean runRandomGeneratorOnce = false;
	private boolean dryRun = false;
	private boolean useJsScriptEngine = false;
	private boolean useGUI = false;
	private MbtEvent notifyApp = null;
	private String javaExecutorClass = null;
	private volatile Thread stopFlag;
	private volatile boolean threadSuspended = false;
	private Thread thisThread;
	private Future<?> future;

	/**
	 * Do not verify labels for edges and vertices. This is used when creating
	 * manual test sequences.
	 */
	private boolean manualTestSequence = false;

	public boolean isManualTestSequence() {
		return manualTestSequence;
	}

	public void setManualTestSequence(boolean manualTestSequence) {
		this.manualTestSequence = manualTestSequence;
	}

	// Private constructor prevents instantiation from other classes
	public ModelBasedTesting() {
	}

	/**
	 * ModelBasedTestingHolder is loaded on the first execution of
	 * ModelBasedTesting.getInstance() or the first access to
	 * ModelBasedTestingHolder.INSTANCE, not before.
	 */
	@SuppressWarnings("synthetic-access")
	private static class ModelBasedTestingHolder {
		private static final ModelBasedTesting INSTANCE = new ModelBasedTesting();
	}

	@SuppressWarnings("synthetic-access")
	public static ModelBasedTesting getInstance() {
		return ModelBasedTestingHolder.INSTANCE;
	}

	/**
	 * Clears everything. Removes any defined machine, generators, stop conditions
	 * etc.
	 */
	public void reset() {
		modelHandler = null;
		machine = null;
		condition = null;
		generator = null;
		template = null;
		runRandomGeneratorOnce = false;
		dryRun = false;
		javaExecutorClass = null;
		startupScript = "";
	}

	public void reload() {
		machine.setAllUnvisited();
	}

	public boolean isUseGUI() {
		return useGUI;
	}

	public void setUseGUI() {
		useGUI = true;
		notifyApp = App.getInstance();
	}

	public boolean isDryRun() {
		return dryRun;
	}

	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	private String startupScript = "";

	private StatisticsManager statisticsManager;

	private void setupStatisticsManager() {
		if (this.statisticsManager == null)
			this.statisticsManager = new StatisticsManager();
		this.statisticsManager.addStatisicsCounter("Vertex Coverage", new VertexCoverageStatistics(getGraph()));
		this.statisticsManager.addStatisicsCounter("Edge Coverage", new EdgeCoverageStatistics(getGraph()));
		this.statisticsManager.addStatisicsCounter("2-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics(getGraph(), 2));
		this.statisticsManager.addStatisicsCounter("3-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics(getGraph(), 3));
		this.statisticsManager.addStatisicsCounter("Requirements Coverage", new RequirementCoverageStatistics(getGraph()));
		this.statisticsManager.addProgress(getMachine().getCurrentVertex());
	}

	/**
	 * @return the statisticsManager
	 */
	public StatisticsManager getStatisticsManager() {
		if (this.statisticsManager == null) {
			this.statisticsManager = new StatisticsManager();
			if (this.machine != null)
				setupStatisticsManager();
		}
		return this.statisticsManager;
	}

	protected void addAlternativeCondition(int conditionType, String conditionValue) throws StopConditionException {
		StopCondition condition = null;
		condition = Util.getCondition(getMachine(), conditionType, conditionValue);

		// If requirement stop condition, check if requirement exists in model
		if (condition instanceof ReachedRequirement) {
			Collection<String> reqs = ((ReachedRequirement) condition).getRequirements();
			for (Iterator<String> iterator = reqs.iterator(); iterator.hasNext();) {
				String req = iterator.next();
				if (getMachine().getAllRequirements().containsKey(req) == false) {
					throw new StopConditionException("Requirement: '" + req + "' do not exist in the model");
				}
			}
		}

		if (getCondition() == null) {
			setCondition(new AlternativeCondition());
			((AlternativeCondition) getCondition()).add(condition);
		} else {
			if (!(getCondition() instanceof AlternativeCondition)) {
				StopCondition old = getCondition();
				setCondition(new AlternativeCondition());
				((AlternativeCondition) getCondition()).add(old);
			}
			((AlternativeCondition) getCondition()).add(condition);
		}
	}

	public void setCondition(StopCondition condition) {
		this.condition = condition;
		if (getGenerator() != null)
			getGenerator().setStopCondition(getCondition());
		if (this.machine != null)
			getCondition().setMachine(getMachine());
	}

	private StopCondition getCondition() {
		return this.condition;
	}

	public FiniteStateMachine getMachine() {
		if (this.machine == null) {
			setMachine(new FiniteStateMachine());
		}
		return this.machine;
	}

	private void setMachine(FiniteStateMachine machine) {
		this.machine = machine;
		if (this.modelHandler != null)
			getMachine().setModel(getGraph());
		if (getCondition() != null)
			getCondition().setMachine(machine);
		if (getGenerator() != null)
			getGenerator().setMachine(machine);
	}

	/**
	 * Return the instance of the graph
	 */
	public Graph getGraph() {
		if (this.modelHandler == null)
			return null;
		return this.modelHandler.getModel();
	}

	public void setGraph(Graph graph) {
		if (this.modelHandler == null) {
			this.modelHandler = new GraphML();
		}
		this.modelHandler.setModel(graph);
		if (this.machine != null)
			getMachine().setModel(graph);
	}
	
	/**
	 * Returns the current future object in the test.
	 */
	public Future<?> getFuture() {
		return future;
	}
	
	/**
	 * Sets the future for the model. The execution of the path will be paused until the future
	 * is done.
	 */
	public void setFuture(Future<?> future) {
		this.future = future;
	}

	/**
	 * Returns the value of an data object within the data space of the model.
	 * 
	 * @param data
	 *          The name of the data object, which value is to be retrieved.
	 * @return The value of the data object. The value is always returned a s
	 *         string. It is the calling parties task to parse the string and
	 *         convert it to correct type.
	 * @throws InvalidDataException
	 *           If the retrieval of the data fails, the InvalidDataException is
	 *           thrown. For example if a FiniteStateMachine is used, which has no
	 *           data space, the exception is thrown.
	 */
	public String getDataValue(String data) throws InvalidDataException {
		Util.AbortIf(this.machine == null, "No machine has been defined!");
		if (this.machine instanceof ExtendedFiniteStateMachine) {
			return ((ExtendedFiniteStateMachine) this.machine).getDataValue(data);
		}
		throw new InvalidDataException("Data can only be fetched from a ExtendedFiniteStateMachine. Please enable EFSM.");
	}

	/**
	 * Executes an action, and returns any outcome as a string.
	 * 
	 * @param action
	 *          The name of the data object and the method, which value is to be
	 *          retrieved.
	 * @return The value of the data object's method. The value is always returned
	 *         a s string. It is the calling parties task to parse the string and
	 *         convert it to correct type.
	 * @throws InvalidDataException
	 *           If the retrieval of the data fails, the InvalidDataException is
	 *           thrown. For example if a FiniteStateMachine is used, which has no
	 *           data space, the exception is thrown.
	 */
	public String execAction(String action) throws InvalidDataException {
		Util.AbortIf(this.machine == null, "No machine has been defined!");
		if (this.machine instanceof ExtendedFiniteStateMachine) {
			return ((ExtendedFiniteStateMachine) this.machine).execAction(action);
		}
		throw new InvalidDataException("Data can only be fetched from a ExtendedFiniteStateMachine. Please enable EFSM.");
	}

	/**
	 * Tells mbt that a requirement (if any), has passed or failed. MBT will look
	 * at the most recent edge or vertex and check if they have requirement. If
	 * none is found, then no action is taken. If req. is found mbt will log the
	 * information using the logger.
	 * 
	 * @param pass
	 *          Tells mbt if the requirement has pass (true), or failed (false).
	 */
	public void passRequirement(boolean pass) {
		Util.AbortIf(this.machine == null, "No machine has been defined!");
		Vertex v = getMachine().getCurrentVertex();
		if (!v.getReqTagKey().isEmpty()) {
			String str = "REQUIREMENT: '" + v.getReqTagKey() + "' has ";
			if (pass) {
				str += "PASSED, at " + v;
				if (v.getReqTagResult() == 0) {
					v.setReqTagResult(1);
				}
			} else {
				str += "FAILED, at " + v;
				if (v.getReqTagResult() != 2) {
					v.setReqTagResult(2);
				}
			}
			logger.info(str);
		}
	}

	protected void enableJsScriptEngine(boolean enableJs) {
		if (enableJs)
			useJsScriptEngine = true;
		else
			useJsScriptEngine = false;
	}

	public void enableExtended(boolean extended) {
		if (extended) {
			setMachine(new ExtendedFiniteStateMachine(useJsScriptEngine));
			if (!getStartupScript().equals("")) {
				logger.debug("Will now try to run script: " + getStartupScript());
				((ExtendedFiniteStateMachine) getMachine()).eval(getStartupScript());
			}
		} else {
			setMachine(new FiniteStateMachine());
		}
	}

	public void setGenerator(PathGenerator generator) {
		this.generator = generator;

		if (this.machine != null)
			getGenerator().setMachine(getMachine());
		if (this.template != null && this.generator instanceof CodeGenerator)
			((CodeGenerator) generator).setTemplate(this.template);
		if (getCondition() != null)
			getGenerator().setStopCondition(getCondition());
	}

	public void setGenerator(int generatorType) throws GeneratorException {
		setGenerator(Util.getGenerator(generatorType));
	}

	private PathGenerator getGenerator() {
		return this.generator;
	}

	public boolean hasNextStep() {
		if (this.machine == null)
			getMachine();
		Util.AbortIf(getGenerator() == null, "No generator has been defined!");
		return getGenerator().hasNext();
	}

	public String[] getNextStep() throws InterruptedException {
		Util.AbortIf(stopFlag != thisThread, "Execution of model has been stopped: " + getGraph());
		if (isUseGUI()) {

			while (true) {
				if (App.getInstance().getStatus().isStopped())
					throw new GuiStoppedExecution();

				if (App.getInstance().getStatus().isNext() || App.getInstance().getStatus().isRunning()
				    || App.getInstance().getStatus().isExecutingJavaTest() || App.getInstance().getStatus().isExecutingSoapTest()
				    && App.getInstance().getStatus().isPaused() == false) {
					break;
				}
				Thread.sleep(500);
			}
		}

		if (threadSuspended) {
			logger.debug("Execution is now suspended: " + getGraph().getLabelKey());
			synchronized (this) {
				while (threadSuspended)
					wait();
			}
			logger.debug("Executions is now resumed: " + getGraph().getLabelKey());
		}

		if (Thread.interrupted()) {
			throw new InterruptedException();
		}
		if (this.machine == null) {
			getMachine();
		}

		getStatisticsManager();
		Util.AbortIf(getGenerator() == null, "No generator has been defined!");

		PathGenerator backupGenerator = null;
		if (runRandomGeneratorOnce) {
			backupGenerator = getGenerator();
			try {
				setGenerator(Keywords.GENERATOR_RANDOM);
			} catch (GeneratorException e) {
				logger.error(e.getMessage());
				throw new RuntimeException("ERROR: " + e.getMessage(), e);
			}
		}

		try {
			return getGenerator().getNext();
		} catch (RuntimeException e) {
			logger.fatal(e.toString());
			throw new RuntimeException("ERROR: " + e.getMessage(), e);
		} finally {
			if (runRandomGeneratorOnce) {
				runRandomGeneratorOnce = false;
				setGenerator(backupGenerator);
			}
			if (notifyApp != null) {
				notifyApp.getNextEvent();
			}
			if (isUseGUI()) {
				if (App.getInstance().getStatus().isStopped())
					new GuiStoppedExecution();

				if (App.getInstance().getStatus().isNext()) {
					App.getInstance().getStatus().unsetState(Status.next);
					App.getInstance().setButtons();
				}
			}
		}
	}

	public String getCurrentVertexName() {
		if (this.machine != null)
			return getMachine().getCurrentVertexName();
		logger.warn("Trying to retrieve current vertex without specifying machine");
		return "";
	}

	public String getCurrentEdgeName() {
		if (this.machine != null)
			return getMachine().getLastEdgeName();
		logger.warn("Trying to retrieve current vertex without specifying machine");
		return "";
	}

	public void readGraph(String graphmlFileName) {
		if (this.modelHandler == null) {
			this.modelHandler = new GraphML();
		}
		this.modelHandler.load(graphmlFileName);

		if (this.machine != null) {
			getMachine().setModel(getGraph());
		}

		if (isUseGUI() && App.getInstance() != null) {
			App.getInstance().updateLayout();
		}
	}

	protected void writeModel(PrintStream ps, boolean printIndex) {
		this.modelHandler.save(ps, printIndex);
	}

	public String getStatisticsString() {
		if (this.machine != null) {
			return getMachine().getStatisticsString();
		}
		logger.warn("Trying to retrieve statistics without specifying machine");
		return "";
	}

	public String getVersionString() {
		Properties properties = new Properties();
		try {
			properties.load(getClass().getResourceAsStream("/org/graphwalker/resources/version.properties"));
			return properties.getProperty("version.major") + "." + properties.getProperty("version.minor") + "."
			    + properties.getProperty("version.fix");
		} catch (IOException e) {
			Util.logStackTraceToError(e);
		}
		return "";
	}

	public String getStatisticsCompact() {
		if (this.machine != null) {
			return getMachine().getStatisticsStringCompact();
		}
		logger.warn("Trying to retrieve compact statistics without specifying machine");
		return "";
	}

	public String getStatisticsVerbose() {
		if (this.machine != null) {
			return getMachine().getStatisticsVerbose();
		}
		logger.warn("Trying to retrieve verbose statistics without specifying machine");
		return "";
	}

	public void setTemplate(String[] template) {
		this.template = template;

		if (getGenerator() != null && getGenerator() instanceof CodeGenerator)
			((CodeGenerator) getGenerator()).setTemplate(this.template);

	}

	public void setTemplate(String templateFile) throws IOException {
		String template = Util.readFile(Util.getFile(templateFile));
		String header = "", body = "", footer = "";
		Pattern p = Pattern.compile("HEADER<\\{\\{([.\\s\\S]+)\\}\\}>HEADER([.\\s\\S]+)FOOTER<\\{\\{([.\\s\\S]+)\\}\\}>FOOTER");
		Matcher m = p.matcher(template);
		if (m.find()) {
			header = m.group(1);
			body = m.group(2);
			footer = m.group(3);
			setTemplate(new String[] { header, body, footer });
		} else {
			setTemplate(new String[] { "", template, "" });
		}
	}

	protected void interractivePath() throws InterruptedException {
		interractivePath(System.in);
	}

	private void interractivePath(InputStream in) throws InterruptedException {
		Vector<String> stepPair = new Vector<String>();
		String req = "";

		for (char input = '0'; true; input = Util.getInput()) {
			logger.debug("Recieved: '" + input + "'");

			switch (input) {
			case '2':
				return;
			case '0':

				if (!hasNextStep() && (stepPair.size() == 0)) {
					return;
				}
				if (stepPair.size() == 0) {
					stepPair = new Vector<String>(Arrays.asList(getNextStep()));
					req = getRequirement(getMachine().getLastEdge());
				} else {
					req = getRequirement(getMachine().getCurrentVertex());
				}

				if (req.length() > 0) {
					req = "/" + req;
				}

				System.out.print((String) stepPair.remove(0) + req);

				String addInfo = "";
				System.out.println();

				if (stepPair.size() == 1) {
					logExecution(getMachine().getLastEdge(), addInfo);
					if (isUseStatisticsManager()) {
						getStatisticsManager().addProgress(getMachine().getLastEdge());
					}
				} else {
					logExecution(getMachine().getCurrentVertex(), addInfo);
					if (isUseStatisticsManager()) {
						getStatisticsManager().addProgress(getMachine().getCurrentVertex());
					}
				}

				break;

			default:
				throw new RuntimeException("Unsupported input recieved.");
			}
		}
	}

	private String getRequirement(AbstractElement element) {
		String req = "";
		if (element instanceof Edge) {
			if (!getMachine().getLastEdge().getReqTagKey().isEmpty()) {
				req = "REQUIREMENT: '" + getMachine().getLastEdge().getReqTagKey() + "'";
			}
			return req;
		} else if (element instanceof Vertex) {
			if (!getMachine().getCurrentVertex().getReqTagKey().isEmpty()) {
				req = "REQUIREMENT: '" + getMachine().getCurrentVertex().getReqTagKey() + "'";
			}
			return req;
		}
		return "";
	}

	protected void logExecution(AbstractElement element, String additionalInfo) {
		String req = " " + getRequirement(element);
		if (element instanceof Edge) {
			logger.info(getMachine().getLastEdge() + req + additionalInfo);
			return;
		} else if (element instanceof Vertex) {
			logger.info(getMachine().getCurrentVertex() + req
			    + (getMachine().hasInternalVariables() ? " DATA: " + getMachine().getCurrentDataString() : "") + additionalInfo);
			return;
		}
	}

	public void setJavaExecutorClass(String executorClass) {
		javaExecutorClass = executorClass;
	}

	public String getJavaExecutorClass() {
		return javaExecutorClass;
	}

	public void executePath() throws InterruptedException {
		if (getJavaExecutorClass() != null) {
			logger.debug("Start executing, using the java class: " + getJavaExecutorClass());
			executePath(getJavaExecutorClass());
			return;
		}

		while (hasNextStep()) {
			String[] stepPair = getNextStep();

			if (stepPair[0].trim() != "") {
				logExecution(getMachine().getLastEdge(), "");
				if (isUseStatisticsManager()) {
					getStatisticsManager().addProgress(getMachine().getLastEdge());
				}
			}
			if (stepPair[1].trim() != "") {
				logExecution(getMachine().getCurrentVertex(), "");
				if (isUseStatisticsManager()) {
					getStatisticsManager().addProgress(getMachine().getCurrentVertex());
				}
			}
		}
	}

	public void executePath(Class<?> clsClass) throws InterruptedException {
		if (clsClass == null)
			throw new RuntimeException("Needed execution class is missing as parameter.");
		executePath(clsClass, null);
	}

	public void executePath(Object objInstance) throws InterruptedException {
		if (objInstance == null)
			throw new RuntimeException("Needed execution instance is missing as parameter.");
		if (isUseGUI()) {
			App.getInstance().pause();
			App.getInstance().updateLayout();
		}
		executePath(null, objInstance);
	}

	protected void executePath(String strClassName) throws InterruptedException {
		if (getJavaExecutorClass() == null) {
			setJavaExecutorClass(strClassName);
		}

		if (isDryRun()) {
			logger.debug("Executing a dry run");
			executePath(null, null);
		} else {
			logger.debug("Executing a non-dry run");
		}

		if (strClassName == null || strClassName.trim().equals(""))
			throw new RuntimeException("Needed execution class name is missing as parameter.");
		Class<?> clsClass = null;

		logger.debug("Trying to get a class for name: " + strClassName);
		try {
			clsClass = Class.forName(strClassName);
			logger.debug("Got class for name: " + strClassName);
		} catch (LinkageError e) {
			String str = "Could not load class: " + e.getMessage() + "\nProblem occured when loading class: " + strClassName
			    + ".\n Current class path is: " + System.getProperty("java.class.path");
			logger.error(str);
			Util.logStackTraceToError(e);
			throw new RuntimeException(str, e);
		} catch (ClassNotFoundException e) {
			String str = "Could not load class: " + strClassName + ".\n Current class path is: " + System.getProperty("java.class.path");
			logger.error(str);
			throw new RuntimeException(str, e);
		}
		executePath(clsClass, null);
	}

	protected void executePath(Class<?> clsClass, Object objInstance) throws InterruptedException {
		thisThread = Thread.currentThread();
		stopFlag = thisThread;
		if (getGenerator().getStopCondition() instanceof TimeDuration) {
			((TimeDuration) getGenerator().getStopCondition()).restartTime();
		}

		if (this.machine == null)
			getMachine();

		if (isDryRun()) {
			logger.debug("Executing a dry run");
			while (hasNextStep()) {
				String[] stepPair = getNextStep();

				logExecution(getMachine().getLastEdge(), "");
				System.out.println("Do edge: " + getMachine().getLastEdge());
				System.out.println("Data: " + stepPair[0]);
				try {
					System.in.read();
				} catch (IOException e) {
				}
				if (isUseStatisticsManager()) {
					getStatisticsManager().addProgress(getMachine().getLastEdge());
				}

				logExecution(getMachine().getCurrentVertex(), "");
				System.out.println("Do vertex: " + getMachine().getCurrentVertex());
				System.out.println("Data: " + stepPair[1]);
				try {
					System.in.read();
				} catch (IOException e) {
				}
				if (isUseStatisticsManager()) {
					getStatisticsManager().addProgress(getMachine().getCurrentVertex());
				}
			}
			return;
		} else {
			logger.debug("Executing a non-dry run");
		}

		if (clsClass == null && objInstance == null)
			throw new RuntimeException("Execution instance or class is missing as parameters.");
		if (clsClass == null) {
			logger.debug("Class is null, but object instance is: " + objInstance.toString());
			clsClass = objInstance.getClass();
		}
		if (objInstance == null) {
			logger.debug("Got class: " + clsClass.getName() + ", but object instance null");
			try {
				objInstance = clsClass.getConstructor(new Class[] { ModelBasedTesting.class }).newInstance(new Object[] { this });
			} catch (SecurityException e) {
				throw new RuntimeException("SecurityException: " + e.getMessage(), e);
			} catch (NoSuchMethodException e) {
				// throw new RuntimeException(
				// "NoSuchMethodException: " + e.getMessage(), e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("IllegalArgumentException: " + e.getMessage(), e);
			} catch (InstantiationException e) {
				throw new RuntimeException("InstantiationException: " + e.getMessage(), e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("IllegalAccessException: " + e.getMessage(), e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException("InvocationTargetException: " + e.getMessage(), e);
			}
		}
		try {
			if (objInstance == null)
				objInstance = clsClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("Cannot create execution instance: " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Cannot access execution instance: " + e.getMessage(), e);
		}
		while (hasNextStep()) {
			if (future == null || future.isDone()) {
				String[] stepPair = getNextStep();
	
				try {
					logExecution(getMachine().getLastEdge(), "");
					executeMethod(clsClass, objInstance, stepPair[0], true);
					if (isUseStatisticsManager()) {
						getStatisticsManager().addProgress(getMachine().getLastEdge());
					}
	
					if (future != null && !future.isDone()) {
						while (!future.isDone()) {
							Thread.sleep(10); // Wait for future event to be done.
						}
					}
					
					logExecution(getMachine().getCurrentVertex(), "");
					executeMethod(clsClass, objInstance, stepPair[1], false);
					if (isUseStatisticsManager()) {
						getStatisticsManager().addProgress(getMachine().getCurrentVertex());
					}
				} catch (IllegalArgumentException e) {
					throw new RuntimeException("Illegal argument used.", e);
				} catch (SecurityException e) {
					throw new RuntimeException("Security failure occured.", e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Illegal access was stoped.", e);
				}

			} else {
				if (future.isCancelled()) {
					throw new RuntimeException("Future task was canceled.");
				}
				Thread.sleep(10); // Sleep a little to release the thread for other activities
			}
		}
	}

	private void executeMethod(Class<?> clsClass, Object objInstance, String strMethod, boolean isEdge) throws IllegalArgumentException,
	    SecurityException, IllegalAccessException {
		if (strMethod.contains("/")) {
			strMethod = strMethod.substring(0, strMethod.indexOf("/"));
		}

		if (strMethod.contains("[")) {
			strMethod = strMethod.substring(0, strMethod.indexOf("["));
		}

		if (strMethod.contains(" ")) {
			String s1 = strMethod.substring(0, strMethod.indexOf(" "));
			String s2 = strMethod.substring(strMethod.indexOf(" ") + 1);
			Class<?>[] paramTypes = { String.class };
			Object[] paramValues = { s2 };

			try {
				clsClass.getMethod(s1, paramTypes).invoke(objInstance, paramValues);
			} catch (InvocationTargetException e) {
				if (isEdge) {
					logger.error("InvocationTargetException for: " + getMachine().getLastEdge());
				} else {
					logger.error("InvocationTargetException for: " + getMachine().getCurrentVertex());
				}
				throw new RuntimeException("InvocationTargetException.", e);
			} catch (NoSuchMethodException e) {
				if (isEdge) {
					logger.error("NoSuchMethodException for: " + getMachine().getLastEdge());
				} else {
					logger.error("NoSuchMethodException for: " + getMachine().getCurrentVertex());
				}
				throw new RuntimeException("NoSuchMethodException.", e);
			}
		} else {
			if (isEdge && strMethod.isEmpty()) {
				return;
			}
			try {
				if (isUseGUI()) {
					App.getInstance().executingJavaTest(true);
				}
				Method m = clsClass.getMethod(strMethod, null);
				m.invoke(objInstance, null);
			} catch (InvocationTargetException e) {
				if (isEdge) {
					logger.error("InvocationTargetException for: " + getMachine().getLastEdge() + " : " + e.getCause().getMessage());
				} else {
					logger.error("InvocationTargetException for: " + getMachine().getCurrentVertex() + " : " + e.getCause().getMessage());
				}
				Util.logStackTraceToError(e);
				throw new RuntimeException("InvocationTargetException.", e.getCause());
			} catch (NoSuchMethodException e) {
				if (isEdge) {
					logger.error("NoSuchMethodException for: " + getMachine().getLastEdge());
				} else {
					logger.error("NoSuchMethodException for: " + getMachine().getCurrentVertex());
				}
				throw new RuntimeException("NoSuchMethodException.", e);
			} finally {
				if (isUseGUI()) {
					App.getInstance().executingJavaTest(false);
				}
			}
		}
	}

	protected void writePath() throws InterruptedException {
		writePath(System.out);
	}

	protected void writePath(Vector<String[]> testSequence) throws InterruptedException {
		if (this.machine == null) {
			getMachine();
		}
		while (hasNextStep()) {
			getNextStep();
			String labels = getMachine().getLastEdge().getLabelKey() + " -> " + getCurrentVertex().getLabelKey();
			String edgeManualInstruction = parseManualInstructions(getMachine().getLastEdge().getManualInstructions());
			String vertexManualInstruction = parseManualInstructions(getCurrentVertex().getManualInstructions());
			testSequence.add(new String[] { labels, edgeManualInstruction, vertexManualInstruction });
		}
	}

	private String parseManualInstructions(String manualInstructions) {
		if (!(getMachine() instanceof ExtendedFiniteStateMachine)) {
			return manualInstructions;
		}
		ExtendedFiniteStateMachine efsm = (ExtendedFiniteStateMachine) getMachine();
		if (!(efsm.isJsEnabled() || efsm.isBeanShellEnabled())) {
			return manualInstructions;
		}

		String parsedStr = manualInstructions;
		Pattern p = Pattern.compile("\\{\\$(\\w+)\\}", Pattern.MULTILINE);
		Matcher m = p.matcher(manualInstructions);
		while (m.find()) {
			String data = m.group(1);
			parsedStr = manualInstructions.replaceAll("\\{\\$" + data + "\\}", efsm.getDataValue(data));
			manualInstructions = parsedStr;
		}

		return parsedStr;
	}

	protected void writePath(PrintStream out) throws InterruptedException {
		if (this.machine == null) {
			getMachine();
		}
		while (hasNextStep()) {
			String[] stepPair = getNextStep();

			if (stepPair[0].trim() != "") {
				out.println(stepPair[0]);
				logExecution(getMachine().getLastEdge(), "");
				if (isUseStatisticsManager()) {
					getStatisticsManager().addProgress(getMachine().getLastEdge());
				}
			}
			if (stepPair[1].trim() != "") {
				out.println(stepPair[1]);
				logExecution(getMachine().getCurrentVertex(), "");
				if (isUseStatisticsManager()) {
					getStatisticsManager().addProgress(getMachine().getCurrentVertex());
				}
			}
		}
	}

	/**
	 * @param script
	 */
	public void setStartupScript(String script) {
		this.startupScript = script;
		if (this.machine != null && this.machine instanceof ExtendedFiniteStateMachine) {
			logger.debug("Will now try to run script: " + script);
			((ExtendedFiniteStateMachine) this.machine).eval(script);
		} else {
			logger.warn("Could not run script: " + script);
			logger.warn("The machine is not an Extended FSM");
		}
	}

	/**
	 * @return the startupScript
	 */
	public String getStartupScript() {
		return this.startupScript;
	}

	public String toString() {
		return getGenerator().toString();
	}

	public void setWeighted(boolean b) {
		getMachine().setWeighted(b);
	}

	public Edge getCurrentEdge() {
		if (this.machine != null)
			return getMachine().getLastEdge();
		logger.warn("Trying to retrieve current edge without specifying machine");
		return null;
	}

	public Vertex getCurrentVertex() {
		if (this.machine != null)
			return getMachine().getCurrentVertex();
		logger.warn("Trying to retrieve current vertex without specifying machine");
		return null;
	}

	/**
	 * Changes the current vertex in the model.
	 * 
	 * @param newVertex
	 *          The name ({@link Keywords.LABEL_KEY}) of the new current vertex of
	 *          the model. If null is given, or newVertex is empty, then the
	 *          default value will be the START vertex in the model. If newVertex
	 *          does not exist in the model, the method does nothing, and the
	 *          current vertex is unaffected.
	 * @return True if the operation succeeds, false if not.
	 */
	public boolean setCurrentVertex(String newVertex) {
		if (this.machine != null) {
			if (newVertex == null || newVertex.isEmpty()) {
				logger.error("Could not manually change the vertex from: " + getMachine().getCurrentVertexName()
				    + " beacuse it is an empty string.");
				return false;
			}
			if (getMachine().hasVertex(newVertex) == false) {
				logger.error("Could not manually change the vertex from: " + getMachine().getCurrentVertexName() + " to: " + newVertex
				    + " beacuse it does not exist in the model.");
				return false;
			}
			logger.info("Manually changing vertex from: " + getMachine().getCurrentVertexName() + " to: " + newVertex);
			getMachine().setVertex(newVertex);

			// We have to empty current Dijkstra path, if it exists.
			if (getGenerator() instanceof NonOptimizedShortestPath) {
				((NonOptimizedShortestPath) getGenerator()).emptyCurrentPath();
			}
			return true;
		} else {
			logger.warn("Trying to set current state without specifying machine");
		}
		return false;
	}

	public boolean isUseStatisticsManager() {
		return useStatisticsManager;
	}

	public void setUseStatisticsManager(boolean useStatisticsManager) {
		this.useStatisticsManager = useStatisticsManager;
	}

	public AbstractElement setAsVisited(Integer index) {
		AbstractElement e = getMachine().findElement(index);
		e.setVisitedKey(e.getVisitedKey() + 1);
		return e;
	}

	public void setCurrentVertex(Vertex vertex) {
		getMachine().setVertex(vertex);
	}

	public AbstractElement decrementVisited(Integer index) {
		AbstractElement e = getMachine().findElement(index);
		if (e.getVisitedKey() > 0)
			e.setVisitedKey(e.getVisitedKey() - 1);
		return e;
	}

	public void setAllUnvisited() {
		getMachine().setAllUnvisited();
	}

	public void stop() {
		logger.debug("Will stop the excution of the model.");
		stopFlag = null;
	}

	public synchronized void suspend() {
		logger.debug("Will suspend the excution of the model.");
		threadSuspended = true;
	}

	public synchronized void resume() {
		logger.debug("Will resume the excution of the model.");
		threadSuspended = false;
		notify();
	}
}
