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
//This file is part of the GraphWalker java package
//The MIT License
//
//Copyright (c) 2010 graphwalker.org
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

package org.graphwalker.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.ws.Endpoint;

import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;
import org.graphwalker.*;
import org.graphwalker.events.AppEvent;
import org.graphwalker.events.MbtEvent;
import org.graphwalker.exceptions.GeneratorException;
import org.graphwalker.exceptions.GuiStoppedExecution;
import org.graphwalker.exceptions.StopConditionException;
import org.graphwalker.graph.AbstractElement;
import org.graphwalker.graph.Edge;
import org.graphwalker.graph.Graph;
import org.graphwalker.graph.Vertex;
import org.graphwalker.io.ParseLog;
import org.jdom.JDOMException;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.util.Animator;

public class App extends JFrame implements ActionListener, MbtEvent, Application {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8605452811238545133L;
	private static final String title = "GraphWalker " + ModelBasedTesting.getInstance().getVersionString();

	private ModelBasedTesting mbt;

	private JSplitPane splitPaneMessages = null;
	private JSplitPane splitPaneGraph = null;
	private JPanel panelStatistics = null;
	private JPanel panelVariables = null;
	private JPanel panelGraph = null;
	private JTextArea statisticsTextArea = null;
	private JTextArea variablesTextArea = null;
	private JLabel latestVertexLabel = null;
	private JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
	private VisualizationViewer<Vertex, Edge> vv;
	private Layout<Vertex, Edge> graphLayout;

	private File xmlFile = null;
	private File graphmlFile = null;
	private File logFile = null;
	private ExecuteMBT executeMBT = null;
	private Timer updateColorLatestVertexLabel = new Timer();

	static private Logger logger;
	private Endpoint endpoint = null;
	private SoapServices soapService = null;

	private JButton loadButton;
	private JButton reloadButton;
	private JButton firstButton;
	private JButton backButton;
	private JButton runButton;
	private JButton pauseButton;
	private JButton nextButton;
	private JCheckBox soapButton;
	private JCheckBox centerOnVertexButton;
	private StatusBar statusBar;

	private Status status = new Status();
	private Vector<ParseLog.LoggedItem> parsedLogFile = null;
	private Integer currentStep = null;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
		setButtons();
	}

	public ModelBasedTesting getMbt() {
		return mbt;
	}

	public void setMbt(ModelBasedTesting mbt) {
		this.mbt = mbt;
	}

	private String newline = "\n";
	static final private String LOAD = "load";
	static final private String RELOAD = "reload";
	static final private String FIRST = "first";
	static final private String BACK = "BACK";
	static final private String RUN = "run";
	static final private String PAUSE = "pause";
	static final private String NEXT = "next";
	static final private String SOAP = "soap";
	static final private String CENTERONVERTEX = "centerOnVertex";

	static private AppEvent appEvent = null;
	static private ChangeEvent changeEvent = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Class<? extends Layout>[] getCombos() {
		List<Class<? extends Layout>> layouts = new ArrayList<Class<? extends Layout>>();
		layouts.add(StaticLayout.class);
		layouts.add(KKLayout.class);
		layouts.add(FRLayout.class);
		layouts.add(CircleLayout.class);
		layouts.add(SpringLayout.class);
		layouts.add(SpringLayout2.class);
		layouts.add(ISOMLayout.class);
		return layouts.toArray(new Class[layouts.size()]);
	}

	private void stopSoap() {
		endpoint.stop();
		status.unsetState(Status.executingSoapTest);
		status.setState(Status.paused);
		statusBar.setMessage("Paused");
		setButtons();
		logger.debug("SOAP service stopped");
		loadModel();
	}

	private void runSoap() {
		if (endpoint != null) {
			endpoint = null;
		}
		setWaitCursor();

		if (executeMBT != null) {
			executeMBT.cancel(true);
		}

		String wsURL = "http://0.0.0.0:" + Util.readWSPort() + "/mbt-services";
		try {
			mbt = Util.loadMbtAsWSFromXml(Util.getFile(xmlFile.getAbsolutePath()));
			mbt.setUseGUI(this);
			soapService = new SoapServices(mbt);
			soapService.xmlFile = xmlFile.getAbsolutePath();
		} catch (Exception e) {
			logger.error("Failed to start the SOAP service. " + e.getMessage());
			JOptionPane.showMessageDialog(App.getInstance(), "Failed to start the SOAP service. " + e.getMessage());
			soapButton.setSelected(false);
			status.setStopped();
			reset();
			return;
		}
		endpoint = Endpoint.publish(wsURL, soapService);

		try {
			String msg = "Now running as a SOAP server. For the WSDL file, see: "
			    + wsURL.replace("0.0.0.0", InetAddress.getLocalHost().getHostName()) + "?WSDL";
			logger.info(msg);
			if (!Util.readSoapGuiStartupState()) {
				JOptionPane.showMessageDialog(App.getInstance(), msg);
			}
			status.unsetState(Status.stopped);
			status.unsetState(Status.paused);
			status.setState(Status.executingSoapTest);
			statusBar.setMessage(msg);
		} catch (UnknownHostException e) {
			logger.error("Failed to start the SOAP service. " + e.getMessage());
			JOptionPane.showMessageDialog(App.getInstance(), "Failed to start the SOAP service. " + e.getMessage());
			soapButton.setSelected(false);
			status.setStopped();
			reset();
		} finally {
			setButtons();
			setDefaultCursor();
			updateLayout();
		}
	}

	@Override
	public void getNextEvent() {
		updateUI();
		getVv().stateChanged(changeEvent);
		if (centerOnVertexButton.isSelected()) {
			centerOnVertex();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		logger.debug("Got action: " + cmd);

		// Handle each button.
		if (LOAD.equals(cmd)) {
			load();
		} else if (RELOAD.equals(cmd)) {
			reload();
		} else if (FIRST.equals(cmd)) {
			first();
		} else if (BACK.equals(cmd)) {
			prev();
		} else if (RUN.equals(cmd)) {
			if (status.isExecutingLogTest())
				next();
			else
				run();
		} else if (PAUSE.equals(cmd)) {
			pause();
		} else if (NEXT.equals(cmd)) {
			if (status.isExecutingLogTest())
				last();
			else
				next();
		} else if (SOAP.equals(cmd)) {
			if (soapButton.isSelected()) {
				runSoap();
			} else {
				stopSoap();
			}
		} else if (CENTERONVERTEX.equals(cmd)) { // center on vertex checkbox
			// clicked
			if (centerOnVertexButton.isSelected()) {
				centerOnVertex();
				getVv().repaint();
			}
		}
	}

	private void load() {
		status.setStopped();
		statusBar.setMessage("Stopped");

		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
		fileChooser.setFileFilter(filter);
		int returnVal = fileChooser.showOpenDialog(getContentPane());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			xmlFile = fileChooser.getSelectedFile();
			logger.debug("Got file: " + xmlFile.getAbsolutePath());
			loadModel();
			if (appEvent != null) {
				appEvent.getLoadEvent();
			}
		}
	}

	private void outPut() {
		if (mbt.getMachine() == null || mbt.getGraph() == null) {
			statisticsTextArea.setText("");
			variablesTextArea.setText("");
			return;
		}
		statisticsTextArea.setText(mbt.getStatisticsString());

		String str = (mbt.getMachine().getLastEdge() == null ? "" : "Edge: " + mbt.getMachine().getLastEdge().getLabelKey())
		    + (mbt.getMachine().getCurrentVertex() == null ? "" : "   Vertex: " + mbt.getMachine().getCurrentVertex().getLabelKey());
		getLatestVertexLabel().setText(str);

		if (status.isExecutingLogTest()) {
			if (parsedLogFile.get(currentStep).data != null) {
				str = parsedLogFile.get(currentStep).data;
			} else {
				str = variablesTextArea.getText();
			}
		} else {
			str = mbt.getMachine().getCurrentDataString();
		}
		str = str.replaceAll(";", newline);
		variablesTextArea.setText(str);
	}

	public void setButtons() {
		if (status.isStopped()) {
			loadButton.setEnabled(true);
			reloadButton.setEnabled(true);
			firstButton.setEnabled(false);
			backButton.setEnabled(false);
			runButton.setEnabled(false);
			pauseButton.setEnabled(false);
			nextButton.setEnabled(false);
			soapButton.setEnabled(false);
		} else if (status.isPaused() && status.isExecutingSoapTest()) {
			loadButton.setEnabled(true);
			reloadButton.setEnabled(true);
			firstButton.setEnabled(false);
			backButton.setEnabled(false);
			runButton.setEnabled(true);
			pauseButton.setEnabled(false);
			nextButton.setEnabled(true);
			soapButton.setEnabled(true);
		} else if (status.isRunning() || status.isNext() || status.isExecutingJavaTest() || status.isExecutingSoapTest()) {
			loadButton.setEnabled(false);
			reloadButton.setEnabled(false);
			firstButton.setEnabled(false);
			backButton.setEnabled(false);
			runButton.setEnabled(false);
			pauseButton.setEnabled(true);
			nextButton.setEnabled(false);
			soapButton.setEnabled(false);
		} else if (status.isPaused() && !(status.isNext() || status.isExecutingJavaTest())) {
			loadButton.setEnabled(true);
			reloadButton.setEnabled(true);
			firstButton.setEnabled(true);
			backButton.setEnabled(false);
			runButton.setEnabled(true);
			pauseButton.setEnabled(false);
			nextButton.setEnabled(true);
			soapButton.setEnabled(true);
		} else if (status.isExecutingLogTest()) {
			loadButton.setEnabled(false);
			reloadButton.setEnabled(false);
			firstButton.setEnabled(true);
			backButton.setEnabled(true);
			runButton.setEnabled(true);
			pauseButton.setEnabled(false);
			nextButton.setEnabled(true);
			soapButton.setEnabled(false);
		}
	}

	@SuppressWarnings("synthetic-access")
	private void loadModel() {
		setWaitCursor();
		if (executeMBT != null) {
			executeMBT.cancel(true);
		}
		logger.debug("Loading model");
		status.unsetState(Status.stopped);
		status.setState(Status.paused);
		statusBar.setMessage("Paused");
		try {
			if (soapButton.isSelected()) {
				mbt = Util.loadMbtAsWSFromXml(Util.getFile(xmlFile.getAbsolutePath()));
				mbt.setUseGUI(this);
				soapService.mbt = mbt;
			} else {
				mbt = Util.loadMbtFromXml(Util.getFile(xmlFile.getAbsolutePath()));
			}
			setTitle(title + " - " + xmlFile.getName());
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.warn(e.getMessage());
			JOptionPane.showMessageDialog(App.getInstance(), e.getMessage());
			reset();
			return;
		} catch (StopConditionException e) {
			logger.warn(e.getMessage());
			JOptionPane.showMessageDialog(App.getInstance(), e.getMessage());
			reset();
			return;
		} catch (GeneratorException e) {
			logger.warn(e.getMessage());
			JOptionPane.showMessageDialog(App.getInstance(), e.getMessage());
			reset();
			return;
		} catch (JDOMException e) {
			logger.warn(e.getMessage());
			JOptionPane.showMessageDialog(App.getInstance(), e.getMessage());
			reset();
			return;
		} catch (FileNotFoundException e) {
			logger.warn(e.getMessage());
			JOptionPane.showMessageDialog(App.getInstance(), e.getMessage());
			reset();
			return;
		} catch (IOException e) {
			logger.warn(e.getMessage());
			JOptionPane.showMessageDialog(App.getInstance(), e.getMessage());
			reset();
			return;
		} catch (RuntimeException e) {
			logger.warn(e.getMessage());
			JOptionPane.showMessageDialog(App.getInstance(), e.getMessage());
			reset();
			return;
		} catch (Exception e) {
			Util.logStackTraceToError(e);
			JOptionPane.showMessageDialog(App.getInstance(), e.getMessage());
			reset();
			return;
		}
		mbt.setUseGUI(this);
		centerOnVertex();
		updateLayout();

		if (!soapButton.isSelected()) {
			if (executeMBT != null) {
				executeMBT = null;
			}
			(executeMBT = new ExecuteMBT()).execute();
		}
		setButtons();
		setDefaultCursor();
	}

	private void reset() {
		statisticsTextArea.setText("");
		variablesTextArea.setText("");
		getLatestVertexLabel().setText("");

		setTitle(title);
		mbt.reset();
		setGraphLayout(new StaticLayout<Vertex, Edge>(new Graph()));
		getVv().setGraphLayout(getGraphLayout());
		status.reset();
		setButtons();
		setDefaultCursor();
	}

	public void run() {
		status.unsetState(Status.stopped);
		if (status.isExecutingSoapTest()) {
			status.unsetState(Status.paused);
		} else {
			status.setState(Status.running);
		}
		statusBar.setMessage("Running");
		setButtons();
	}

	public void executingJavaTest(boolean executingJavaTest) {
		if (executingJavaTest) {
			status.setState(Status.executingJavaTest);
			statusBar.setMessage("Executing a Java test");
		} else {
			status.unsetState(Status.executingJavaTest);
		}
		setButtons();
	}

	private class ExecuteMBT extends SwingWorker<Void, Void> {
		private Logger log = Util.setupLogger(ExecuteMBT.class);

		@Override
		protected Void doInBackground() {
			try {
				log.debug("GUI is starting to traverse the model");
				mbt.executePath();
			} catch (InterruptedException e) {
				//
			} catch (GuiStoppedExecution e) {
				//
			} catch (Exception e) {
				Util.logStackTraceToError(e);
				log.error(e.getMessage());
				log.error(e.getCause());
				JOptionPane.showMessageDialog(App.getInstance(), e.getCause().getMessage());
			}
			return null;
		}

		@Override
		protected void done() {
			super.done();
			log.debug("GUI is finished traversing the model");
			App.getInstance().stop();
		}
	}

	private void stop() {
		status.setState(Status.stopped);
		statusBar.setMessage("Stopped");
		setButtons();
	}

	public void pause() {
		status.unsetState(Status.stopped);
		status.unsetState(Status.running);
		status.setState(Status.paused);
		statusBar.setMessage("Paused");
		setButtons();
	}

	private void first() {
		if (status.isExecutingLogTest()) {
			mbt.setAllUnvisited();
			currentStep = 0;
			Integer index = parsedLogFile.get(currentStep).index;
			AbstractElement ae = mbt.getMachine().findElement(index);
			if (ae instanceof Edge) {
				mbt.getMachine().setLastEdge((Edge) ae);
				mbt.setCurrentVertex((Vertex) null);
			} else if (ae instanceof Vertex) {
				mbt.setCurrentVertex((Vertex) ae);
				mbt.getMachine().setLastEdge(null);
			}
			outPut();
			if (centerOnVertexButton.isSelected())
				centerOnVertex();
			getVv().repaint();
		}
	}

	private void last() {
		if (status.isExecutingLogTest()) {
			Integer index = null;
			for (; currentStep < parsedLogFile.size(); currentStep++) {
				index = parsedLogFile.get(currentStep).index;
				mbt.setAsVisited(index);
			}
			index = parsedLogFile.get(--currentStep).index;
			AbstractElement ae = mbt.getMachine().findElement(index);
			if (ae instanceof Edge) {
				mbt.getMachine().setLastEdge((Edge) ae);
				mbt.setCurrentVertex((Vertex) null);
			} else if (ae instanceof Vertex) {
				mbt.setCurrentVertex((Vertex) ae);
				mbt.getMachine().setLastEdge(null);
			}
			outPut();
			if (centerOnVertexButton.isSelected())
				centerOnVertex();
			getVv().repaint();
		}
	}

	private void prev() {
		if (status.isExecutingLogTest()) {
			Integer index = parsedLogFile.get(currentStep).index;
			AbstractElement ae = mbt.decrementVisited(index);
			if (currentStep > 0)
				currentStep--;
			index = parsedLogFile.get(currentStep).index;
			ae = mbt.decrementVisited(index);
			if (ae instanceof Edge) {
				mbt.getMachine().setLastEdge((Edge) ae);
				mbt.setCurrentVertex((Vertex) null);
			} else if (ae instanceof Vertex) {
				mbt.setCurrentVertex((Vertex) ae);
				mbt.getMachine().setLastEdge(null);
			}
			outPut();
			if (centerOnVertexButton.isSelected())
				centerOnVertex();
			getVv().repaint();
		}
	}

	private void next() {
		if (status.isExecutingLogTest()) {
			if (currentStep < parsedLogFile.size() - 1)
				currentStep++;
			Integer index = parsedLogFile.get(currentStep).index;
			AbstractElement ae = mbt.setAsVisited(index);
			if (ae instanceof Edge) {
				mbt.getMachine().setLastEdge((Edge) ae);
				mbt.setCurrentVertex((Vertex) null);
			} else if (ae instanceof Vertex) {
				mbt.setCurrentVertex((Vertex) ae);
				mbt.getMachine().setLastEdge(null);
			}
			outPut();
			if (centerOnVertexButton.isSelected())
				centerOnVertex();
			getVv().repaint();
			return;
		}
		if (!mbt.hasNextStep()) {
			status.setState(Status.stopped);
			statusBar.setMessage("Stopped");
			setButtons();
			return;
		}
		if (status.isNext()) {
			return;
		}
		status.setState(Status.next);
		setButtons();
		if (centerOnVertexButton.isSelected())
			centerOnVertex();
	}

	private void centerOnVertex() {
		Vertex v = mbt.getCurrentVertex();
		if (v != null) {
			Point2D target = getVv().getGraphLayout().transform(v);
			Point2D current = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(vv.getCenter());
			double dx = current.getX() - target.getX();
			double dy = current.getY() - target.getY();
			vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy);
		}
	}

	private void updateUI() {
		logger.debug("Updating the UI");
		outPut();
	}

	private void reload() {
		logger.debug("reload");
		status.setStopped();
		statusBar.setMessage("Stopped");
		loadModel();
	}

	public void setGraphLayout(Layout<Vertex, Edge> graphLayout) {
		logger.debug("setLayout using: " + graphLayout.toString());
		this.graphLayout = graphLayout;

		Transformer<Vertex, Point2D> vertexLocation = new Transformer<Vertex, Point2D>() {
			@Override
			public Point2D transform(Vertex v) {
				return v.getLocation();
			}
		};

		this.graphLayout.setInitializer(vertexLocation);
	}

	public VisualizationViewer<Vertex, Edge> getVv() {
		return vv;
	}

	private void setWaitCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	private void setDefaultCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public void updateLayout() {
		if (mbt == null || mbt.getGraph() == null) {
			return;
		}
		if (isVisible()) {
			logger.debug("updateLayout");
			setWaitCursor();
			setGraphLayout(new StaticLayout<Vertex, Edge>(mbt.getGraph()));
			getVv().setGraphLayout(getGraphLayout());
			updateUI();
			setDefaultCursor();
		}
	}

	public class MyEdgePaintFunction implements Transformer<Edge, Paint> {
		@Override
		public Paint transform(Edge e) {
			if (mbt.getMachine().getLastEdge() != null && mbt.getMachine().getLastEdge().equals(e))
				return Color.RED;
			else if (e.getVisitedKey() > 0)
				return Color.LIGHT_GRAY;
			else
				return Color.BLUE;
		}
	}

	private class MyEdgeStrokeFunction implements Transformer<Edge, Stroke> {
		private final Stroke UNVISITED = new BasicStroke(3);
		private final Stroke VISITED = new BasicStroke(1);
		private final Stroke CURRENT = new BasicStroke(3);

		@Override
		public Stroke transform(Edge e) {
			if (mbt.getMachine().getLastEdge() != null && mbt.getMachine().getLastEdge().equals(e))
				return CURRENT;
			else if (e.getVisitedKey() > 0)
				return VISITED;
			else
				return UNVISITED;
		}

	}

	private class MyVertexPaintFunction implements Transformer<Vertex, Paint> {
		@Override
		public Paint transform(Vertex v) {
			if (mbt.getMachine().isCurrentVertex(v))
				return Color.RED;
			else if (v.getVisitedKey() > 0)
				return Color.LIGHT_GRAY;
			else
				return Color.BLUE;
		}
	}

	private class MyVertexFillPaintFunction implements Transformer<Vertex, Paint> {
		@Override
		public Paint transform(Vertex v) {
			if (mbt.getMachine().isCurrentVertex(v))
				return Color.RED;
			else if (v.getVisitedKey() > 0)
				return Color.LIGHT_GRAY;
			else
				return v.getFillColor();
		}
	}

	private VisualizationViewer<Vertex, Edge> getGraphViewer() {
		if (mbt == null || mbt.getGraph() == null)
			setGraphLayout(new StaticLayout<Vertex, Edge>(new Graph()));
		else
			setGraphLayout(new StaticLayout<Vertex, Edge>(mbt.getGraph()));

		vv = new VisualizationViewer<Vertex, Edge>(getGraphLayout());

		DefaultModalGraphMouse<Vertex, Edge> graphMouse = new DefaultModalGraphMouse<Vertex, Edge>();
		vv.setGraphMouse(graphMouse);
		vv.setPickSupport(new ShapePickSupport<Vertex, Edge>(vv));
		vv.setBackground(Color.WHITE);
		vv.getRenderContext().setVertexDrawPaintTransformer(new MyVertexPaintFunction());
		vv.getRenderContext().setVertexFillPaintTransformer(new MyVertexFillPaintFunction());
		vv.getRenderContext().setEdgeDrawPaintTransformer(new MyEdgePaintFunction());
		vv.getRenderContext().setEdgeStrokeTransformer(new MyEdgeStrokeFunction());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

		Transformer<Vertex, Shape> vertexShape = new Transformer<Vertex, Shape>() {
			@Override
			public Shape transform(Vertex v) {
				return new Rectangle2D.Float(0, 0, v.getWidth(), v.getHeight());
			}
		};
		vv.getRenderContext().setVertexShapeTransformer(vertexShape);

		Transformer<Vertex, String> vertexStringer = new Transformer<Vertex, String>() {
			@Override
			public String transform(Vertex v) {
				return "<html><center>" + v.getFullLabelKey().replaceAll("\\n", "<p>") + "<p>INDEX=" + v.getIndexKey();
			}
		};
		vv.getRenderContext().setVertexLabelTransformer(vertexStringer);

		Transformer<Edge, String> edgeStringer = new Transformer<Edge, String>() {
			@Override
			public String transform(Edge e) {
				return "<html><center>" + e.getFullLabelKey().replaceAll("\\n", "<p>") + "<p>INDEX=" + e.getIndexKey();
			}
		};
		vv.getRenderContext().setEdgeLabelTransformer(edgeStringer);

		return vv;
	}

	private void createPanelStatistics() {
		panelStatistics = new JPanel();
		panelStatistics.setLayout(new BorderLayout());

		statisticsTextArea = new JTextArea();
		statisticsTextArea.setPreferredSize(new Dimension(300, 100));
		panelStatistics.add(statisticsTextArea, BorderLayout.CENTER);
	}

	private void createPanelVariables() {
		panelVariables = new JPanel();
		panelVariables.setLayout(new BorderLayout());

		variablesTextArea = new JTextArea();
		variablesTextArea.setPreferredSize(new Dimension(100, 100));
		panelVariables.add(variablesTextArea, BorderLayout.CENTER);
	}

	private JButton makeNavigationButton(String imageName, String actionCommand, String toolTipText, String altText, boolean enabled) {
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
			logger.error("Resource not found: " + imgLocation);
		}

		return button;
	}

	private JCheckBox makeNavigationCheckBoxButton(String imageName, String actionCommand, String toolTipText, String altText) {
		// Create and initialize the button.
		JCheckBox button = new JCheckBox();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		button.setText(altText);

		return button;
	}

	private final class LayoutChooser implements ActionListener {
		private final JComboBox jcb;
		private final VisualizationViewer<Vertex, Edge> vv;

		private LayoutChooser(JComboBox jcb, VisualizationViewer<Vertex, Edge> vv) {
			super();
			this.jcb = jcb;
			this.vv = vv;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent arg0) {
			Graph graph = null;
			if (mbt == null || mbt.getGraph() == null)
				graph = new Graph();
			else
				graph = mbt.getGraph();

			Object[] constructorArgs = { graph };

			Class<? extends Layout<Vertex, Edge>> layoutC = (Class<? extends Layout<Vertex, Edge>>) jcb.getSelectedItem();
			try {
				Constructor<? extends Layout<Vertex, Edge>> constructor = layoutC
				    .getConstructor(new Class[] { edu.uci.ics.jung.graph.Graph.class });
				Object o = constructor.newInstance(constructorArgs);
				Layout<Vertex, Edge> l = (Layout<Vertex, Edge>) o;
				l.setInitializer(vv.getGraphLayout());
				l.setSize(vv.getSize());

				LayoutTransition<Vertex, Edge> lt = new LayoutTransition<Vertex, Edge>(vv, vv.getGraphLayout(), l);
				Animator animator = new Animator(lt);
				animator.start();
				vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
				vv.repaint();

			} catch (Exception e) {
				Util.logStackTraceToError(e);
			}
		}
	}

	@SuppressWarnings("serial")
	private void addButtons(JToolBar toolBar) {
		loadButton = makeNavigationButton("open", LOAD, "Load a model (graphml file)", "Load", true);
		toolBar.add(loadButton);

		reloadButton = makeNavigationButton("reload", RELOAD, "Reload/Restart already loaded Model", "Reload", false);
		toolBar.add(reloadButton);

		firstButton = makeNavigationButton("first", FIRST, "Start at the beginning", "First", false);
		toolBar.add(firstButton);

		backButton = makeNavigationButton("back", BACK, "Backs a step", "Back", false);
		toolBar.add(backButton);

		runButton = makeNavigationButton("run", RUN, "Starts the execution/Take a step forward in the log", "Run", false);
		toolBar.add(runButton);

		pauseButton = makeNavigationButton("pause", PAUSE, "Pauses the execution", "Pause", false);
		toolBar.add(pauseButton);

		nextButton = makeNavigationButton("next", NEXT, "Walk a step in the model/Go to the end of log", "Next", false);
		toolBar.add(nextButton);

		soapButton = makeNavigationCheckBoxButton("soap", SOAP, "Run MBT in SOAP(Web Services) mode", "Soap");
		soapButton.setSelected(Util.readSoapGuiStartupState());
		toolBar.add(soapButton);

		centerOnVertexButton = makeNavigationCheckBoxButton("centerOnVertex", CENTERONVERTEX, "Center the layout on the current vertex",
		    "Center on current vertex");
		toolBar.add(centerOnVertexButton);

		@SuppressWarnings("rawtypes")
		Class[] combos = getCombos();
		final JComboBox jcb = new JComboBox(combos);
		// use a renderer to shorten the layout name presentation
		jcb.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				String valueString = value.toString();
				valueString = valueString.substring(valueString.lastIndexOf('.') + 1);
				return super.getListCellRendererComponent(list, valueString, index, isSelected, cellHasFocus);
			}
		});
		jcb.addActionListener(new LayoutChooser(jcb, getVv()));
		jcb.setSelectedItem(StaticLayout.class);

		toolBar.add(jcb);
	}

	private void createPanelGraph() {
		panelGraph = new JPanel();
		panelGraph.setLayout(new BorderLayout());
		setLatestVertexLabel(new JLabel(" "));
		getLatestVertexLabel().setHorizontalAlignment(SwingConstants.CENTER);
		getLatestVertexLabel().setOpaque(true);
		panelGraph.add(getLatestVertexLabel(), BorderLayout.NORTH);
		panelGraph.add(getGraphViewer(), BorderLayout.CENTER);
	}

	private void init() {
		setTitle(title);
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

		statusBar = new StatusBar();
		getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);

		int delay = 1000; // delay for 1 sec.
		int period = 500; // repeat every 0.5 sec.
		updateColorLatestVertexLabel = new Timer();

		updateColorLatestVertexLabel.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (getStatus().isStopped()) {
					if (getLatestVertexLabel().getBackground().equals(Color.GRAY)) {
						return;
					}
					getLatestVertexLabel().setBackground(Color.GRAY);
				}

				if (getStatus().isPaused() && !(getStatus().isNext() || getStatus().isExecutingJavaTest())) {
					getLatestVertexLabel().setBackground(Color.RED);
				} else if (getStatus().isRunning() || getStatus().isNext() || getStatus().isExecutingJavaTest()
				    || getStatus().isExecutingSoapTest()) {
					getLatestVertexLabel().setBackground(Color.GREEN);
				}
			}
		}, delay, period);

		if (xmlFile != null)
			loadModel();
		else if (graphmlFile != null && logFile != null) {
			setWaitCursor();
			try {
				mbt.readGraph(graphmlFile.getAbsolutePath());
			} catch (Exception e) {
				Util.logStackTraceToError(e);
				JOptionPane.showMessageDialog(App.getInstance(), "Failed to load model. " + e.getMessage());
			}
			try {
				parsedLogFile = ParseLog.readLog(logFile);
				currentStep = 0;
			} catch (IOException e) {
				Util.logStackTraceToError(e);
				JOptionPane.showMessageDialog(App.getInstance(), "Failed to load log file. " + e.getMessage());
			}

			Integer index = parsedLogFile.get(currentStep).index;
			AbstractElement ae = mbt.setAsVisited(index);
			if (ae instanceof Edge) {
				mbt.getMachine().setLastEdge((Edge) ae);
				mbt.setCurrentVertex((Vertex) null);
			}

			status.setState(Status.executingLogTest);
			status.unsetState(Status.stopped);
			setButtons();
			setDefaultCursor();
		}
	}

	// Private constructor prevents instantiation from other classes
	private App() {
		logger = Util.setupLogger(App.class);
		mbt = new ModelBasedTesting();
		init();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationByPlatform(true);
		setVisible(true);
		updateLayout();
	}

	/**
	 * AppHolder is loaded on the first execution of App.getInstance() or the
	 * first access to AppHolder.INSTANCE, not before.
	 */
	@SuppressWarnings("synthetic-access")
	private static class AppHolder {
		private static final App INSTANCE = new App();
	}

	@SuppressWarnings("synthetic-access")
	public static App getInstance() {
		return AppHolder.INSTANCE;
	}

	public static void main() {
		App.getInstance();
	}

	public static void main(String args[]) {
		if (args != null && args.length == 1) {
			App.getInstance().setXmlFile(new File(args[0]));
		} else if (args != null && args.length == 2) {
			App.getInstance().setGraphmlFile(new File(args[0]));
			App.getInstance().setLogFile(new File(args[1]));
		}
		App.getInstance();
	}

	public void setLatestVertexLabel(JLabel latestVertexLabel) {
		this.latestVertexLabel = latestVertexLabel;
	}

	public JLabel getLatestVertexLabel() {
		return latestVertexLabel;
	}

	public File getXmlFile() {
		return xmlFile;
	}

	public Layout<Vertex, Edge> getGraphLayout() {
		return graphLayout;
	}

	public void setXmlFile(File xmlFile) {
		this.xmlFile = xmlFile;
	}

	public void setGraphmlFile(File graphmlFile) {
		this.graphmlFile = graphmlFile;
	}

	public void setLogFile(File logFile) {
		this.logFile = logFile;
	}
}
