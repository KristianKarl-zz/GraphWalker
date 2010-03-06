package org.tigris.mbt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.tigris.mbt.conditions.AlternativeCondition;
import org.tigris.mbt.conditions.CombinationalCondition;
import org.tigris.mbt.conditions.EdgeCoverage;
import org.tigris.mbt.conditions.NeverCondition;
import org.tigris.mbt.conditions.ReachedEdge;
import org.tigris.mbt.conditions.ReachedRequirement;
import org.tigris.mbt.conditions.ReachedVertex;
import org.tigris.mbt.conditions.RequirementCoverage;
import org.tigris.mbt.conditions.VertexCoverage;
import org.tigris.mbt.conditions.StopCondition;
import org.tigris.mbt.conditions.TestCaseLength;
import org.tigris.mbt.conditions.TimeDuration;
import org.tigris.mbt.exceptions.GeneratorException;
import org.tigris.mbt.exceptions.StopConditionException;
import org.tigris.mbt.generators.CodeGenerator;
import org.tigris.mbt.generators.CombinedPathGenerator;
import org.tigris.mbt.generators.ListGenerator;
import org.tigris.mbt.generators.NonOptimizedShortestPath;
import org.tigris.mbt.generators.PathGenerator;
import org.tigris.mbt.generators.RandomPathGenerator;
import org.tigris.mbt.generators.RequirementsGenerator;
import org.tigris.mbt.generators.A_StarPathGenerator;
import org.tigris.mbt.graph.AbstractElement;
import org.tigris.mbt.graph.Edge;
import org.tigris.mbt.graph.Graph;
import org.tigris.mbt.graph.Vertex;

/**
 * This class has some utility functionality used by org.tigris.mbt The
 * functionality is:<br>
 * * Getting names with extra info for vertices and edges<br>
 * * Setting up the logger for classes<br>
 */
public class Util {

	static Logger logger = setupLogger(Util.class);
	static private Random random = new Random();
	public static String newline = System.getProperty("line.separator");
	static private Timer timer = null;

	public static String getCompleteName(AbstractElement element) {
		if (element instanceof Edge)
			return getCompleteEdgeName((Edge) element);
		if (element instanceof Vertex)
			return getCompleteVertexName((Vertex) element);
		throw new RuntimeException("Element type not supported: '" + element.getClass().getName() + "'");
	}

	/**
	 * Retries information regarding an edge, and returns it as a String. This
	 * method is for logging purposes.
	 * 
	 * @param edge
	 *            The edge about which information shall be retrieved.
	 * @return Returns a String with information regarding the edge, including
	 *         the source and destination vertices. The format is:<br>
	 * 
	 *         <pre>
	 * '&lt;EDGE LABEL&gt;', INDEX=x ('&lt;SOURCE VERTEX LABEL&gt;', INDEX=y -&gt; '&lt;DEST VERTEX LABEL&gt;', INDEX=z)
	 * </pre>
	 * 
	 *         Where x, y and n are the unique indexes for the edge, the source
	 *         vertex and the destination vertex.<br>
	 *         Please note that the label of an edge can be either null, or
	 *         empty ("");
	 */
	private static String getCompleteEdgeName(Edge edge) {
		String str = "Edge: '" + edge.getLabelKey() + "', INDEX=" + edge.getIndexKey();
		return str;
	}

	/**
	 * Retries information regarding a vertex, and returns it as a String. This
	 * method is for logging purposes.
	 * 
	 * @param vertex
	 *            The vertex about which information shall be retrieved.
	 * @return Returns a String with information regarding the vertex. The
	 *         format is:<br>
	 * 
	 *         <pre>
	 * '&lt;VERTEX LABEL&gt;', INDEX=n
	 * </pre>
	 * 
	 *         Where is the unique index for the vertex.
	 */
	private static String getCompleteVertexName(Vertex vertex) {
		String str = "Vertex: '" + vertex.getLabelKey() + "', INDEX=" + vertex.getIndexKey();
		return str;
	}

	public static void AbortIf(boolean bool, String message) {
		if (bool) {
			throw new RuntimeException(message);
		}
	}

	@SuppressWarnings("unchecked")
	public static Logger setupLogger(Class classParam) {
		Logger logger = Logger.getLogger(classParam);
		if (new File("mbt.properties").canRead()) {
			PropertyConfigurator.configure("mbt.properties");
		} else {
			try {
				WriterAppender writerAppender = new WriterAppender(new SimpleLayout(), new FileOutputStream("mbt.log"));
				logger.addAppender(writerAppender);
				logger.setLevel((Level) Level.ERROR);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}

		}
		return logger;
	}

	/**
	 * Creates an adds a vertex to a graph.
	 * @param graph The graph to which a vertex is to be added.
	 * @param strLabel The label of the vertex.
	 * @return The newly created vertex.
	 */
	public static Vertex addVertexToGraph(Graph graph, String strLabel) {
		Vertex retur = new Vertex();
		retur.setIndexKey(new Integer(graph.getEdgeCount() + graph.getVertexCount() + 1));
		if (strLabel != null)
			retur.setLabelKey(strLabel);
		graph.addVertex(retur);
		return retur;
	}

	/**
	 * Creates and adds an edge to a graph.
	 * @param graph The graph to which the edge is to be added.
	 * @param vertexFrom The source point of the edge.
	 * @param vertexTo The destination point of the edge.
	 * @param strLabel The label of the edge.
	 * @param strParameter The parameter(s) to be passed to the method implementing the
	 * edge in a test.
	 * @param strGuard The guard of the edge.
	 * @param strAction The action to be performed.
	 * @return The newly created edge.
	 */
	public static Edge addEdgeToGraph(Graph graph, Vertex vertexFrom, Vertex vertexTo, String strLabel, String strParameter, String strGuard,
			String strAction) {
		Edge retur = new Edge();
		retur.setIndexKey(new Integer(graph.getEdgeCount() + graph.getVertexCount() + 1));
		if (strLabel != null)
			retur.setLabelKey(strLabel);
		if (strParameter != null)
			retur.setParameterKey(strParameter);
		if (strGuard != null)
			retur.setGuardKey(strGuard);
		if (strAction != null)
			retur.setActionsKey(strAction);
		graph.addEdge(retur, vertexFrom, vertexTo);
		return retur;
	}

	/**
	 * Adds a stop condition for the model.
	 * @param conditionType The condition type.
	 * @param conditionValue The value of the condition.
	 * @return The newly creaated stop condition.
	 * @throws StopConditionException
	 */
	public static StopCondition getCondition(int conditionType, String conditionValue) throws StopConditionException {
		StopCondition condition = null;
		try {
			switch (conditionType) {
			case Keywords.CONDITION_EDGE_COVERAGE:
				condition = new EdgeCoverage(Double.parseDouble(conditionValue) / 100);
				break;
			case Keywords.CONDITION_REACHED_EDGE:
				if ( conditionValue == null || conditionValue.isEmpty() ) {
					throw new StopConditionException("The name of reached edge must not be empty");
				}
				if ( ModelBasedTesting.getInstance().getMachine().findEdge( Edge.getLabelAndParameter(conditionValue)[0] ) == null ) {
					throw new StopConditionException("The name of reached edge: '" + Edge.getLabelAndParameter(conditionValue)[0] + 
              "' (in stop condition: '" +  conditionValue + "') does not exists in the model.");
				}
				condition = new ReachedEdge(conditionValue);
				break;
			case Keywords.CONDITION_REACHED_VERTEX:
				if ( conditionValue == null || conditionValue.isEmpty() ) {
					throw new StopConditionException("The name of reached vertex must not be empty");
				}
				if ( ModelBasedTesting.getInstance().getMachine().findVertex( Vertex.getLabel(conditionValue) ) == null ) {
					throw new StopConditionException("The name of reached vertex: '" + Vertex.getLabel(conditionValue) + 
							                             "' (in stop condition: '" +  conditionValue + "') does not exists in the model.");
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
			if (conditionValue == null || conditionValue.isEmpty())
				throw new StopConditionException("Stop condition value is missing. ");
			else
				throw new StopConditionException("Invalid stop condition value: " + conditionValue);
		}
		return condition;
	}

	public static PathGenerator getGenerator(int generatorType) throws GeneratorException {
		PathGenerator generator = null;

		switch (generatorType) {
		case Keywords.GENERATOR_RANDOM:
			generator = new RandomPathGenerator();
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

		logger.debug("Added generator: " + generator);

		return generator;
	}

	/**
	 * Load MBT settings from a xml file
	 * 
	 * @param fileName
	 *            The XML settings file
	 * @return
	 * @throws StopConditionException
	 * @throws GeneratorException
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws InterruptedException 
	 */
	public static ModelBasedTesting loadMbtAsWSFromXml(String fileName) throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		return loadXml(fileName, true, false);
	}

	/**
	 * Load MBT settings from a xml file
	 * 
	 * @param fileName
	 *            The XML settings file
	 * @param dryRun
	 *            Is mbt to be run in a dry run mode?
	 * @return
	 * @throws StopConditionException
	 * @throws GeneratorException
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws InterruptedException 
	 */
	public static ModelBasedTesting loadMbtFromXml(String fileName, boolean dryRun) throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		return loadXml(fileName, false, dryRun);
	}

	/**
	 * Load MBT settings from a xml file
	 * 
	 * @param fileName
	 *            The XML settings file
	 * @return
	 * @throws StopConditionException
	 * @throws GeneratorException
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws InterruptedException 
	 */
	public static ModelBasedTesting loadMbtFromXml(String fileName) throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		return loadXml(fileName, false, false);
	}

	/**
	 * Load MBT settings from a xml file
	 * 
	 * @param fileName
	 *            The XML settings file
	 * @param runningSoapServices
	 *            Is mbt to run in Web Services mode?
	 * @param dryRun
	 *            Is mbt to be run in a dry run mode?
	 * @return
	 * @throws StopConditionException
	 * @throws GeneratorException
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws InterruptedException 
	 */
	@SuppressWarnings("unchecked")
	private static ModelBasedTesting loadXml(String fileName, boolean runningSoapServices, boolean dryRun) throws StopConditionException,
			GeneratorException, IOException, JDOMException, InterruptedException {
		final ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		mbt.reset();
		mbt.setDryRun(dryRun);

		SAXBuilder parser = new SAXBuilder("org.apache.crimson.parser.XMLReaderImpl", false);
		Document doc;
		doc = parser.build(fileName);
		Element root = doc.getRootElement();
		List<Element> models = root.getChildren("MODEL");

		if (models.size() == 0)
			throw new RuntimeException("Model is missing from XML");

		for (Iterator<Element> i = models.iterator(); i.hasNext();) {
			mbt.readGraph((i.next()).getAttributeValue("PATH"));
		}

		List<Element> classPath = root.getChildren("CLASS");
		for (Iterator<Element> i = classPath.iterator(); i.hasNext();) {
			String classPaths[] = i.next().getAttributeValue("PATH").split(":");
			for (int j = 0; j < classPaths.length; j++) {
				try {
					ClassPathHack.addFile(classPaths[j]);
					logger.debug("Added to classpath: " + classPaths[j]);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage() + "\nCould not add: '" + classPaths[j] + "' to CLASSPATH\n"
							+ "Please review your xml file: '" + fileName + "' at CLASS PATH", e);
				}
			}
		}

		if (root.getAttributeValue("SCRIPT_ENGINE") != null && root.getAttributeValue("SCRIPT_ENGINE").equalsIgnoreCase("js")) {
			logger.debug("Enabling JavaScript engine");
			mbt.enableJsScriptEngine(true);
		} else {
			logger.debug("Using BeanShell script engine, if EFSM is enabled.");
			mbt.enableJsScriptEngine(false);
		}

		if (root.getAttributeValue("EXTENDED") != null && root.getAttributeValue("EXTENDED").equalsIgnoreCase("true")) {
			logger.debug("Enabling extended FSM");
			mbt.enableExtended(true);
			mbt.setStartupScript(getScriptContent(root.getChildren("SCRIPT")));
		} else {
			logger.debug("Disabling extended FSM");
			mbt.enableExtended(false);
		}

		if (root.getAttributeValue("WEIGHT") != null && root.getAttributeValue("WEIGHT").equalsIgnoreCase("true")) {
			logger.debug("Using weighted edges");
			mbt.setWeighted(true);
		} else {
			logger.debug("Will not use weighted edges");
			mbt.setWeighted(false);
		}

		List<Element> generators = root.getChildren("GENERATOR");

		if (generators.size() == 0)
			throw new RuntimeException("Generator is missing from XML");

		PathGenerator generator;
		if (generators.size() > 1) {
			generator = new CombinedPathGenerator();
			for (Iterator<Element> i = generators.iterator(); i.hasNext();) {
				((CombinedPathGenerator) generator).addPathGenerator(getGenerator(i.next()));
			}
		} else {
			generator = getGenerator((Element) generators.get(0));
		}
		if (generator == null)
			throw new RuntimeException("Failed to set generator");
		mbt.setGenerator(generator);

		final String reportName = root.getAttributeValue("REPORT");
		String reportTemplate = root.getAttributeValue("REPORT-TEMPLATE");

		if (reportName != null && reportTemplate != null) {
			logger.debug("Will use report template: " + reportTemplate);
			mbt.getStatisticsManager().setReportTemplate(reportTemplate);
		}

		String logInterval = root.getAttributeValue("LOG-INTERVAL");

		if (logInterval != null) {
			long seconds = Integer.valueOf(logInterval).longValue();
			TimerTask logTask;
			if (reportName != null && reportTemplate != null) {
				logTask = new TimerTask() {
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
					public void run() {
						logger.info(mbt.getStatisticsCompact());
					}
				};
			}
			timer = new Timer();
			timer.schedule(logTask, 500, seconds * 1000);
		}

		if (runningSoapServices == false) {
			try {

				String executor = root.getAttributeValue("EXECUTOR");
				logger.debug("Executor is: " + executor);
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
							throw new RuntimeException("offline file '" + executorParam + "' could not be created or is writeprotected.", e);
						}
					}
					if (mbt.isUseGUI() == false) {
						mbt.writePath(out);
						if (out != System.out) {
							out.close();
						}
					}
				} else if (executor.equalsIgnoreCase("java")) {
					if (executorParam == null || executorParam.equals("")) {
						throw new RuntimeException("No java class specified for execution");
					}

					if (mbt.isUseGUI() == false) {
						mbt.executePath(executorParam);
					} else {
						mbt.setJavaExecutorClass(executorParam);
					}
				} else if (executor.equalsIgnoreCase("online")) {
					if (mbt.isDryRun()) {
						logger.debug("Executing a dry run");
						mbt.executePath(null, null);
					}
					if (mbt.isUseGUI() == false) {
						mbt.interractivePath();
					}

				} else if (executor.equalsIgnoreCase("none") || executor.equals("")) {
					// no execution (for debug purpose)
				} else {
					throw new RuntimeException("Unknown executor '" + executor + "'");
				}

			} finally {
				if (timer != null)
					timer.cancel();
				if (reportName != null && reportTemplate != null && mbt.isUseGUI() == false) {
					mbt.getStatisticsManager().writeFullReport(reportName);
				}
			}
		}
		return mbt;
	}

	public static void killTimer() {
		if (timer != null) {
			timer.cancel();
		}
	}

	private static String getScriptContent(List<Element> scripts) throws IOException {
		String retur = "";
		for (Iterator<Element> i = scripts.iterator(); i.hasNext();) {
			Element script = (Element) i.next();
			String internal = script.getTextTrim();
			if (internal != null && !internal.equals(""))
				retur += internal + "\n";
			String external = script.getAttributeValue("PATH");
			if (external != null && !external.equals(""))
				retur += readFile(external) + "\n";
		}
		return retur;
	}

	@SuppressWarnings("unchecked")
	private static PathGenerator getGenerator(Element generator) throws StopConditionException, GeneratorException, IOException {
		int generatorType = Keywords.getGenerator(generator.getAttributeValue("TYPE"));
		PathGenerator generatorObject = getGenerator(generatorType);
		if (generatorObject instanceof CodeGenerator) {
			String[] template = { "", "", "" };
			String templateFile = generator.getAttributeValue("VALUE");
			if (templateFile != null) {
				template[1] = readFile(templateFile.trim());
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
			StopCondition stopCondition = getCondition(generator.getChildren());
			if (stopCondition != null) {
				generatorObject.setStopCondition(stopCondition);
			}
		}
		return generatorObject;
	}

	private static StopCondition getCondition(List<Element> conditions) throws StopConditionException {
		StopCondition condition = null;
		if (conditions.size() > 1) {
			condition = new CombinationalCondition();
			for (Iterator<Element> i = conditions.iterator(); i.hasNext();)
				((CombinationalCondition) condition).add(getCondition((Element) i.next()));
		} else if (conditions.size() == 1) {
			condition = getCondition((Element) conditions.get(0));
		}
		return condition;
	}

	@SuppressWarnings("unchecked")
	private static StopCondition getCondition(Element condition) throws StopConditionException {
		StopCondition stopCondition = null;
		if (condition.getName().equalsIgnoreCase("AND")) {
			stopCondition = new CombinationalCondition();
			for (Iterator<Element> i = condition.getChildren().iterator(); i.hasNext();)
				((CombinationalCondition) stopCondition).add(getCondition(i.next()));
		} else if (condition.getName().equalsIgnoreCase("OR")) {
			stopCondition = new AlternativeCondition();
			for (Iterator<Element> i = condition.getChildren().iterator(); i.hasNext();)
				((AlternativeCondition) stopCondition).add(getCondition(i.next()));
		} else if (condition.getName().equalsIgnoreCase("CONDITION")) {
			int type = Keywords.getStopCondition(condition.getAttributeValue("TYPE"));
			String value = condition.getAttributeValue("VALUE");
			stopCondition = getCondition(type, value);
		}
		return stopCondition;
	}

	public static String readFile(String fileName) throws IOException  {
		String retur = "";
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		while (in.ready())
			retur += in.readLine() + "\n";
		return retur;
	}

	public static char getInput() {
		char c = 0;
		try {
			while (c != '0' && c != '1' && c != '2') {
				int tmp = System.in.read();
				c = (char) tmp;
			}
		} catch (IOException e) {
		}
		return c;
	}

	/**
	 * This functions shuffle the array, and returns the shuffled array
	 * 
	 * @param array
	 * @return
	 */
	public static Object[] shuffle(Object[] array) {
		for (int i = 0; i < array.length; i++) {
			Object leftObject = array[i];
			int index = random.nextInt(array.length);
			Object rightObject = array[index];

			array[i] = rightObject;
			array[index] = leftObject;
		}
		return array;
	}

	public static int getLevenshteinDistance(String s, String t) {
		if (s == null || t == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}

		int n = s.length(); // length of s
		int m = t.length(); // length of t

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		int p[] = new int[n + 1]; // 'previous' cost array, horizontally
		int d[] = new int[n + 1]; // cost array, horizontally
		int _d[]; // placeholder to assist in swapping p and d

		// indexes into strings s and t
		int i; // iterates through s
		int j; // iterates through t

		char t_j; // jth character of t

		int cost; // cost

		for (i = 0; i <= n; i++) {
			p[i] = i;
		}

		for (j = 1; j <= m; j++) {
			t_j = t.charAt(j - 1);
			d[0] = j;

			for (i = 1; i <= n; i++) {
				cost = s.charAt(i - 1) == t_j ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left
				// and up
				// +cost
				d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
			}

			// copy current distance counts to 'previous row' distance counts
			_d = p;
			p = d;
			d = _d;
		}

		// our last action in the above loop was to switch d and p, so p now
		// actually has the most recent cost counts
		return p[n];
	}

	public static InetAddress getInternetAddr(String nic) {
		// Find the real network interface
		NetworkInterface iface = null;
		InetAddress ia = null;
		boolean foundNIC = false;
		try {
			for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements() && foundNIC == false;) {
				iface = (NetworkInterface) ifaces.nextElement();
				logger.debug("Interface: " + iface.getDisplayName());
				for (Enumeration<InetAddress> ips = iface.getInetAddresses(); ips.hasMoreElements() && foundNIC == false;) {
					ia = (InetAddress) ips.nextElement();
					logger.debug(ia.getCanonicalHostName() + " " + ia.getHostAddress());
					if (!ia.isLoopbackAddress()) {
						logger.debug("  Not a loopback address...");
						if (ia.getHostAddress().indexOf(":") == -1 && nic.equals(iface.getDisplayName())) {
							logger.debug("  Host address does not contain ':'");
							logger.debug("  Interface: " + iface.getName() + " seems to be InternetInterface. I'll take it...");
							foundNIC = true;
						}
					}
				}
			}
		} catch (SocketException e) {
			logger.error(e.getMessage());
		} finally {
			if (foundNIC == false && nic != null) {
				logger.error("Could not bind to network interface: " + nic);
				throw new RuntimeException("Could not bind to network interface: " + nic);
			} else if (foundNIC == false && nic == null) {
				logger.error("Could not bind to any network interface");
				throw new RuntimeException("Could not bind to any network interface: ");
			}
		}
		return ia;
	}

	public static String readWSPort() {
		PropertiesConfiguration conf = null;
		try {
			conf = new PropertiesConfiguration("mbt.properties");
		} catch (ConfigurationException e) {
			logger.error(e.getMessage());
		}
		String port = conf.getString("mbt.ws.port");
		logger.debug("Read port from mbt.properties: " + port);
		if (port == null) {
			port = "9090";
			logger.debug("Setting port to: 9090");
		}
		return port;
	}

	public static String printClassPath() {
		String classPath = "";

		// Get the System Classloader
		ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();

		// Get the URLs
		URL[] urls = ((URLClassLoader) sysClassLoader).getURLs();

		for (int i = 0; i < urls.length; i++) {
			classPath += urls[i].getFile() + "\n";
		}
		return classPath;
	}

	public static void logStackTraceToError(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.close();
		logger.error(sw.toString());
	}

	public static void logStackTraceToError(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.close();
		logger.error(sw.toString());
	}
}
