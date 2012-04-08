/*
 * #%L
 * Maven GraphWalker Plugin
 * %%
 * Copyright (C) 2011 - 2012 GraphWalker
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
package org.graphwalker.maven.plugin.reports;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.Requirement;
import org.graphwalker.core.model.Vertex;
import org.graphwalker.core.statistics.EdgeStatistics;
import org.graphwalker.core.statistics.RequirementStatistics;
import org.graphwalker.core.statistics.VertexStatistics;
import org.graphwalker.core.utils.Resource;
import org.graphwalker.maven.plugin.Bundle;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * <p>XMLReportGenerator class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class XMLReportGenerator implements ReportGenerator {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private final File myReportDirectory;
    private final Model myModel;
    private final List<Throwable> myExceptions;
    private final Date myStartTime;

    /**
     * <p>Constructor for XMLReportGenerator.</p>
     *
     * @param reportDirectory a {@link java.io.File} object.
     * @param model           a {@link org.graphwalker.core.model.Model} object.
     * @param exceptions      a {@link java.util.List} object.
     * @param startTime       a {@link java.util.Date} object.
     */
    public XMLReportGenerator(File reportDirectory, Model model, List<Throwable> exceptions, Date startTime) {
        myReportDirectory = reportDirectory;
        myModel = model;
        myExceptions = exceptions;
        myStartTime = startTime;
    }

    private File getReportFile(File reportDirectory, Model model) {
        if (!reportDirectory.mkdirs()) {
            if (!reportDirectory.exists()) {
                throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.directory"));
            }
        }
        return new File(reportDirectory, model.getImplementation().getClass().getName() + ".xml");
    }

    private Xpp3Dom generateReport(Model model, List<Throwable> exceptions) {
        Xpp3Dom report = new Xpp3Dom("report");
        report.setAttribute("class", model.getImplementation().getClass().getName());
        report.setAttribute("group", model.getGroup());
        report.setAttribute("timestamp", String.valueOf(myStartTime.getTime()));

        VertexStatistics vertexStatistics = new VertexStatistics(model.getVertices());
        Xpp3Dom verticesElement = new Xpp3Dom("vertices");
        verticesElement.setAttribute("count", "" + vertexStatistics.getVertexCount());
        verticesElement.setAttribute("visited", "" + vertexStatistics.getVisitedVertexCount());
        verticesElement.setAttribute("blocked", "" + vertexStatistics.getBlockedVertexCount());
        verticesElement.setAttribute("unreachable", "" + vertexStatistics.getUnreachableVertexCount());
        //verticesElement.setAttribute("coverage", ""+model.getVertices().size());
        for (Vertex vertex : model.getVertices()) {
            Xpp3Dom vertexElement = new Xpp3Dom("vertex");
            vertexElement.setAttribute("id", vertex.getId());
            vertexElement.setAttribute("name", (null != vertex.getName() ? vertex.getName() : ""));
            vertexElement.setAttribute("status", vertex.getStatus().name());
            vertexElement.setAttribute("visitCount", "" + vertex.getVisitCount());
            verticesElement.addChild(vertexElement);
        }
        report.addChild(verticesElement);

        EdgeStatistics edgeStatistics = new EdgeStatistics(model.getEdges());
        Xpp3Dom edgesElement = new Xpp3Dom("edges");
        edgesElement.setAttribute("count", "" + edgeStatistics.getEdgeCount());
        edgesElement.setAttribute("visited", "" + edgeStatistics.getVisitedEdgeCount());
        edgesElement.setAttribute("blocked", "" + edgeStatistics.getBlockedEdgeCount());
        edgesElement.setAttribute("unreachable", "" + edgeStatistics.getUnreachableEdgeCount());
        //edgesElement.setAttribute("coverage", ""+model.getEdges().size());
        for (Edge edge : model.getEdges()) {
            Xpp3Dom edgeElement = new Xpp3Dom("edge");
            edgeElement.setAttribute("id", edge.getId());
            edgeElement.setAttribute("name", (null != edge.getName() ? edge.getName() : ""));
            edgeElement.setAttribute("status", edge.getStatus().name());
            edgeElement.setAttribute("visitCount", "" + edge.getVisitCount());
            edgesElement.addChild(edgeElement);
        }
        report.addChild(edgesElement);

        RequirementStatistics requirementStatistics = new RequirementStatistics(model.getRequirements());
        Xpp3Dom requirementsElement = new Xpp3Dom("requirements");
        requirementsElement.setAttribute("count", "" + requirementStatistics.getRequirementCount());
        requirementsElement.setAttribute("passed", "" + requirementStatistics.getPassedRequirementCount());
        requirementsElement.setAttribute("failed", "" + requirementStatistics.getFailedRequirementCount());
        requirementsElement.setAttribute("notCovered", "" + requirementStatistics.getNotCoveredRequirementCount());
        //requirementsElement.setAttribute("coverage", ""+model.getRequirements().size());
        for (Requirement requirement : model.getRequirements()) {
            Xpp3Dom requirementElement = new Xpp3Dom("requirement");
            requirementElement.setAttribute("id", requirement.getId());
            requirementElement.setAttribute("status", requirement.getStatus().name());
            requirementsElement.addChild(requirementElement);
        }
        report.addChild(requirementsElement);

        if (null != exceptions) {
            Xpp3Dom exceptionsElement = new Xpp3Dom("exceptions");
            for (Throwable throwable : exceptions) {
                Xpp3Dom exceptionElement = new Xpp3Dom("exception");
                exceptionElement.setValue(getStackTrace(throwable));
                exceptionsElement.addChild(exceptionElement);
            }
            report.addChild(exceptionsElement);
        }
        return report;
    }

    /**
     * <p>writeReport.</p>
     */
    public void writeReport() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getReportFile(myReportDirectory, myModel)), "UTF-8")));
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + LINE_SEPARATOR);
            Xpp3DomWriter.write(new PrettyPrintXMLWriter(writer), generateReport(myModel, myExceptions));
        } catch (UnsupportedEncodingException e) {
            throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.encoding"), e);
        } catch (FileNotFoundException e) {
            throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.creating", e.getMessage()), e);
        } finally {
            IOUtil.close(writer);
        }
    }

    private String getStackTrace(Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder(LINE_SEPARATOR);
        stringBuilder.append("      ");
        stringBuilder.append(throwable.toString());
        for (StackTraceElement element : throwable.getStackTrace()) {
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append("        at ");
            stringBuilder.append(element.toString());
        }
        if (null != throwable.getCause()) {
            appendStackTraceCause(stringBuilder, throwable.getStackTrace(), throwable.getCause());
        }
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append("    ");
        return stringBuilder.toString();
    }

    private void appendStackTraceCause(StringBuilder stringBuilder, StackTraceElement[] stackTraceElements, Throwable throwable) {
        StackTraceElement[] causedStackTraceElements = throwable.getStackTrace();
        int m = causedStackTraceElements.length - 1, n = stackTraceElements.length - 1;
        while (m >= 0 && n >= 0 && causedStackTraceElements[m].equals(stackTraceElements[n])) {
            m--;
            n--;
        }
        int framesInCommon = causedStackTraceElements.length - 1 - m;
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append("      ").append("Caused by: ");
        stringBuilder.append(throwable.toString());
        for (int i = 0; i <= m; i++) {
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append("        at ");
            stringBuilder.append(causedStackTraceElements[i]);
        }
        if (framesInCommon != 0) {
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append("        ... ");
            stringBuilder.append(framesInCommon);
            stringBuilder.append(" more");
        }
        if (null != throwable.getCause()) {
            appendStackTraceCause(stringBuilder, stackTraceElements, throwable.getCause());
        }
    }
}
