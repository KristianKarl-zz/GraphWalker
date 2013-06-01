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
 * <p>ValidateMojo class.</p>
 *
 * @author nilols
 */
@Mojo(name = "validate"
        , defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES
        , requiresDependencyResolution = ResolutionScope.TEST)
@Execute(goal = "generate")
public class ValidateMojo extends AbstractGraphWalkerMojo {

    // Tänkt att änvändas automatiskt innan TestMojo'n exekveras för att validera modeller och implementationer innan de körs
    // eftersom det kan dröja länge innan man märker ett fel i runtime (like dry run)

    // 1. Hitta alla tester (ska vi bry oss om att filtrera bort tester)
    // 2. Ladda in modeller och implementationer
    // 3. verifiera att implementationerna innehåller alla metoder och rätt argument
    // 4. spara undan allt i AbstractGraphWalkerMojo så att TestMojo kan använda en validerad version utan att behöva göra om allt

    @Override
    public void executeMojo() {
        int i = 0;
    }
}