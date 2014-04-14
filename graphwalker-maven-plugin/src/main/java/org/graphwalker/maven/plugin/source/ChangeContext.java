/*
 * #%L
 * GraphWalker Maven Plugin
 * %%
 * Copyright (C) 2011 - 2013 GraphWalker
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
package org.graphwalker.maven.plugin.source;

import japa.parser.ast.body.MethodDeclaration;
import org.graphwalker.core.Model;
import org.graphwalker.core.model.Element;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public final class ChangeContext {

    private final Set<String> methodNames;
    private final Set<MethodDeclaration> methodDeclarations = new HashSet<MethodDeclaration>();

    /**
     * <p>Constructor for ChangeContext.</p>
     *
     * @param model a {@link org.graphwalker.core.Model} object.
     */
    public ChangeContext(Model model) {
        methodNames = extractMethodNames(model);
    }

    /**
     * <p>getMethodsName.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<String> getMethodsName() {
        return methodNames;
    }

    /**
     * <p>addMethodDeclaration.</p>
     *
     * @param methodDeclaration a {@link japa.parser.ast.body.MethodDeclaration} object.
     */
    public void addMethodDeclaration(MethodDeclaration methodDeclaration) {
        methodDeclarations.add(methodDeclaration);
    }

    /**
     * <p>Getter for the field <code>methodDeclarations</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<MethodDeclaration> getMethodDeclarations() {
        return methodDeclarations;
    }

    private Set<String> extractMethodNames(Model model) {
        Set<String> methodNames = new HashSet<String>();
        for (Element element: model.getElements()) {
            if (null != element.getName() && !"Start".equalsIgnoreCase(element.getName())) {
                methodNames.add(element.getName());
            }
        }
        return methodNames;
    }
}
