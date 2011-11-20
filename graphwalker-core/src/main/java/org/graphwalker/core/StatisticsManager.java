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

import org.apache.log4j.Logger;
import org.graphwalker.core.graph.AbstractElement;
import org.graphwalker.core.statistics.Statistics;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMSource;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * <p>StatisticsManager class.</p>
 */
public class StatisticsManager {

    static private Logger log = Util.setupLogger(StatisticsManager.class);
    private Hashtable<String, Statistics> counters;
    private Document progress;
    private Transformer styleTemplate;

    /**
     * <p>Constructor for StatisticsManager.</p>
     */
    public StatisticsManager() {
        this.counters = new Hashtable<String, Statistics>();
        this.progress = new Document(new Element("Statistics"));
    }

    /**
     * <p>addStatisicsCounter.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param statisicsCounter a {@link org.graphwalker.core.statistics.Statistics} object.
     */
    public void addStatisicsCounter(String name, Statistics statisicsCounter) {
        counters.put(name, statisicsCounter);
    }

    /**
     * <p>addProgress.</p>
     *
     * @param element a {@link org.graphwalker.core.graph.AbstractElement} object.
     */
    public void addProgress(AbstractElement element) {
        for (Enumeration<String> e = counters.keys(); e.hasMoreElements(); ) {
            String key = e.nextElement();
            Statistics stats = counters.get(key);
            stats.addProgress(element);
        }
        this.progress.getRootElement().addContent(getCurrentStatistic().detachRootElement());
    }

    private int[] getStatistic(String key) {
        Statistics stats = counters.get(key);
        return new int[]{stats.getCurrent(), stats.getMax()};
    }

    /**
     * <p>getCurrentStatisticXml.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCurrentStatisticXml() {
        XMLOutputter outputter = new XMLOutputter();
        return outputter.outputString(getCurrentStatistic());
    }

    /**
     * <p>getFullProgressXml.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFullProgressXml() {
        XMLOutputter outputter = new XMLOutputter();
        return outputter.outputString(this.progress);
    }

    /**
     * <p>getCurrentStatistic.</p>
     *
     * @return a {@link org.jdom.Document} object.
     */
    public Document getCurrentStatistic() {
        Element root = new Element("Statistic");
        Document doc = new Document(root);
        for (Enumeration<String> e = counters.keys(); e.hasMoreElements(); ) {
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

    /**
     * <p>setReportTemplate.</p>
     *
     * @param inputStream a {@link java.io.InputStream} object.
     */
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

    /**
     * <p>writeFullReport.</p>
     *
     * @param fileName a {@link java.lang.String} object.
     */
    public void writeFullReport(String fileName) {
        try {
            writeFullReport(new PrintStream(new File(fileName)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not create or open '" + fileName + "'", e);
        }
    }

    /**
     * <p>writeFullReport.</p>
     *
     * @param out a {@link java.io.PrintStream} object.
     */
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
     * <p>getCounterNames.</p>
     *
     * @return the available Statistics counter names used by this manager
     */
    public Set<String> getCounterNames() {
        return new HashSet<String>(counters.keySet());
    }
}
