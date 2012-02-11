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
import org.graphwalker.core.model.Model;
import org.graphwalker.core.statistics.EdgeStatistics;
import org.graphwalker.core.statistics.RequirementStatistics;
import org.graphwalker.core.statistics.VertexStatistics;
import org.graphwalker.core.utils.Resource;
import org.graphwalker.maven.plugin.Bundle;

import java.io.*;

/**
 * <p>XMLReportGenerator class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class XMLReportGenerator {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private XMLReportGenerator() {
    }
    
    private static File getReportFile(Model model, File reportDirectory) {
        if (!reportDirectory.mkdirs()) {
            if (!reportDirectory.exists()) {
                throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.directory"));
            }
        }
        return new File(reportDirectory, model.getImplementation().getClass().getName()+".xml");
    }

    private static Xpp3Dom generateReport(Model model) {
        Xpp3Dom report = new Xpp3Dom("report");
        report.setAttribute("class", model.getImplementation().getClass().getName());
        report.setAttribute("group", model.getGroup());
        VertexStatistics vertexStatistics = new VertexStatistics(model.getVertices());
        Xpp3Dom vertices = new Xpp3Dom("vertices");
        vertices.setAttribute("count", ""+vertexStatistics.getVertexCount());
        vertices.setAttribute("visited", ""+vertexStatistics.getVisitedVertexCount());
        vertices.setAttribute("blocked", ""+vertexStatistics.getBlockedVertexCount());
        vertices.setAttribute("unreachable", ""+vertexStatistics.getUnreachableVertexCount());
        //vertices.setAttribute("coverage", ""+model.getVertices().size());
        report.addChild(vertices);
        EdgeStatistics edgeStatistics = new EdgeStatistics(model.getEdges());
        Xpp3Dom edges = new Xpp3Dom("edges");
        edges.setAttribute("count", ""+edgeStatistics.getEdgeCount());
        edges.setAttribute("visited", ""+edgeStatistics.getVisitedEdgeCount());
        edges.setAttribute("blocked", ""+edgeStatistics.getBlockedEdgeCount());
        edges.setAttribute("unreachable", ""+edgeStatistics.getUnreachableEdgeCount());
        //edges.setAttribute("coverage", ""+model.getEdges().size());
        report.addChild(edges);
        RequirementStatistics requirementStatistics = new RequirementStatistics(model.getRequirements());
        Xpp3Dom requirements = new Xpp3Dom("requirements");
        requirements.setAttribute("count", ""+requirementStatistics.getRequirementCount());
        requirements.setAttribute("passed", ""+requirementStatistics.getPassedRequirementCount());
        requirements.setAttribute("failed", ""+requirementStatistics.getFailedRequirementCount());
        requirements.setAttribute("notCovered", ""+requirementStatistics.getNotCoveredRequirementCount());
        //requirements.setAttribute("coverage", ""+model.getRequirements().size());
        report.addChild(requirements);
        return report;
    }

    /**
     * <p>writeReport.</p>
     */
    public static void writeReport(Model model, File reportDirectory) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getReportFile(model, reportDirectory)), "UTF-8")));
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+LINE_SEPARATOR);
            Xpp3DomWriter.write(new PrettyPrintXMLWriter(writer), generateReport(model));
        } catch (UnsupportedEncodingException e) {
            throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.encoding"), e);
        } catch (FileNotFoundException e) {
            throw new ReportException(Resource.getText(Bundle.NAME, "exception.report.creating", e.getMessage()), e);
        } finally {
            IOUtil.close(writer);
        }
    }

}
