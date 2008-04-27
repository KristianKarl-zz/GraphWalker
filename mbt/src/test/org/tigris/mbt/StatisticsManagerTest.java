/**
 * 
 */
package test.org.tigris.mbt;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.tigris.mbt.Keywords;
import org.tigris.mbt.StatisticsManager;
import org.tigris.mbt.Util;
import org.tigris.mbt.statistics.EdgeCoverageStatistics;
import org.tigris.mbt.statistics.EdgeSequenceCoverageStatistics;
import org.tigris.mbt.statistics.RequirementCoverageStatistics;
import org.tigris.mbt.statistics.StateCoverageStatistics;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.utils.UserData;
import junit.framework.TestCase;

/**
 * @author Johan Tejle
 *
 */
public class StatisticsManagerTest extends TestCase {

	SparseGraph graph;
	DirectedSparseVertex start;
	DirectedSparseVertex v1;
	DirectedSparseVertex v2;
	DirectedSparseEdge e1;
	DirectedSparseEdge e2;
	DirectedSparseEdge e3;
	DirectedSparseEdge e4;
	
	protected void setUp() throws Exception {
		super.setUp();
		graph = new SparseGraph();
		
		start = Util.addVertexToGraph(graph, "Start");
		
		v1 = Util.addVertexToGraph(graph, "V1");
		v1.setUserDatum(Keywords.REQTAG_KEY, "REQ002", UserData.SHARED);

		v2 = Util.addVertexToGraph(graph, "V2");
		v2.setUserDatum(Keywords.REQTAG_KEY, "REQ004", UserData.SHARED);

		e1 = Util.addEdgeToGraph(graph, start, v1, "E1", null, null, "x=1;y=new Vector()");
		e1.setUserDatum(Keywords.REQTAG_KEY, "REQ001,REQ002", UserData.SHARED);
		
		e2 = Util.addEdgeToGraph(graph, v1, v2, "E2", null, null, "x=2");
		e2.setUserDatum(Keywords.REQTAG_KEY, "REQ003", UserData.SHARED);

		e3 = Util.addEdgeToGraph(graph, v2, v2, "E3", null, "x<6", "x++");

		e4 = Util.addEdgeToGraph(graph, v2, v1, "E4", null, "y.size()<3", "y.add(x)");
	}

	public void testConstructor() 
	{
		new StatisticsManager();
	}

	public void testAdd() 
	{
		StatisticsManager statisticsManager = new StatisticsManager();
		statisticsManager.addStatisicsCounter("State Coverage", new StateCoverageStatistics( graph ));
		statisticsManager.addStatisicsCounter("Edge Coverage", new EdgeCoverageStatistics( graph ));
		statisticsManager.addStatisicsCounter("2-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics( graph, 2 ));
		statisticsManager.addStatisicsCounter("3-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics( graph, 3 ));
		statisticsManager.addStatisicsCounter("Requirements Coverage", new RequirementCoverageStatistics( graph ));
		assertEquals("[Requirements Coverage, Edge Coverage, State Coverage, 3-Edge Sequence Coverage, 2-Edge Sequence Coverage]", statisticsManager.getCounterNames().toString());
	}

	public void testProgress() 
	{
		StatisticsManager statisticsManager = new StatisticsManager();
		statisticsManager.addStatisicsCounter("State Coverage", new StateCoverageStatistics( graph ));
		statisticsManager.addStatisicsCounter("Edge Coverage", new EdgeCoverageStatistics( graph ));
		statisticsManager.addStatisicsCounter("2-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics( graph, 2 ));
		statisticsManager.addStatisicsCounter("3-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics( graph, 3 ));
		statisticsManager.addStatisicsCounter("Requirements Coverage", new RequirementCoverageStatistics( graph ));
		statisticsManager.addProgress(start);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + Util.newline +
				"<Statistic>" +
				"<Data type=\"Edge Coverage\" value=\"0\" max=\"4\" />"+
				"<Data type=\"State Coverage\" value=\"1\" max=\"3\" />" +
				"<Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" />" +
				"<Data type=\"2-Edge Sequence Coverage\" value=\"0\" max=\"6\" />" +
				"<Data type=\"Requirements Coverage\" value=\"0\" max=\"4\" />" +
				"</Statistic>" + Util.newline, statisticsManager.getCurrentStatisticXml());
		statisticsManager.addProgress(e1);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + Util.newline +
				"<Statistic>" +
				"<Data type=\"Edge Coverage\" value=\"1\" max=\"4\" />"+
				"<Data type=\"State Coverage\" value=\"1\" max=\"3\" />" +
				"<Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" />" +
				"<Data type=\"2-Edge Sequence Coverage\" value=\"0\" max=\"6\" />" +
				"<Data type=\"Requirements Coverage\" value=\"2\" max=\"4\" />" +
				"</Statistic>" + Util.newline, statisticsManager.getCurrentStatisticXml());
		statisticsManager.addProgress(v1);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + Util.newline +
				"<Statistic>" +
				"<Data type=\"Edge Coverage\" value=\"1\" max=\"4\" />"+
				"<Data type=\"State Coverage\" value=\"2\" max=\"3\" />" +
				"<Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" />" +
				"<Data type=\"2-Edge Sequence Coverage\" value=\"0\" max=\"6\" />" +
				"<Data type=\"Requirements Coverage\" value=\"2\" max=\"4\" />" +
				"</Statistic>" + Util.newline, statisticsManager.getCurrentStatisticXml());
		statisticsManager.addProgress(e2);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + Util.newline +
				"<Statistic>" +
				"<Data type=\"Edge Coverage\" value=\"2\" max=\"4\" />"+
				"<Data type=\"State Coverage\" value=\"2\" max=\"3\" />" +
				"<Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" />" +
				"<Data type=\"2-Edge Sequence Coverage\" value=\"1\" max=\"6\" />" +
				"<Data type=\"Requirements Coverage\" value=\"3\" max=\"4\" />" +
				"</Statistic>" + Util.newline, statisticsManager.getCurrentStatisticXml());
		statisticsManager.addProgress(v2);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + Util.newline +
				"<Statistic>" +
				"<Data type=\"Edge Coverage\" value=\"2\" max=\"4\" />"+
				"<Data type=\"State Coverage\" value=\"3\" max=\"3\" />" +
				"<Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" />" +
				"<Data type=\"2-Edge Sequence Coverage\" value=\"1\" max=\"6\" />" +
				"<Data type=\"Requirements Coverage\" value=\"4\" max=\"4\" />" +
				"</Statistic>" + Util.newline, statisticsManager.getCurrentStatisticXml());
		statisticsManager.addProgress(e3);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + Util.newline +
				"<Statistic>" +
				"<Data type=\"Edge Coverage\" value=\"3\" max=\"4\" />"+
				"<Data type=\"State Coverage\" value=\"3\" max=\"3\" />" +
				"<Data type=\"3-Edge Sequence Coverage\" value=\"1\" max=\"10\" />" +
				"<Data type=\"2-Edge Sequence Coverage\" value=\"2\" max=\"6\" />" +
				"<Data type=\"Requirements Coverage\" value=\"4\" max=\"4\" />" +
				"</Statistic>" + Util.newline, statisticsManager.getCurrentStatisticXml());
		statisticsManager.addProgress(e4);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + Util.newline +
				"<Statistic>" +
				"<Data type=\"Edge Coverage\" value=\"4\" max=\"4\" />"+
				"<Data type=\"State Coverage\" value=\"3\" max=\"3\" />" +
				"<Data type=\"3-Edge Sequence Coverage\" value=\"2\" max=\"10\" />" +
				"<Data type=\"2-Edge Sequence Coverage\" value=\"3\" max=\"6\" />" +
				"<Data type=\"Requirements Coverage\" value=\"4\" max=\"4\" />" +
				"</Statistic>" + Util.newline, statisticsManager.getCurrentStatisticXml());
	}

	public void testFullProgress() 
	{
		StatisticsManager statisticsManager = new StatisticsManager();
		statisticsManager.addStatisicsCounter("State Coverage", new StateCoverageStatistics( graph ));
		statisticsManager.addStatisicsCounter("Edge Coverage", new EdgeCoverageStatistics( graph ));
		statisticsManager.addStatisicsCounter("2-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics( graph, 2 ));
		statisticsManager.addStatisicsCounter("3-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics( graph, 3 ));
		statisticsManager.addStatisicsCounter("Requirements Coverage", new RequirementCoverageStatistics( graph ));
		statisticsManager.addProgress(start);
		statisticsManager.addProgress(e1);
		statisticsManager.addProgress(v1);
		statisticsManager.addProgress(e2);
		statisticsManager.addProgress(v2);
		statisticsManager.addProgress(e3);
		statisticsManager.addProgress(e4);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + Util.newline +
				"<Statistics>" +
				"<Statistic>" +
				"<Data type=\"Edge Coverage\" value=\"0\" max=\"4\" />"+
				"<Data type=\"State Coverage\" value=\"1\" max=\"3\" />" +
				"<Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" />" +
				"<Data type=\"2-Edge Sequence Coverage\" value=\"0\" max=\"6\" />" +
				"<Data type=\"Requirements Coverage\" value=\"0\" max=\"4\" />" +
				"</Statistic>" + 
				"<Statistic>" +
				"<Data type=\"Edge Coverage\" value=\"1\" max=\"4\" />"+
				"<Data type=\"State Coverage\" value=\"1\" max=\"3\" />" +
				"<Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" />" +
				"<Data type=\"2-Edge Sequence Coverage\" value=\"0\" max=\"6\" />" +
				"<Data type=\"Requirements Coverage\" value=\"2\" max=\"4\" />" +
				"</Statistic>" + 
				"<Statistic>" +
				"<Data type=\"Edge Coverage\" value=\"1\" max=\"4\" />"+
				"<Data type=\"State Coverage\" value=\"2\" max=\"3\" />" +
				"<Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" />" +
				"<Data type=\"2-Edge Sequence Coverage\" value=\"0\" max=\"6\" />" +
				"<Data type=\"Requirements Coverage\" value=\"2\" max=\"4\" />" +
				"</Statistic>" + 
				"<Statistic>" +
				"<Data type=\"Edge Coverage\" value=\"2\" max=\"4\" />"+
				"<Data type=\"State Coverage\" value=\"2\" max=\"3\" />" +
				"<Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" />" +
				"<Data type=\"2-Edge Sequence Coverage\" value=\"1\" max=\"6\" />" +
				"<Data type=\"Requirements Coverage\" value=\"3\" max=\"4\" />" +
				"</Statistic>" + 
				"<Statistic>" +
				"<Data type=\"Edge Coverage\" value=\"2\" max=\"4\" />"+
				"<Data type=\"State Coverage\" value=\"3\" max=\"3\" />" +
				"<Data type=\"3-Edge Sequence Coverage\" value=\"0\" max=\"10\" />" +
				"<Data type=\"2-Edge Sequence Coverage\" value=\"1\" max=\"6\" />" +
				"<Data type=\"Requirements Coverage\" value=\"4\" max=\"4\" />" +
				"</Statistic>" + 
				"<Statistic>" +
				"<Data type=\"Edge Coverage\" value=\"3\" max=\"4\" />"+
				"<Data type=\"State Coverage\" value=\"3\" max=\"3\" />" +
				"<Data type=\"3-Edge Sequence Coverage\" value=\"1\" max=\"10\" />" +
				"<Data type=\"2-Edge Sequence Coverage\" value=\"2\" max=\"6\" />" +
				"<Data type=\"Requirements Coverage\" value=\"4\" max=\"4\" />" +
				"</Statistic>" + 
				"<Statistic>" +
				"<Data type=\"Edge Coverage\" value=\"4\" max=\"4\" />"+
				"<Data type=\"State Coverage\" value=\"3\" max=\"3\" />" +
				"<Data type=\"3-Edge Sequence Coverage\" value=\"2\" max=\"10\" />" +
				"<Data type=\"2-Edge Sequence Coverage\" value=\"3\" max=\"6\" />" +
				"<Data type=\"Requirements Coverage\" value=\"4\" max=\"4\" />" +
				"</Statistic>" + 
				"</Statistics>" + Util.newline, statisticsManager.getFullProgressXml());
	}
	public void testFullProgressReport() 
	{
		StatisticsManager statisticsManager = new StatisticsManager();
		statisticsManager.addStatisicsCounter("State Coverage", new StateCoverageStatistics( graph ));
		statisticsManager.addStatisicsCounter("Edge Coverage", new EdgeCoverageStatistics( graph ));
		statisticsManager.addStatisicsCounter("2-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics( graph, 2 ));
		statisticsManager.addStatisicsCounter("3-Edge Sequence Coverage", new EdgeSequenceCoverageStatistics( graph, 3 ));
		statisticsManager.addStatisicsCounter("Requirements Coverage", new RequirementCoverageStatistics( graph ));
		statisticsManager.addProgress(start);
		statisticsManager.addProgress(e1);
		statisticsManager.addProgress(v1);
		statisticsManager.addProgress(e2);
		statisticsManager.addProgress(v2);
		statisticsManager.addProgress(e3);
		statisticsManager.addProgress(e4);
		statisticsManager.setReportTemplate("templates/short.report");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		statisticsManager.writeFullReport(new PrintStream(out));
		assertEquals("<table cellpadding=\"5px\">" + Util.newline +
				"<tr>" + Util.newline +
				"<td rowspan=\"4\"><img src=\"http://chart.apis.google.com/chart?" +
				"chs=300x200&" +
				"chxt=x,y&" +
				"chxr=1,0,100|0,0,7&" +
				"chco=ff0000,00ff00,0000ff,000000,777777&" +
				"chd=t:0,0,25,25,50,50,75,100|0,33,33,67,67,100,100,100|0,0,0,0,17,17,33,50|0,0,0,0,0,0,10,20&" +
				"cht=lc\" alt=\"Coverage\"></td><td style=\"background-color: #f00\"> </td><td>Edge Coverage</td>" + Util.newline +
				"</tr>" + Util.newline +
				"<tr>" + Util.newline +
				"<td style=\"background-color: #0f0\"> </td><td>State Coverage</td>" + Util.newline +
				"</tr>" + Util.newline +
				"<tr>" + Util.newline +
				"<td style=\"background-color: #00f\"> </td><td>2-Edge Sequence Coverage</td>" + Util.newline +
				"</tr>" + Util.newline +
				"<tr>" + Util.newline +
				"<td style=\"background-color: #000\"> </td><td>3-Edge Sequence Coverage</td>" + Util.newline +
				"</tr>" + Util.newline +
				"</table>"+ Util.newline, out.toString());
	}
	
}
