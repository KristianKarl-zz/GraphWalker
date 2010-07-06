package org.graphwalker.io;

import java.io.PrintStream;
import java.util.Vector;

public class PrintHTMLTestSequence {

	public PrintHTMLTestSequence(Vector<String[]> testSequence, PrintStream out) {
		writeHTML(testSequence, out);
	}

	private void writeHTML(Vector<String[]> testSequence, PrintStream out) {

		header(out);

		int index = 1;
		for (String[] string : testSequence) {
			
			out.println("<p>");
			out.println("<table class=\"example\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			out.println("<tbody><tr><td>");
			out.println("<h2 class=\"example\">" + string[0] + "</h2>");
			out.println("<table style=\"background-color: white;\" width=\"100%\" border=\"1\" cellpadding=\"2\" cellspacing=\"0\">");

			out.println("<tbody><tr>");
			out.println("<col width=10%>");
			out.println("<col width=45%>");
			out.println("<col width=45%>");
			out.println("<tr>");
			out.println("<td class=\"indexRow\"><b>Step</b></td>");
			out.println("<td class=\"indexRow\"><b>Action</b></td>");
			out.println("<td class=\"indexRow\"><b>Expected Result</b></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td class=\"tcRow\">" + index++ + "</td>");
			out.println("<td class=\"tcRow\">" + string[1] + "</td>");
			out.println("<td class=\"tcRow\">" + string[2] + "</td>");
			out.println("</tr>");
			out.println("</tbody></table>");
			out.println("</td></tr></tbody></table>");
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
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.w3schools.com/stdtheme.css\" />");
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
