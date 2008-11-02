package org.tigris.mbt.GUI;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.tigris.mbt.CLI;
import org.tigris.mbt.Keywords;

public class mbt extends JFrame implements ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;
	private CLI cli = null;

	Thread workerThread = null;

	JFileChooser fileChooser;

	JPanel commandPanel;
	JComboBox commandsComboBox;

	JPanel modelPanel;
	JButton browseModelButton;
	JTextField modelPathTextField;

	JPanel xmlPanel;
	JButton browseXMLButton;
	JTextField xmlTextField;

	JPanel templatePanel;
	JLabel templateLabel;
	JButton browseTemplateButton;
	JTextField templatePathTextField;

	JPanel outputFilePanel;
	JLabel outputFileLabel;
	JButton browseOutputFileButton;
	JTextField outputFileTextField;

	JPanel generatorPanel;
	JLabel generatorLabel;
	JComboBox generatorsComboBox;

	JPanel optionsPanel;
	JCheckBox extendedCheckBox;
	JCheckBox weightCheckBox;

	JPanel stopConditionPanel;
	JLabel stopConditionLabel;
	JComboBox stopConditionsComboBox;
	JLabel stopConditionValueLabel;
	JTextField stopConditionValueTextField;

	JPanel outputPanel;
	static JTextArea outputTextArea;

	JPanel runPanel;
	JButton runButton;

	PrintStream aPrintStream = new PrintStream(new FilteredStream(
			new ByteArrayOutputStream()), true);

	class FilteredStream extends FilterOutputStream {
		public FilteredStream(OutputStream aStream) {
			super(aStream);
		}

		public void write(byte b[]) throws IOException {
			String aString = new String(b);
			outputTextArea.append(aString);
			// Make sure the last line is always visible
			outputTextArea.setCaretPosition(outputTextArea.getDocument()
					.getLength());
		}

		public void write(byte b[], int off, int len) throws IOException {
			String aString = new String(b, off, len);
			outputTextArea.append(aString);
			// Make sure the last line is always visible
			outputTextArea.setCaretPosition(outputTextArea.getDocument()
					.getLength());
		}
	}

	public mbt() throws IOException {
		setTitle("MBT Setup and Launch Utility");

		// Create a file chooser
		fileChooser = new JFileChooser();
		GridBagConstraints c = new GridBagConstraints();

		commandPanel = new JPanel(new GridBagLayout());
		setMyConstraints(c, 0, 0, GridBagConstraints.EAST);
		commandPanel.add(new JLabel("Command:"), c);
		commandsComboBox = new JComboBox();
		commandsComboBox.addItem("online");
		commandsComboBox.addItem("offline");
		commandsComboBox.addItem("requirements");
		commandsComboBox.addItem("methods");
		commandsComboBox.addItem("merge");
		commandsComboBox.addItem("xml");
		commandsComboBox.addItem("soap");
		commandsComboBox.addItem("source");
		commandsComboBox.addItemListener(this);
		setMyConstraints(c, 1, 0, GridBagConstraints.WEST);
		commandPanel.add(commandsComboBox, c);

		modelPanel = new JPanel(new GridBagLayout());
		setMyConstraints(c, 0, 0, GridBagConstraints.EAST);
		modelPanel.add(new JLabel("Model:"), c);
		modelPathTextField = new JTextField(20);
		setMyConstraints(c, 1, 0, GridBagConstraints.EAST);
		modelPanel.add(modelPathTextField, c);
		browseModelButton = new JButton("Browse");
		setMyConstraints(c, 2, 0, GridBagConstraints.EAST);
		browseModelButton.addActionListener(this);
		modelPanel.add(browseModelButton, c);

		xmlPanel = new JPanel(new GridBagLayout());
		setMyConstraints(c, 0, 0, GridBagConstraints.EAST);
		xmlPanel.add(new JLabel("XML:"), c);
		xmlTextField = new JTextField(20);
		setMyConstraints(c, 1, 0, GridBagConstraints.EAST);
		xmlPanel.add(xmlTextField, c);
		browseXMLButton = new JButton("Browse");
		setMyConstraints(c, 2, 0, GridBagConstraints.EAST);
		browseXMLButton.addActionListener(this);
		xmlPanel.add(browseXMLButton, c);
		
		templatePanel = new JPanel(new GridBagLayout());
		templateLabel = new JLabel("Template:");
		setMyConstraints(c, 0, 0, GridBagConstraints.EAST);
		templatePanel.add(templateLabel, c);
		templatePathTextField = new JTextField(20);
		setMyConstraints(c, 1, 0, GridBagConstraints.EAST);
		templatePanel.add(templatePathTextField, c);
		browseTemplateButton = new JButton("Browse");
		browseTemplateButton.addActionListener(this);
		setMyConstraints(c, 2, 0, GridBagConstraints.EAST);
		templatePanel.add(browseTemplateButton, c);

		outputFilePanel = new JPanel(new GridBagLayout());
		outputFileLabel = new JLabel("Output:");
		setMyConstraints(c, 0, 0, GridBagConstraints.EAST);
		outputFilePanel.add(outputFileLabel, c);
		outputFileTextField = new JTextField(20);
		setMyConstraints(c, 1, 0, GridBagConstraints.EAST);
		outputFilePanel.add(outputFileTextField, c);
		browseOutputFileButton = new JButton("Browse");
		browseOutputFileButton.addActionListener(this);
		setMyConstraints(c, 2, 0, GridBagConstraints.EAST);
		outputFilePanel.add(browseOutputFileButton, c);

		generatorPanel = new JPanel(new GridBagLayout());
		generatorLabel = new JLabel("Generator:");
		setMyConstraints(c, 0, 0, GridBagConstraints.EAST);
		generatorPanel.add(generatorLabel, c);
		generatorsComboBox = new JComboBox();
		setMyConstraints(c, 1, 0, GridBagConstraints.EAST);
		generatorPanel.add(generatorsComboBox, c);

		optionsPanel = new JPanel(new GridBagLayout());
		extendedCheckBox = new JCheckBox("Extendex FSM");
		setMyConstraints(c, 0, 0, GridBagConstraints.EAST);
		optionsPanel.add(extendedCheckBox, c);
		weightCheckBox = new JCheckBox("Weight");
		setMyConstraints(c, 1, 0, GridBagConstraints.EAST);
		optionsPanel.add(weightCheckBox, c);

		stopConditionPanel = new JPanel(new GridBagLayout());
		stopConditionLabel = new JLabel("Stop condition:");
		setMyConstraints(c, 0, 0, GridBagConstraints.EAST);
		stopConditionPanel.add(stopConditionLabel, c);
		stopConditionsComboBox = new JComboBox();
		Set list = Keywords.getStopConditions();
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String g = (String) iterator.next();
			stopConditionsComboBox.addItem(g);
		}
		setMyConstraints(c, 1, 0, GridBagConstraints.EAST);
		stopConditionPanel.add(stopConditionsComboBox, c);
		stopConditionValueLabel = new JLabel("Value:");
		setMyConstraints(c, 2, 0, GridBagConstraints.EAST);
		stopConditionPanel.add(stopConditionValueLabel, c);
		stopConditionValueTextField = new JTextField(5);
		setMyConstraints(c, 3, 0, GridBagConstraints.EAST);
		stopConditionPanel.add(stopConditionValueTextField, c);

		outputPanel = new JPanel(new GridBagLayout());
		outputPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
		outputTextArea = new JTextArea(10, 35);
		setMyConstraints(c, 0, 0, GridBagConstraints.EAST);
		outputPanel.add(new JScrollPane(outputTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), c);

		runPanel = new JPanel(new GridBagLayout());
		runButton = new JButton("Run");
		setMyConstraints(c, 1, 0, GridBagConstraints.EAST);
		runButton.addActionListener(this);
		runPanel.add(runButton, c);

		setLayout(new GridBagLayout());
		setOnlinePanels();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);

		System.setOut(aPrintStream);
		System.setErr(aPrintStream);
	}

	public void actionPerformed(ActionEvent e) {
		// Handle open button action.
		if (e.getSource() == browseModelButton) {
			fileChooser
					.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int returnVal = fileChooser.showOpenDialog(mbt.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				modelPathTextField.setText(file.getAbsolutePath());
			}
		} else if (e.getSource() == browseOutputFileButton) {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = fileChooser.showOpenDialog(mbt.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				outputFileTextField.setText(file.getAbsolutePath());
			}
		} else if (e.getSource() == browseTemplateButton) {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = fileChooser.showOpenDialog(mbt.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				templatePathTextField.setText(file.getAbsolutePath());
			}
		} else if (e.getSource() == browseXMLButton) {
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = fileChooser.showOpenDialog(mbt.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				xmlTextField.setText(file.getAbsolutePath());
			}
		} else if (e.getSource() == runButton) {
			if (runButton.getText().equals("Stop")) {
				workerThread.stop();
				workerThread = null;
				cli.StopSOAP();
				cli = null;
				setGUIIdleMode();
			} else if (commandsComboBox.getSelectedItem().equals("merge")) {
				workerThread = new MergeThread();
				workerThread.start();
			} else if (commandsComboBox.getSelectedItem().equals("xml")) {
				workerThread = new XMLThread();
				workerThread.start();
			} else if (commandsComboBox.getSelectedItem().equals("soap")) {
				workerThread = new SOAPThread();
				workerThread.start();
			} else if (commandsComboBox.getSelectedItem().equals("source")) {
				workerThread = new SourceThread();
				workerThread.start();
			} else if (commandsComboBox.getSelectedItem().equals("offline")) {
				runButton.setText("Stop");
				workerThread = new OfflineThread();
				workerThread.start();
			} else if (commandsComboBox.getSelectedItem().equals("online")) {
				runButton.setText("Stop");
				workerThread = new OnlineThread();
				workerThread.start();
			} else if (commandsComboBox.getSelectedItem()
					.equals("requirements")) {
				workerThread = new RequirementsThread();
				workerThread.start();
			} else if (commandsComboBox.getSelectedItem().equals("methods")) {
				workerThread = new MethodsThread();
				workerThread.start();
			}
		}
	}

	public void itemStateChanged(ItemEvent e) {
		// Handle command action
		if (e.getSource() == commandsComboBox) {
			if (commandsComboBox.getSelectedItem().equals("source")) {
				setSourcePanels();
			} else if (commandsComboBox.getSelectedItem()
					.equals("requirements")) {
				setRequirementsPanels();
			} else if (commandsComboBox.getSelectedItem().equals("xml")) {
				setXMLPanels();
			} else if (commandsComboBox.getSelectedItem().equals("offline")) {
				setOfflinePanels();
			} else if (commandsComboBox.getSelectedItem().equals("online")) {
				setOnlinePanels();
			} else if (commandsComboBox.getSelectedItem().equals("methods")) {
				setMethodsPanels();
			} else if (commandsComboBox.getSelectedItem().equals("merge")) {
				setMergePanels();
			} else if (commandsComboBox.getSelectedItem().equals("soap")) {
				setSOAPPanels();
			}
		}
	}

	public static void main(String[] args) {
		try {
			new mbt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void setMyConstraints(GridBagConstraints c, int gridx,
			int gridy, int anchor) {
		c.gridx = gridx;
		c.gridy = gridy;
		c.anchor = anchor;
	}

	private static void reset() {
		outputTextArea.setText("");
	}

	private void removeAllPanels() {
		remove(commandPanel);
		remove(modelPanel);
		remove(xmlPanel);
		remove(outputFilePanel);
		remove(templatePanel);
		remove(generatorPanel);
		remove(optionsPanel);
		remove(stopConditionPanel);
		remove(outputPanel);
		remove(runPanel);
	}

	private void setMergePanels() {
		int index = 0;
		removeAllPanels();
		GridBagConstraints c = new GridBagConstraints();
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(commandPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(modelPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(outputFilePanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(outputPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(runPanel, c);
		pack();
	}

	private void setSOAPPanels() {
		setXMLPanels();
	}

	private void setXMLPanels() {
		int index = 0;
		removeAllPanels();
		GridBagConstraints c = new GridBagConstraints();
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(commandPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(xmlPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(outputFilePanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(outputPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(runPanel, c);
		pack();
	}

	private void setSourcePanels() {
		int index = 0;
		removeAllPanels();
		GridBagConstraints c = new GridBagConstraints();
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(commandPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(modelPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(templatePanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(outputFilePanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(outputPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(runPanel, c);
		pack();
	}

	private void setRequirementsPanels() {
		int index = 0;
		removeAllPanels();
		GridBagConstraints c = new GridBagConstraints();
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(commandPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(modelPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(outputFilePanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(outputPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(runPanel, c);
		pack();
	}

	private void setMethodsPanels() {
		int index = 0;
		removeAllPanels();
		GridBagConstraints c = new GridBagConstraints();
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(commandPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(modelPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(outputFilePanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(outputPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(runPanel, c);
		pack();
	}

	private void setOfflinePanels() {
		int index = 0;
		removeAllPanels();
		generatorsComboBox.removeAllItems();
		generatorsComboBox.addItem("RANDOM");
		generatorsComboBox.addItem("A_STAR");
		generatorsComboBox.addItem("SHORTEST_NON_OPTIMIZED");
		GridBagConstraints c = new GridBagConstraints();
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(commandPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(modelPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(outputFilePanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(generatorPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(optionsPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(stopConditionPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(outputPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(runPanel, c);
		pack();
	}

	private void setOnlinePanels() {
		int index = 0;
		removeAllPanels();
		generatorsComboBox.removeAllItems();
		generatorsComboBox.addItem("RANDOM");
		generatorsComboBox.addItem("A_STAR");
		generatorsComboBox.addItem("SHORTEST_NON_OPTIMIZED");
		GridBagConstraints c = new GridBagConstraints();
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(commandPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(modelPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(outputFilePanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(generatorPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(optionsPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(stopConditionPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(outputPanel, c);
		setMyConstraints(c, 0, index++, GridBagConstraints.CENTER);
		add(runPanel, c);
		pack();
	}

	public class OfflineThread extends Thread {
		public void run() {
			reset();
			setGUIRunningMode();
			String args[] = {
					"offline",
					"-f",
					modelPathTextField.getText(),
					"-g",
					(String) generatorsComboBox.getSelectedItem(),
					"-s",
					(String) stopConditionsComboBox.getSelectedItem() + ":"
							+ stopConditionValueTextField.getText() };
			cli = new CLI();
			cli.main(args);
			setGUIIdleMode();
		}
	}

	public class OnlineThread extends Thread {
		public void run() {
			reset();
			setGUIRunningMode();
			String args[] = {
					"online",
					"-f",
					modelPathTextField.getText(),
					"-g",
					(String) generatorsComboBox.getSelectedItem(),
					"-s",
					(String) stopConditionsComboBox.getSelectedItem() + ":"
							+ stopConditionValueTextField.getText() };
			cli = new CLI();
			cli.main(args);
			setGUIIdleMode();
		}
	}

	public class SourceThread extends Thread {
		public void run() {
			reset();
			setGUIRunningMode();
			String args[] = { "source", "-f", modelPathTextField.getText(),
					"-t", templatePathTextField.getText() };
			cli = new CLI();
			cli.main(args);
			setGUIIdleMode();
		}
	}

	public class XMLThread extends Thread {
		public void run() {
			reset();
			setGUIRunningMode();
			String args[] = { "xml", "-f", xmlTextField.getText() };
			cli = new CLI();
			cli.main(args);
			setGUIIdleMode();
		}
	}

	public class SOAPThread extends Thread {
		public void run() {
			reset();
			setGUIRunningMode();
			String args[] = { "soap", "-f", xmlTextField.getText() };
			cli = new CLI();
			cli.main(args);
		}
	}

	public class MergeThread extends Thread {
		public void run() {
			reset();
			setGUIRunningMode();
			String args[] = { "merge", "-f", modelPathTextField.getText() };
			cli = new CLI();
			cli.main(args);
			setGUIIdleMode();
		}
	}

	public class RequirementsThread extends Thread {
		public void run() {
			reset();
			setGUIRunningMode();
			String args[] = { "requirements", "-f",
					modelPathTextField.getText() };
			cli = new CLI();
			cli.main(args);
			setGUIIdleMode();
		}
	}

	public class MethodsThread extends Thread {
		public void run() {
			reset();
			setGUIRunningMode();
			String args[] = { "methods", "-f", modelPathTextField.getText() };
			cli = new CLI();
			cli.main(args);
			setGUIIdleMode();
		}
	}

	private void setGUIRunningMode() {
		runButton.setText("Stop");
	}

	private void setGUIIdleMode() {
		runButton.setText("Run");
	}
}
