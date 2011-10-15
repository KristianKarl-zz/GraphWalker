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
//This file is part of the GraphWalker java package
//The MIT License
//
//Copyright (c) 2010 graphwalker.org
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

package org.graphwalker.generators;

import org.graphwalker.conditions.StopCondition;

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

	public CodeGenerator(StopCondition stopCondition) {
		super(stopCondition);
	}

	public CodeGenerator() {
		super();
	}

	private String[] template; // {HEADER, BODY, FOOTER}
	private boolean first = true;

	public void setTemplate(String[] template) {
		this.template = template.clone();
	}

	@Override
	public String[] getNext() {
		String[] retur = super.getNext();
		if (retur[0].isEmpty()) {
			retur[1] = "";
			return retur;
		}
		retur[0] = (first && template[0].length() > 0 ? template[0] + "\n" : "")
		    + // HEADER
		    template[1]
		        // BODY
		        .replaceAll("\\{LABEL\\}", retur[0]).replaceAll("\\{EDGE_VERTEX\\}", retur[1])
		        .replaceAll("\\{DESCRIPTION\\}", retur[2].replaceAll("\n", "\n   * "))
		    + (!hasNext() && template[2].length() > 0 ? "\n" + template[2] : ""); // FOOTER
		retur[1] = "";
		first = false;
		return retur;
	}

	@Override
	public String toString() {
		return "CODE";
	}
}
