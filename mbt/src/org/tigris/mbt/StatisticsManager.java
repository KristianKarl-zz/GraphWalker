/**
 * 
 */
package org.tigris.mbt;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.tigris.mbt.statistics.*;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
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
	private String xsltReport;
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
		this.xsltReport = Util.readFile(filename);
		
	}
	
	public String getFullReport() {
	    
		try {
			// Set up the XSLT stylesheet for use with Xalan-J 2
		    TransformerFactory transformerFactory = TransformerFactory.newInstance();
		    Templates stylesheet = transformerFactory.newTemplates( new StreamSource(new StringReader(this.xsltReport)));
		    Transformer processor = stylesheet.newTransformer();
		    // Use I/O streams for source files
		    PipedInputStream sourceIn = new PipedInputStream();
		    PipedOutputStream sourceOut = new PipedOutputStream(sourceIn);
		    StreamSource source = new StreamSource(sourceIn);
		    // Use I/O streams for output files
		    PipedInputStream resultIn = new PipedInputStream();
		    PipedOutputStream resultOut = new PipedOutputStream(resultIn);
		    // Convert the output target for use in Xalan-J 2
		    StreamResult result = new StreamResult(resultOut);
		    // Get a means for output of the JDOM Document
		    XMLOutputter xmlOutputter = new XMLOutputter();
		    // Output to the I/O stream
		    xmlOutputter.output(this.progress, sourceOut);
		    sourceOut.close();
		    // Feed the resultant I/O stream into the XSLT processor
		    processor.transform(source, result);
		    resultOut.close();
		    // Convert the resultant transformed document back to JDOM
		    SAXBuilder builder = new SAXBuilder();
		    Document resultDoc = builder.build(resultIn);
		    return xmlOutputter.outputString(resultDoc);
		} catch(IOException e){
			throw new RuntimeException("Could not read needed information for report.", e);
		} catch(TransformerException e){
			throw new RuntimeException("Could not complete transformation of report.", e);
		} catch (JDOMException e) {
			throw new RuntimeException("Could not rebuild report after transformation.", e);
		}
	}
}
