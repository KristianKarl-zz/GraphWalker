/**
 * 
 */
package org.tigris.mbt;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.tigris.mbt.statistics.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import edu.uci.ics.jung.graph.impl.AbstractElement;

/**
 * @author Johan Tejle
 *
 */
public class StatisticsManager {

	Hashtable counters;
	Document progress;
	private Transformer stylesheet;

	/**
	 * 
	 */
	public StatisticsManager() {
		this.counters = new Hashtable();
		this.progress = new Document(new Element("Statistics"));
	}
	
	public void addStatisicsCounter(String name, Statistics statisicsCounter)
	{
		counters.put(name, statisicsCounter);
	}
	
	public void addProgress(AbstractElement element)
	{
		for(Enumeration e=counters.keys();e.hasMoreElements();)
		{
			String key = (String)e.nextElement();
			Statistics stats = (Statistics) counters.get(key);
			stats.addProgress(element);
		}
		this.progress.getRootElement().addContent(getCurrentStatistic().detachRootElement());
	}

	public int[] getStatistic(String key)
	{
		Statistics stats = (Statistics) counters.get(key);
		int[] retur = {stats.getCurrent(),stats.getMax()};
		return retur;
	}

	public String getCurrentStatisticXml() {
		XMLOutputter outputter = new XMLOutputter();
		return outputter.outputString(getCurrentStatistic());
	}

	public String getFullProgressXml() {
		XMLOutputter outputter = new XMLOutputter();
		return outputter.outputString(this.progress);
	}
	
	public Document getCurrentStatistic()
	{
		Element root = new Element("Statistic");
		Document doc = new Document(root);
		for(Enumeration e=counters.keys();e.hasMoreElements();)
		{
			String key = (String)e.nextElement();
			int[] stats = getStatistic(key);

			Element child = new Element("Data");
			child.setAttribute("type", key);
			child.setAttribute("value", ""+stats[0]);
			child.setAttribute("max", ""+stats[1]);
			root.addContent(child);
		}
		return doc;
	}
	
	public void setReportTemplate(String filename)
	{
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    try {
	    	Templates stylesheetTemplate = transformerFactory.newTemplates( new StreamSource(new File(filename)));
	    this.stylesheet = stylesheetTemplate.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException("A serious configuration error detected in '"+filename+"' while creating report template.", e);
		}
		
	}
	
	public void writeFullReport() {
		writeFullReport(System.out);
	}
	
	public void writeFullReport(String fileName) {
		try {
			writeFullReport(new PrintStream(new File(fileName)));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not create or open '"+fileName+"'", e);
		}
	}
	
	public void writeFullReport(PrintStream out) {
	    
		try {
		    XMLOutputter xmlOutputter = new XMLOutputter();
		    StreamSource source = new StreamSource(new StringReader(xmlOutputter.outputString(this.progress)));
		    StreamResult result = new StreamResult(out);
		    this.stylesheet.transform(source, result);
		} catch(TransformerException e){
			throw new RuntimeException("Could not complete transformation of report.", e);
		}
	}

	/**
	 * @return the available Statistics counter names used by this manager
	 */
	public Set getCounterNames() {
		return new HashSet(counters.keySet());
	}
}
