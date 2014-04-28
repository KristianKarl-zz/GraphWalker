/*
 * #%L
 * GraphWalker Maven Plugin
 * %%
 * Copyright (C) 2011 - 2014 GraphWalker
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

import org.graphwalker.core.Model;
import org.graphwalker.core.SimpleModel;
import org.graphwalker.core.common.Assert;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Guard;
import org.graphwalker.core.model.Vertex;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * @author Nils Olsson
 */
public class CodeGeneratorTest {

    @Test
    public void unnamedElement() {
        Model model = new SimpleModel().addEdge(new Edge(null, new Vertex("Start"), new Vertex("Verify"), new Guard("isConnected()")));
        SourceFile sourceFile = new SourceFile(Paths.get("/example/test.graphml"), Paths.get("/"), Paths.get("/"));
        CodeGenerator codeGenerator = new CodeGenerator(sourceFile, model);
        String output = codeGenerator.generate();
        Assert.assertFalse(output.contains("Start"));
        Assert.assertFalse(output.contains("isConnected()"));
        Assert.assertTrue(output.contains("Verify"));
    }

    @Test
    public void unnamedElements() {
        Model model = new SimpleModel().addEdge(new Edge(null, new Vertex("Start"), new Vertex("Verify"), new Guard("isConnected()")))
                .addEdge(new Edge(null, new Vertex("Verify"), new Vertex(null), new Guard("isCompleted()")));
        SourceFile sourceFile = new SourceFile(Paths.get("/example/test.graphml"), Paths.get("/"), Paths.get("/"));
        CodeGenerator codeGenerator = new CodeGenerator(sourceFile, model);
        String output = codeGenerator.generate();
        Assert.assertFalse(output.contains("Start"));
        Assert.assertFalse(output.contains("isConnected()"));
        Assert.assertTrue(output.contains("Verify"));
    }

    @Test
    public void emptyElementName() {
        Model model = new SimpleModel().addEdge(new Edge("", new Vertex("Start"), new Vertex("Verify"), new Guard("isConnected()")));
        SourceFile sourceFile = new SourceFile(Paths.get("/example/test.graphml"), Paths.get("/"), Paths.get("/"));
        CodeGenerator codeGenerator = new CodeGenerator(sourceFile, model);
        String output = codeGenerator.generate();
        Assert.assertFalse(output.contains("Start"));
        Assert.assertFalse(output.contains("isConnected()"));
        Assert.assertTrue(output.contains("Verify"));
    }

    @Test
    public void emptyElementNames() {
        Model model = new SimpleModel().addEdge(new Edge("", new Vertex("Start"), new Vertex("Verify"), new Guard("isConnected()")))
                .addEdge(new Edge("", new Vertex("Verify"), new Vertex(""), new Guard("isCompleted()")));
        SourceFile sourceFile = new SourceFile(Paths.get("/example/test.graphml"), Paths.get("/"), Paths.get("/"));
        CodeGenerator codeGenerator = new CodeGenerator(sourceFile, model);
        String output = codeGenerator.generate();
        Assert.assertFalse(output.contains("Start"));
        Assert.assertFalse(output.contains("isConnected()"));
        Assert.assertTrue(output.contains("Verify"));
    }
}
