package org.graphwalker.webrenderer.geometry;

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

import java.util.List;
import java.util.UUID;

/**
 * @author Nils Olsson
 */
public class Edge {

    private UUID id;
    private Label label;
    private Vertex source;
    private Vertex target;
    private Point pathSource;
    private Point pathTarget;
    private List<Point> pathPoints;

    public Edge(UUID id, Label label, Vertex source, Vertex target, Point pathSource, Point pathTarget, List<Point> pathPoints) {
        this.id = id;
        this.label = label;
        this.source = source;
        this.target = target;
        this.pathSource = pathSource;
        this.pathTarget = pathTarget;
        this.pathPoints = pathPoints;
    }

    public UUID getId() {
        return id;
    }

    public Label getLabel() {
        return label;
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getTarget() {
        return target;
    }

    public Point getPathSource() {
        return pathSource;
    }

    public Point getPathTarget() {
        return pathTarget;
    }

    public List<Point> getPathPoints() {
        return pathPoints;
    }
}
