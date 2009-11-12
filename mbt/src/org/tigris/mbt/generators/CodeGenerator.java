package org.tigris.mbt.generators;

/**
 * Will generate code using a template. The code generated will contain all
 * lables/names defined by the vertices and edges. This enables the user to
 * write templates for a multitude of scripting or programming languages.<br>
 * <br>
 * There is 2 variables in the template that will be replaced as follows:<br>
 * <strong>{LABEL}</strong> Will be replace by the actual name of the edge or
 * vertex.<br>
 * <strong>{EDGE_VERTEX}</strong> Will be replace by the word 'Edge' or
 * 'Vertex'.<br>
 * <br>
 * <strong>Below is an example of a template.</strong>
 * 
 * <pre>
 * /**
 * * This method implements the {EDGE_VERTEX} '{LABEL}'
 * * /
 * public void {LABEL}()
 * {
 *    log.info( "{EDGE_VERTEX}: {LABEL}" );
 *    throw new RuntimeException( "Not implemented" );
 * }
 * </pre>
 */
public class CodeGenerator extends ListGenerator {

	String[] template; // {HEADER, BODY, FOOTER}
	private boolean first = true;

	public CodeGenerator() {
		super();
	}

	public CodeGenerator(String[] template) {
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
