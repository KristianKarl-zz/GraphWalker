package org.tigris.mbt.generators;

import org.tigris.mbt.FiniteStateMachine;

public class CodeGenerator extends ListGenerator {

	String template;
	
	public CodeGenerator(FiniteStateMachine machine, String template) {
		super(machine);
		this.template = template;
	}
	
	public String[] getNext() {
		String[] retur = super.getNext();
		retur[0] = template
			.replaceAll( "\\{LABEL\\}", retur[0])
			.replaceAll( "\\{EDGE_VERTEX\\}",retur[1]);
		return retur;
	}
}
