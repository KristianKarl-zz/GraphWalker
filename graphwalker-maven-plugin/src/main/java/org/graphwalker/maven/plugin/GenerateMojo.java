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
package org.graphwalker.maven.plugin;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * <p>GenerateMojo class.</p>
 *
 * @author nilols
 */
@Mojo(name = "generate"
        , defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES
        , requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.PROCESS_CLASSES)
public class GenerateMojo extends AbstractGraphWalkerMojo {
    // Tänkt att användas för att generera klasser (enklast kanske att bara generera/uppdatera interface)
    // utifrån modeller, och alt. uppdatera befintliga klasser med ny metoder
    //

    @Override
    public void executeMojo() {
        int i = 0;
        // hitta alla modeller som stödjs, leta i både source och test, ta hänsyn till include/exclude
        // kolla om filer för dessa finns, package namn blir samma som katalog strukturen
        // finns det inga interface, generera nya
        // finna det, om vi ska uppdatera så genererar vi om filerna.

    }
}
