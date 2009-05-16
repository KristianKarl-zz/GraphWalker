package org.tigris.mbt.GUI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.ws.Endpoint;

import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;
import org.tigris.mbt.ModelBasedTesting;
import org.tigris.mbt.SoapServices;
import org.tigris.mbt.Util;
import org.tigris.mbt.events.AppEvent;
import org.tigris.mbt.events.MbtEvent;
import org.tigris.mbt.graph.Edge;
import org.tigris.mbt.graph.Graph;
import org.tigris.mbt.graph.Vertex;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;


public class App extends JFrame implements ActionListener, MbtEvent  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8605452811238545133L;
	
	private JSplitPane splitPaneMessages;
	private JSplitPane splitPaneGraph;
	private JPanel panelStatistics;
	private JPanel panelVariables;
	private JPanel panelGraph;
	private JTextArea statisticsTextArea;
	private JTextArea variablesTextArea;
	private JLabel latestStateLabel;
	private JFileChooser fileChooser = new JFileChooser(System
			.getProperty("user.dir"));
	private VisualizationViewer<Vertex, Edge> vv;
	private Layout<Vertex, Edge> layout;
	private File xmlFile;
	private ExecuteMBT executeMBT = null;
	
	public File getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(File xmlFile) {
		this.xmlFile = xmlFile;
	}

	static private Logger log;
	private static Endpoint endpoint = null;
	private SoapServices soapService = null;
	
	private JButton loadButton;
	private JButton reloadButton;
	private JButton runButton;
	private JButton pauseButton;
	private JButton nextButton;
	private JCheckBox soapButton;
	
	public Status status = new Status();

	protected String newline = "\n";
	static final private String LOAD = "load";
    static final private String RELOAD = "reload";
    static final private String RUN = "run";
    static final private String PAUSE = "pause";
    static final private String NEXT = "next";
    static final private String SOAP = "soap";

	static private AppEvent appEvent = null;
	static private ChangeEvent changeEvent = null;
	
	public static void SetAppEventNotifier( AppEvent event )
    {
		log.debug( "AppEvent is set using: " + event );
		appEvent = event;
		changeEvent = new ChangeEvent(event);
    } 

	private void runSoap()
	{
		if ( endpoint != null )
		{
			endpoint.stop();
		}

		String wsURL = "http://0.0.0.0:" + Util.readWSPort() + "/mbt-services";
		soapService = new SoapServices( xmlFile.getAbsolutePath() );
		endpoint = Endpoint.publish( wsURL, soapService );
		
		try {
			log.info( "Now running as a SOAP server. For the WSDL file, see: " + wsURL.replace( "0.0.0.0", InetAddress.getLocalHost().getHostName() ) + "?WSDL" );
		} catch (UnknownHostException e) {
			log.info( "Now running as a SOAP server. For the WSDL file, see: " + wsURL + "?WSDL" );
			log.error( e.getMessage() );
		}
	}

	public void getNextEvent() {
		updateUI();
		getVv().stateChanged(changeEvent);
	}
	
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
		log.debug( "Got action: " + cmd );

        // Handle each button.
        if (LOAD.equals(cmd)) {
            load();
        } else if (RELOAD.equals(cmd)) { // second button clicked
            reload();
        } else if (RUN.equals(cmd)) { // third button clicked
            run();
        } else if (PAUSE.equals(cmd)) { // third button clicked
            pause();
        } else if (NEXT.equals(cmd)) { // third button clicked
            next();
	    } else if (SOAP.equals(cmd)) { // soap checkbox clicked
	    	if ( xmlFile != null && xmlFile.canRead() )
	    		reload();
	    }
    }

    public void load() {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"XML files", "xml");
		fileChooser.setFileFilter(filter);
		int returnVal = fileChooser.showOpenDialog(getContentPane());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			xmlFile = fileChooser.getSelectedFile();
			log.debug( "Got file: " + xmlFile.getAbsolutePath() );
			loadModel();
			if ( appEvent != null )
				appEvent.getLoadEvent();
		}
	}

	private void outPut() {
		statisticsTextArea.setText( ModelBasedTesting.getInstance().getStatisticsString() );
		
		variablesTextArea.setText( ModelBasedTesting.getInstance().getStatisticsString());
		String str = "Last edge: "
				+ (ModelBasedTesting.getInstance().getMachine().getLastEdge() == null ? ""
						: (String) ModelBasedTesting.getInstance().getMachine().getLastEdge()
								.getLabelKey())
				+ "   Current state: "
				+ ModelBasedTesting.getInstance().getMachine().getCurrentState().getLabelKey();
		latestStateLabel.setText( str );
		
		str = ModelBasedTesting.getInstance().getMachine().getCurrentDataString();
		str = str.replaceAll(";", newline);
		variablesTextArea.setText( str );
	}

	private void setButtons() {
		if ( status.isPaused() ) {
			loadButton.setEnabled(true);
			reloadButton.setEnabled(true);
			runButton.setEnabled(true);
			pauseButton.setEnabled(false);
			nextButton.setEnabled(true);
			soapButton.setEnabled(true);
		}
		else if ( status.isRunning() ) {
			loadButton.setEnabled(false);
			reloadButton.setEnabled(false);
			runButton.setEnabled(false);
			pauseButton.setEnabled(true);
			nextButton.setEnabled(false);
			soapButton.setEnabled(false);
		}
		else if ( status.isNext() ) {
			loadButton.setEnabled(false);
			reloadButton.setEnabled(false);
			runButton.setEnabled(false);
			pauseButton.setEnabled(false);
			nextButton.setEnabled(false);
			soapButton.setEnabled(false);
		}
	}
	
	@SuppressWarnings("synthetic-access")
	private void loadModel() {
		if ( executeMBT != null ) {
			executeMBT.cancel(true);
			executeMBT = null;
		}
		status.setPaused();
		setButtons();
		if ( soapButton.isSelected() )
			runSoap();
		else
		{
			log.debug( "Loading model" );
			Util.loadMbtFromXml( xmlFile.getAbsolutePath() );
		}
		(executeMBT = new ExecuteMBT()).execute();
	}

	public void run() {
		status.setRunning();
		setButtons();		
	}
	
    private class ExecuteMBT extends SwingWorker<Void, Void> {
        protected Void doInBackground() {
        	ModelBasedTesting.getInstance().executePath();
            return null;
        }

        protected void done() {
			super.done();
			App.getInstance().pause();
		}
    }

	public void pause() {
		status.setPaused();
		setButtons();
	}

	public void next() {
		if ( ModelBasedTesting.getInstance().hasNextStep() == false )
			return;
		if ( status.isNext() ) {
			return;
		}

		setButtons();
		status.setNext();
		setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
		ModelBasedTesting.getInstance().getNextStep();
		setCursor( Cursor.getDefaultCursor() );
		status.setPaused();
		setButtons();
	}

	public void updateUI() {
		log.debug( "Updating the UI" );
		outPut();
	}

	public void reload() {
		loadModel();
	}

	public void setLayout(Layout<Vertex, Edge> layout) {
		this.layout = layout;

		Transformer<Vertex,Point2D> vertexLocation = new Transformer<Vertex,Point2D>(){
            public Point2D transform(Vertex v) {
                return v.getLocation();
            }
        };

		this.layout.setInitializer(vertexLocation);
	}

	public VisualizationViewer<Vertex, Edge> getVv() {
		return vv;
	}

	public void updateLayout() {
		if ( ModelBasedTesting.getInstance().getGraph() != null ) {
			setLayout( new StaticLayout<Vertex, Edge>( ModelBasedTesting.getInstance().getGraph() ) );
			getVv().setGraphLayout( layout );
			updateUI();
		}
	}

	public class MyEdgePaintFunction implements Transformer<Edge,Paint> { 
		public Paint transform(Edge e) {
			if ( ModelBasedTesting.getInstance().getMachine().getLastEdge() != null && 
				 ModelBasedTesting.getInstance().getMachine().getLastEdge().equals(e))
				return Color.RED;
			else if (e.getVisitedKey() > 0)
				return Color.LIGHT_GRAY;
			else
				return Color.BLUE;
		}
	}
	
	public class MyEdgeStrokeFunction implements Transformer<Edge,Stroke> {
		protected final Stroke UNVISITED = new BasicStroke(1);
		protected final Stroke VISITED = new BasicStroke(1);
		protected final Stroke CURRENT = new BasicStroke(3);

        public Stroke transform(Edge e) {
			if ( ModelBasedTesting.getInstance().getMachine().getLastEdge() != null	&&
				 ModelBasedTesting.getInstance().getMachine().getLastEdge().equals(e))
				return CURRENT;
			else if (e.getVisitedKey() > 0)
				return VISITED;
			else
				return UNVISITED;
        }
	    
	}
	
	public class MyVertexPaintFunction implements Transformer<Vertex,Paint> {
		public Paint transform(Vertex v) {
			if ( ModelBasedTesting.getInstance().getMachine().isCurrentState(v))
				return Color.RED;
			else if (v.getVisitedKey() > 0 )
				return Color.LIGHT_GRAY;
			else
				return Color.BLUE;
		}
	}

	public class MyVertexFillPaintFunction implements Transformer<Vertex,Paint> {
		public Paint transform( Vertex v ) {
			if ( ModelBasedTesting.getInstance().getMachine().isCurrentState(v))
				return Color.RED;
			else if ( v.getVisitedKey() > 0 )
				return Color.LIGHT_GRAY;
			else
				return Color.BLUE;
		}
	}

	private VisualizationViewer<Vertex, Edge> getGraphViewer() {		
		if ( ModelBasedTesting.getInstance().getGraph() == null )
			layout = new StaticLayout<Vertex, Edge>(new Graph() );
		else
			//layout = new StaticLayout<Vertex, Edge>( ModelBasedTesting.getInstance().getGraph() );
			layout = new StaticLayout<Vertex, Edge>( ModelBasedTesting.getInstance().getGraph() );
		
		vv = new VisualizationViewer<Vertex, Edge>(layout);

		DefaultModalGraphMouse<Vertex, Edge> graphMouse = new DefaultModalGraphMouse<Vertex, Edge>();
		vv.setGraphMouse(graphMouse);
		vv.setPickSupport( new ShapePickSupport<Vertex, Edge>(vv));
		vv.setBackground(Color.WHITE);
        vv.getRenderContext().setVertexDrawPaintTransformer(new MyVertexPaintFunction());
        vv.getRenderContext().setVertexFillPaintTransformer(new MyVertexFillPaintFunction());
        vv.getRenderContext().setEdgeDrawPaintTransformer(new MyEdgePaintFunction());
        vv.getRenderContext().setEdgeStrokeTransformer(new MyEdgeStrokeFunction());

        Transformer<Vertex,String> vertexStringer = new Transformer<Vertex,String>(){
            public String transform(Vertex v) {
                return "<html><center>" + v.getFullLabelKey().replaceAll( "\\n", "<p>" ) + "<p>INDEX=" + v.getIndexKey();
            }
        };
		vv.getRenderContext().setVertexLabelTransformer(vertexStringer);

        Transformer<Edge,String> edgeStringer = new Transformer<Edge,String>(){
            public String transform(Edge e) {
                return "<html><center>" + e.getFullLabelKey().replaceAll( "\\n", "<p>" ) + "<p>INDEX=" + e.getIndexKey();
            }
        };
        vv.getRenderContext().setEdgeLabelTransformer(edgeStringer);

		return vv;
	}

	public void createPanelStatistics() {
		panelStatistics = new JPanel();
		panelStatistics.setLayout(new BorderLayout());

		statisticsTextArea = new JTextArea();
		statisticsTextArea.setPreferredSize(new Dimension(300,100));
		panelStatistics.add(statisticsTextArea, BorderLayout.CENTER);
	}

	public void createPanelVariables() {
		panelVariables = new JPanel();
		panelVariables.setLayout(new BorderLayout());

		variablesTextArea = new JTextArea();
		variablesTextArea.setPreferredSize(new Dimension(100,100));
		panelVariables.add(variablesTextArea, BorderLayout.CENTER);
	}

	protected JButton makeNavigationButton(String imageName,
			String actionCommand, String toolTipText, String altText, boolean enabled) {
		
		// Look for the image.
		String imgLocation = "resources/icons/" + imageName + ".png";
		URL imageURL = App.class.getResource(imgLocation);

		// Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);
		button.setEnabled(enabled);

		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else { // no image found
			button.setText(altText);
			log.error("Resource not found: " + imgLocation);
		}

		return button;
	}

	protected JCheckBox makeNavigationCheckBoxButton(String imageName,
			String actionCommand, String toolTipText, String altText) {

		// Create and initialize the button.
		JCheckBox button = new JCheckBox();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		button.setText(altText);

		return button;
	}

	public void addButtons(JToolBar toolBar) {

		loadButton = makeNavigationButton("open", LOAD,
                "Load a model (graphml file)",
        		"Load", true);
		toolBar.add(loadButton);

		reloadButton = makeNavigationButton("reload", RELOAD,
                "Reload already loaded Model",
        		"Reload", false);
		toolBar.add(reloadButton);

		runButton = makeNavigationButton("run", RUN,
                "Starts the execution",
        		"Run", false);
		toolBar.add(runButton);

		pauseButton = makeNavigationButton("pause", PAUSE,
                "Pauses the execution",
        		"Pause", false);
		toolBar.add(pauseButton);

		nextButton = makeNavigationButton("next", NEXT,
                "Walk a step in the model",
        		"Next", false);
		toolBar.add(nextButton);

		soapButton = makeNavigationCheckBoxButton("soap", SOAP,
                "Run MBT in SOAP(Web Services) mode",
        		"Soap");
		toolBar.add(soapButton);
	}

	public void createPanelGraph() {
		panelGraph = new JPanel();
		panelGraph.setLayout(new BorderLayout());
		latestStateLabel = new JLabel(" ");
		panelGraph.add(latestStateLabel, BorderLayout.NORTH);
		panelGraph.add(getGraphViewer(), BorderLayout.CENTER);
	}

	public void init() {
		setTitle("Split Pane Application");
		setBackground(Color.gray);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		getContentPane().add(topPanel);

		// Create the panels
		createPanelStatistics();
		createPanelVariables();
		createPanelGraph();

		// Create a splitter panes
		splitPaneGraph = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		topPanel.add(splitPaneGraph, BorderLayout.CENTER);
		splitPaneGraph.setTopComponent(panelGraph);

		splitPaneMessages = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPaneGraph.setBottomComponent(splitPaneMessages);
		splitPaneMessages.setLeftComponent(panelStatistics);
		splitPaneMessages.setRightComponent(panelVariables);

		JToolBar toolBar = new JToolBar("Toolbar");
		add(toolBar, BorderLayout.PAGE_START);
		addButtons(toolBar);
	}

	// Private constructor prevents instantiation from other classes
	private App() {
		log = Util.setupLogger( App.class );
		init();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationByPlatform(true);
		setVisible(true);
	}

	/**
	 * AppHolder is loaded on the first execution of App.getInstance() 
	 * or the first access to AppHolder.INSTANCE, not before.
	 */
	@SuppressWarnings("synthetic-access")
	private static class AppHolder { 
		private static final App INSTANCE = new App();
	}

	@SuppressWarnings("synthetic-access")
	public static App getInstance() {
		return AppHolder.INSTANCE;
	}

	public static void main(String args[]) {
		getInstance();
		ModelBasedTesting.getInstance().setUseGUI();
	}
}
