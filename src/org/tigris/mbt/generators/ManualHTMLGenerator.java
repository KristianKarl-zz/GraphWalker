package org.tigris.mbt.generators;

/**
 * Will generate HTML fromatted test case sequence
 */
public class ManualHTMLGenerator extends ListGenerator {

	String[] template; // {HEADER, BODY, FOOTER}
	private boolean first = true;

	public ManualHTMLGenerator() {
		super();
	}

	public ManualHTMLGenerator(String[] template) {
		setTemplate(template);
	}

	public void setTemplate(String[] template) {
		this.template = template;
	}

	public String[] getNext() {
		String[] retur = super.getNext();
		retur[0] = (first && template[0].length() > 0 ? template[0] + "\n" : "")
		    + // HEADER
		    template[1] // BODY
		        .replaceAll("\\{LABEL\\}", retur[0]).replaceAll("\\{EDGE_VERTEX\\}", retur[1])
		    + (!hasNext() && template[2].length() > 0 ? "\n" + template[2] : ""); // FOOTER
		retur[1] = "";
		first = false;
		return retur;
	}

	public String toString() {
		return "CODE";
	}
}
