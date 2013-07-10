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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.graphwalker.graph.AbstractElement;
import org.graphwalker.statistics.Statistics;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.jdom2.transform.JDOMSource;

/**
 * @author Johan Tejle
 * 
 */
public class StatisticsManager {

  static private Logger log = Util.setupLogger(StatisticsManager.class);
  private Hashtable<String, Statistics> counters;
  private Document progress;
  private Transformer styleTemplate;

  /**
	 * 
	 */
  public StatisticsManager() {
    this.counters = new Hashtable<String, Statistics>();
    this.progress = new Document(new Element("Statistics"));
  }

  public void addStatisicsCounter(String name, Statistics statisicsCounter) {
    counters.put(name, statisicsCounter);
  }

  public void addProgress(AbstractElement element) {
    for (Enumeration<String> e = counters.keys(); e.hasMoreElements();) {
      String key = e.nextElement();
      Statistics stats = counters.get(key);
      stats.addProgress(element);
    }
    this.progress.getRootElement().addContent(getCurrentStatistic().detachRootElement());
  }

  private int[] getStatistic(String key) {
    Statistics stats = counters.get(key);
    return new int[] {stats.getCurrent(), stats.getMax()};
  }

  public String getCurrentStatisticXml() {
    XMLOutputter outputter = new XMLOutputter();
    return outputter.outputString(getCurrentStatistic());
  }

  public String getFullProgressXml() {
    XMLOutputter outputter = new XMLOutputter();
    return outputter.outputString(this.progress);
  }

  public Document getCurrentStatistic() {
    Element root = new Element("Statistic");
    Document doc = new Document(root);
    for (Enumeration<String> e = counters.keys(); e.hasMoreElements();) {
      String key = e.nextElement();
      int[] stats = getStatistic(key);

      Element child = new Element("Data");
      child.setAttribute("type", key);
      child.setAttribute("value", "" + stats[0]);
      child.setAttribute("max", "" + stats[1]);
      root.addContent(child);
    }
    return doc;
  }

  public void setReportTemplate(InputStream inputStream) {
    log.info("Setting template to '" + inputStream + "'");
    try {
      styleTemplate = TransformerFactory.newInstance().newTemplates(new StreamSource(inputStream)).newTransformer();
    } catch (TransformerConfigurationException e) {
      throw new RuntimeException("A serious configuration exception detected in '" + inputStream + "' while creating report template.", e);
    } catch (TransformerFactoryConfigurationError e) {
      throw new RuntimeException("A serious configuration error detected in '" + inputStream + "' while creating report template.", e);
    }
  }

  protected void writeFullReport(String fileName) {
    try {
      writeFullReport(new PrintStream(new File(fileName)));
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Could not create or open '" + fileName + "'", e);
    }
  }

  public void writeFullReport(PrintStream out) {
    log.info("Writing full report");
    try {
      styleTemplate.transform(new JDOMSource((Document) this.progress.clone()), new StreamResult(out));
      // out.close();
    } catch (TransformerException e) {
      throw new RuntimeException("Could not create report", e);
    }
  }

  /**
   * @return the available Statistics counter names used by this manager
   */
  public Set<String> getCounterNames() {
    return new HashSet<String>(counters.keySet());
  }
}
