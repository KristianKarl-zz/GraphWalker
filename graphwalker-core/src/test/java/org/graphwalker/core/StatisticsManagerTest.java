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

import junit.framework.TestCase;
import org.graphwalker.core.graph.Edge;
import org.graphwalker.core.graph.Graph;
import org.graphwalker.core.graph.Vertex;
import org.graphwalker.core.statistics.EdgeCoverageStatistics;
import org.graphwalker.core.statistics.EdgeSequenceCoverageStatistics;
import org.graphwalker.core.statistics.RequirementCoverageStatistics;
import org.graphwalker.core.statistics.VertexCoverageStatistics;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author Johan Tejle
 */
public class StatisticsManagerTest extends TestCase {

    Graph graph;
    Vertex start;
    Vertex v1;
    Vertex v2;
    Edge e1;
    Edge e2;
    Edge e3;
    Edge e4;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        graph = new Graph();

        start = Util.addVertexToGraph(graph, "Start");

        v1 = Util.addVertexToGraph(graph, "V1");
        v1.setReqTagKey("REQ002");

        v2 = Util.addVertexToGraph(graph, "V2");
        v2.setReqTagKey("REQ004");

        e1 = Util.addEdgeToGraph(graph, start, v1, "E1", null, null, "x=1;y=new Vector()");
        e1.setReqTagKey("REQ001,REQ002");

        e2 = Util.addEdgeToGraph(graph, v1, v2, "E2", null, null, "x=2");
        e2.setReqTagKey("REQ003");

        e3 = Util.addEdgeToGraph(graph, v2, v2, "E3", null, "x<6", "x++");

        e4 = Util.addEdgeToGraph(graph, v2, v1, "E4", null, "y.size()<3", "y.add(x)");
    }

    public void testConstructor() {
        new StatisticsManager();
    }

    public void testAdd() {
        StatisticsManager statisticsManager = new StatisticsManager();
        statisticsManager.addStatisicsCounter("State Coverage", new VertexCoverageStatistics(graph));
        statisticsManager.addStatisicsCounter("Edge Coverage", new EdgeCoverageStatistics(graph));
        statisticsManager.addStatisicsCounter("2-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics(graph, 2));
        statisticsManager.addStatisicsCounter("3-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics(graph, 3));
        statisticsManager.addStatisicsCounter("Requirements Coverage", new RequirementCoverageStatistics(graph));
        assertEquals("[Requirements Coverage, Edge Coverage, State Coverage, 3-Edge Sequence Coverage, 2-Edge Sequence Coverage]",
                statisticsManager.getCounterNames().toString());
    }

    public void testProgress() {
        StatisticsManager statisticsManager = new StatisticsManager();
        statisticsManager.addStatisicsCounter("State Coverage", new VertexCoverageStatistics(graph));
        statisticsManager.addStatisicsCounter("Edge Coverage", new EdgeCoverageStatistics(graph));
        statisticsManager.addStatisicsCounter("2-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics(graph, 2));
        statisticsManager.addStatisicsCounter("3-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics(graph, 3));
        statisticsManager.addStatisicsCounter("Requirements Coverage", new RequirementCoverageStatistics(graph));
        statisticsManager.addProgress(start);
        assertEquals(
                true,
                statisticsManager
                        .getCurrentStatisticXml()
                        .matches(
                                "<\\?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"\\?>\\s+<Statistic><Data type=\"Edge Coverage\" value=\"0\" max=\"4\" /><Data type=\"State Coverage\" value=\"1\" max=\"3\" /><Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" /><Data type=\"2-Edge Sequence Coverage\" value=\"0\" max=\"6\" /><Data type=\"Requirements Coverage\" value=\"0\" max=\"4\" /></Statistic>\\s+"));
        statisticsManager.addProgress(e1);
        assertEquals(
                true,
                statisticsManager
                        .getCurrentStatisticXml()
                        .matches(
                                "<\\?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"\\?>\\s+<Statistic><Data type=\"Edge Coverage\" value=\"1\" max=\"4\" /><Data type=\"State Coverage\" value=\"1\" max=\"3\" /><Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" /><Data type=\"2-Edge Sequence Coverage\" value=\"0\" max=\"6\" /><Data type=\"Requirements Coverage\" value=\"2\" max=\"4\" /></Statistic>\\s+"));
        statisticsManager.addProgress(v1);
        assertEquals(
                true,
                statisticsManager
                        .getCurrentStatisticXml()
                        .matches(
                                "<\\?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"\\?>\\s+<Statistic><Data type=\"Edge Coverage\" value=\"1\" max=\"4\" /><Data type=\"State Coverage\" value=\"2\" max=\"3\" /><Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" /><Data type=\"2-Edge Sequence Coverage\" value=\"0\" max=\"6\" /><Data type=\"Requirements Coverage\" value=\"2\" max=\"4\" /></Statistic>\\s+"));
        statisticsManager.addProgress(e2);
        assertEquals(
                true,
                statisticsManager
                        .getCurrentStatisticXml()
                        .matches(
                                "<\\?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"\\?>\\s+<Statistic><Data type=\"Edge Coverage\" value=\"2\" max=\"4\" /><Data type=\"State Coverage\" value=\"2\" max=\"3\" /><Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" /><Data type=\"2-Edge Sequence Coverage\" value=\"1\" max=\"6\" /><Data type=\"Requirements Coverage\" value=\"3\" max=\"4\" /></Statistic>\\s+"));
        statisticsManager.addProgress(v2);
        assertEquals(
                true,
                statisticsManager
                        .getCurrentStatisticXml()
                        .matches(
                                "<\\?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"\\?>\\s+<Statistic><Data type=\"Edge Coverage\" value=\"2\" max=\"4\" /><Data type=\"State Coverage\" value=\"3\" max=\"3\" /><Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" /><Data type=\"2-Edge Sequence Coverage\" value=\"1\" max=\"6\" /><Data type=\"Requirements Coverage\" value=\"4\" max=\"4\" /></Statistic>\\s+"));
        statisticsManager.addProgress(e3);
        assertEquals(
                true,
                statisticsManager
                        .getCurrentStatisticXml()
                        .matches(
                                "<\\?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"\\?>\\s+<Statistic><Data type=\"Edge Coverage\" value=\"3\" max=\"4\" /><Data type=\"State Coverage\" value=\"3\" max=\"3\" /><Data type=\"3-Edge Sequence Coverage\" value=\"1\" max=\"10\" /><Data type=\"2-Edge Sequence Coverage\" value=\"2\" max=\"6\" /><Data type=\"Requirements Coverage\" value=\"4\" max=\"4\" /></Statistic>\\s+"));
        statisticsManager.addProgress(e4);
        assertEquals(
                true,
                statisticsManager
                        .getCurrentStatisticXml()
                        .matches(
                                "<\\?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"\\?>\\s+<Statistic><Data type=\"Edge Coverage\" value=\"4\" max=\"4\" /><Data type=\"State Coverage\" value=\"3\" max=\"3\" /><Data type=\"3-Edge Sequence Coverage\" value=\"2\" max=\"10\" /><Data type=\"2-Edge Sequence Coverage\" value=\"3\" max=\"6\" /><Data type=\"Requirements Coverage\" value=\"4\" max=\"4\" /></Statistic>\\s+"));
    }

    public void testFullProgress() {
        StatisticsManager statisticsManager = new StatisticsManager();
        statisticsManager.addStatisicsCounter("State Coverage", new VertexCoverageStatistics(graph));
        statisticsManager.addStatisicsCounter("Edge Coverage", new EdgeCoverageStatistics(graph));
        statisticsManager.addStatisicsCounter("2-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics(graph, 2));
        statisticsManager.addStatisicsCounter("3-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics(graph, 3));
        statisticsManager.addStatisicsCounter("Requirements Coverage", new RequirementCoverageStatistics(graph));
        statisticsManager.addProgress(start);
        statisticsManager.addProgress(e1);
        statisticsManager.addProgress(v1);
        statisticsManager.addProgress(e2);
        statisticsManager.addProgress(v2);
        statisticsManager.addProgress(e3);
        statisticsManager.addProgress(e4);
        assertEquals(
                true,
                statisticsManager
                        .getFullProgressXml()
                        .matches(
                                "<\\?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"\\?>\\s+<Statistics><Statistic><Data type=\"Edge Coverage\" value=\"0\" max=\"4\" /><Data type=\"State Coverage\" value=\"1\" max=\"3\" /><Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" /><Data type=\"2-Edge Sequence Coverage\" value=\"0\" max=\"6\" /><Data type=\"Requirements Coverage\" value=\"0\" max=\"4\" /></Statistic><Statistic><Data type=\"Edge Coverage\" value=\"1\" max=\"4\" /><Data type=\"State Coverage\" value=\"1\" max=\"3\" /><Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" /><Data type=\"2-Edge Sequence Coverage\" value=\"0\" max=\"6\" /><Data type=\"Requirements Coverage\" value=\"2\" max=\"4\" /></Statistic><Statistic><Data type=\"Edge Coverage\" value=\"1\" max=\"4\" /><Data type=\"State Coverage\" value=\"2\" max=\"3\" /><Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" /><Data type=\"2-Edge Sequence Coverage\" value=\"0\" max=\"6\" /><Data type=\"Requirements Coverage\" value=\"2\" max=\"4\" /></Statistic><Statistic><Data type=\"Edge Coverage\" value=\"2\" max=\"4\" /><Data type=\"State Coverage\" value=\"2\" max=\"3\" /><Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" /><Data type=\"2-Edge Sequence Coverage\" value=\"1\" max=\"6\" /><Data type=\"Requirements Coverage\" value=\"3\" max=\"4\" /></Statistic><Statistic><Data type=\"Edge Coverage\" value=\"2\" max=\"4\" /><Data type=\"State Coverage\" value=\"3\" max=\"3\" /><Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" /><Data type=\"2-Edge Sequence Coverage\" value=\"1\" max=\"6\" /><Data type=\"Requirements Coverage\" value=\"4\" max=\"4\" /></Statistic><Statistic><Data type=\"Edge Coverage\" value=\"3\" max=\"4\" /><Data type=\"State Coverage\" value=\"3\" max=\"3\" /><Data type=\"3-Edge Sequence Coverage\" value=\"1\" max=\"10\" /><Data type=\"2-Edge Sequence Coverage\" value=\"2\" max=\"6\" /><Data type=\"Requirements Coverage\" value=\"4\" max=\"4\" /></Statistic><Statistic><Data type=\"Edge Coverage\" value=\"4\" max=\"4\" /><Data type=\"State Coverage\" value=\"3\" max=\"3\" /><Data type=\"3-Edge Sequence Coverage\" value=\"2\" max=\"10\" /><Data type=\"2-Edge Sequence Coverage\" value=\"3\" max=\"6\" /><Data type=\"Requirements Coverage\" value=\"4\" max=\"4\" /></Statistic></Statistics>\\s+"));
    }

    public void testFullProgressReport() {
        StatisticsManager statisticsManager = new StatisticsManager();
        statisticsManager.addStatisicsCounter("State Coverage", new VertexCoverageStatistics(graph));
        statisticsManager.addStatisicsCounter("Edge Coverage", new EdgeCoverageStatistics(graph));
        statisticsManager.addStatisicsCounter("2-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics(graph, 2));
        statisticsManager.addStatisicsCounter("3-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics(graph, 3));
        statisticsManager.addStatisicsCounter("Requirements Coverage", new RequirementCoverageStatistics(graph));
        statisticsManager.addProgress(start);
        statisticsManager.addProgress(e1);
        statisticsManager.addProgress(v1);
        statisticsManager.addProgress(e2);
        statisticsManager.addProgress(v2);
        statisticsManager.addProgress(e3);
        statisticsManager.addProgress(e4);
        statisticsManager.setReportTemplate(getClass().getClassLoader().getResourceAsStream("templates/short.report"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        statisticsManager.writeFullReport(new PrintStream(out));
        assertEquals(
                "<table cellpadding=\"5px\">"
                        + Util.newline
                        + "<tr>"
                        + Util.newline
                        + "<td rowspan=\"4\"><img src=\"http://chart.apis.google.com/chart?"
                        + "chs=300x200&"
                        + "amp;chxt=x,y&amp;chxr=1,0,100|0,0,7&amp;chco=ff0000,00ff00,0000ff,000000,777777&amp;chd=t:0,0,25,25,50,50,75,100|0,33,33,67,67,100,100,100|0,0,0,0,17,17,33,50|0,0,0,0,0,0,10,20&amp;"
                        + "cht=lc\" alt=\"Coverage\"></td><td style=\"background-color: #f00\"> </td><td>Edge Coverage</td>" + Util.newline + "</tr>"
                        + Util.newline + "<tr>" + Util.newline + "<td style=\"background-color: #0f0\"> </td><td>State Coverage</td>" + Util.newline
                        + "</tr>" + Util.newline + "<tr>" + Util.newline
                        + "<td style=\"background-color: #00f\"> </td><td>2-Edge Sequence Coverage</td>" + Util.newline + "</tr>" + Util.newline
                        + "<tr>" + Util.newline + "<td style=\"background-color: #000\"> </td><td>3-Edge Sequence Coverage</td>" + Util.newline
                        + "</tr>" + Util.newline + "</table>" + Util.newline, out.toString());
    }

}
