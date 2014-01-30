package org.graphwalker.webrenderer;

/*
 * #%L
 * GraphWalker Web Renderer
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

import org.graphwalker.core.*;
import org.graphwalker.core.condition.VertexCoverage;
import org.graphwalker.core.generator.RandomPath;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Vertex;
import org.junit.Test;
import org.webbitserver.handler.StaticFileHandler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * @author Nils Olsson
 */
public class WebRendererTest {

    @Test
    public void simpleTest() throws Exception {
        //WebRenderer renderer = new WebRenderer("", 8080);
        //renderer.add(new StaticFileHandler("src/main/resources"));
        //renderer.start();
        //renderer.stop();
    }


    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException, URISyntaxException {

        Model model = new SimpleModel().addEdge(new Edge("e1", new Vertex("v1"), new Vertex("v2")));
        PathGenerator pathGenerator = new RandomPath(new VertexCoverage());
        ExecutionContext context = new ExecutionContext(model, pathGenerator);
        Machine machine = new SimpleMachine(Arrays.asList(context, context));

        WebRenderer renderer = new WebRenderer(machine, 8080);
        renderer.add(new StaticFileHandler("src/main/resources"));
        renderer.start();

        //while (machine.hasNextStep()) {
        //    machine.getNextStep();
        //}

        //renderer.stop();
    }
}
