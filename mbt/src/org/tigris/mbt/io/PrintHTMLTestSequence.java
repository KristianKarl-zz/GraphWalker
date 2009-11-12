package org.tigris.mbt.io;

import java.io.PrintStream;
import java.util.Vector;

import edu.uci.ics.jung.graph.util.Pair;

public class PrintHTMLTestSequence {

	public PrintHTMLTestSequence(Vector<Pair<String>> testSequence, PrintStream out) {
		writeHTML(testSequence, out);
	}

	private void writeHTML(Vector<Pair<String>> testSequence, PrintStream out) {

		header(out);

		int index = 1;
		for (Pair<String> pair : testSequence) {
			out.println("<p>");
			out.println("<div align=\"center\"><table class=\"myTable\" cellpadding=\"3\" cellspacing=\"0\"><tbody>");
			out.println("<col width=10%>");
			out.println("<col width=45%>");
			out.println("<col width=45%>");
			out.println("<tr>");
			out.println("<td class=\"indexRow\"><b>Step " + index++ + "</b></td>");
			out.println("<td class=\"indexRow\"><b>Action</b></td>");
			out.println("<td class=\"indexRow\"><b>Expected Result</b></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td class=\"tcRow\"> </td>");
			out.println("<td class=\"tcRow\">" + pair.getFirst() + "</td>");
			out.println("<td class=\"tcRow\">" + pair.getSecond() + "</td>");
			out.println("</tr></tbody></table></div>");
			out.println("</p>");
		}


		footer(out);
	}
	
	private void header(PrintStream out) {
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">");
		out.println("<title>Test Sequence made by MBT</title>");
		out.println("</head>");
		out.println("<body>");

		out.println("<style>");
		out.println(".myTable   { border:1px solid #369; width:90%; }");
		out.println(".indexRow  { background-color: #9ab; padding:2px; }");
		out.println(".tcRow  { background-color: #ddf; padding:2px; }");
		out.println("</style>");
	}

	private void footer(PrintStream out) {
		out.println("</body>");
		out.println("</html>");
	}
}
