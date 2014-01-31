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

import org.graphstream.algorithm.Prim;
import org.graphstream.algorithm.SpanningTree;
import org.graphstream.algorithm.util.FibonacciHeap;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.stream.PipeBase;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.layout.Layout;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * @author Nils Olsson
 */
public class HierarchicalLayout extends PipeBase implements Layout {

    public static enum Rendering {
        VERTICAL, HORIZONTAL, DISK
    }

    private final LinkedList<String> roots;
    private final Graph internalGraph;
    private boolean structureChanged;
    private Rendering renderingType;
    private Point3 hi, lo;
    private long lastStep;
    private int nodeMoved;

    public HierarchicalLayout() {
        roots = new LinkedList<>();
        internalGraph = new AdjacencyListGraph("hierarchical_layout-intern");
        hi = new Point3();
        lo = new Point3();
        renderingType = Rendering.VERTICAL;
    }

    public Graph getGraph() {
        return internalGraph;
    }

    public void clear() {
    }

    public void compute() {
        nodeMoved = 0;

        if (structureChanged) {
            structureChanged = false;
            computePositions();
        }

        publishPositions();
        lastStep = System.currentTimeMillis();
    }

    protected void computePositions() {
        final int[] levels = new int[internalGraph.getNodeCount()];
        Arrays.fill(levels, -1);

        LinkedList<Node> roots = new LinkedList<>(), roots2 = new LinkedList<>();

        if (this.roots.size() > 0) {
            for (String root : this.roots) roots.add(internalGraph.getNode(root));
        }

        SpanningTree tree = new Prim("weight", "inTree");
        tree.init(internalGraph);
        tree.compute();

        if (roots.size() == 0) {
            int max = internalGraph.getNode(0).getDegree();
            int maxIndex = 0;

            for (int i = 1; i < internalGraph.getNodeCount(); i++)
                if (internalGraph.getNode(i).getDegree() > max) {
                    max = internalGraph.getNode(i).getDegree();
                    maxIndex = i;
                }

            roots.add(internalGraph.getNode(maxIndex));
        }

        Box rootBox = new Box();
        LevelBox rootLevelBox = new LevelBox(0);
        LinkedList<LevelBox> levelBoxes = new LinkedList<>();

        rootLevelBox.add(rootBox);
        levelBoxes.add(rootLevelBox);

        for (Node n : roots) {
            levels[n.getIndex()] = 0;
            setBox(rootBox, n);
        }

        do {
            while (roots.size() > 0) {
                Node root = roots.poll();
                int level = levels[root.getIndex()] + 1;
                Box box = getChildrenBox(root);

                for (Edge e : root.getEdgeSet()) {
                    if (e.getAttribute(tree.getFlagAttribute()).equals(
                            tree.getFlagOn())) {
                        Node op = e.getOpposite(root);

                        if (levels[op.getIndex()] < 0
                                || level < levels[op.getIndex()]) {
                            levels[op.getIndex()] = level;
                            roots2.add(op);
                            op.setAttribute("parent", root);
                            setBox(box, op);
                        }
                    }
                }
            }

            roots.addAll(roots2);
            roots2.clear();
        } while (roots.size() > 0);

        FibonacciHeap<Integer, Box> boxes = new FibonacciHeap<>();
        boxes.add(0, rootBox);

        for (int i = 0; i < internalGraph.getNodeCount(); i++) {
            Box box = getChildrenBox(internalGraph.getNode(i));

            if (box != null) {
                boxes.add(box.level, box);

                while (levelBoxes.size() <= box.level)
                    levelBoxes.add(new LevelBox(levelBoxes.size()));

                levelBoxes.get(box.level).add(box);
            }
        }

        for (LevelBox levelBox : levelBoxes) levelBox.sort();

        while (boxes.size() > 0)
            renderBox(boxes.extractMin());

        hi.x = hi.y = Double.MIN_VALUE;
        lo.x = lo.y = Double.MAX_VALUE;

        for (int idx = 0; idx < internalGraph.getNodeCount(); idx++) {
            Node n = internalGraph.getNode(idx);
            double y = n.getNumber("y");
            double x = n.getNumber("x");

            if (!n.hasNumber("oldX") || n.getNumber("oldX") != x
                    || !n.hasNumber("oldY") || n.getNumber("oldY") != y) {
                n.setAttribute("oldX", x);
                n.setAttribute("oldY", y);
                n.addAttribute("changed");
                nodeMoved++;
            }

            hi.x = Math.max(hi.x, x);
            hi.y = Math.max(hi.y, y);
            lo.x = Math.min(lo.x, x);
            lo.y = Math.min(lo.y, y);
        }
    }

    protected void setBox(Box box, Node node) {
        if (node.hasAttribute("box"))
            getBox(node).remove(node);

        box.add(node);
        node.setAttribute("box", box);

        if (!node.hasAttribute("children"))
            node.addAttribute("children", new Box(node, 1));

        getChildrenBox(node).level = box.level + 1;
    }

    protected static Box getBox(Node node) {
        return node.getAttribute("box");
    }

    protected static Box getChildrenBox(Node node) {
        return node.getAttribute("children");
    }

    protected void renderBox(Box box) {
        if (box.size() == 0)
            return;

        for (int i = 0; i < box.size(); i++) {
            Node n = box.get(i);

            switch (renderingType) {
                case VERTICAL:
                    n.setAttribute("x", box.width * i / (double) box.size());
                    n.setAttribute("y", box.height / 2);
                    break;
                case DISK:
                case HORIZONTAL:
                    n.setAttribute("x", box.width / 2);
                    n.setAttribute("y", box.height * i / (double) box.size());
                    break;
            }
        }

        double sx = 1, sy = 1;
        double dx = 0, dy = 0;

        if (box.parent != null) {
            Box parentBox = getBox(box.parent);

            switch (renderingType) {
                case VERTICAL:
                    sx = 1 / (double) parentBox.size();
                    sy = 1 / Math.pow(2, box.level);
                    break;
                case DISK:
                case HORIZONTAL:
                    sx = 1 / Math.pow(2, box.level);
                    sy = 1 / (double) parentBox.size();
                    break;
            }
        }

        box.scale(sx, sy);

        if (box.parent != null) {
            Box parentBox = getBox(box.parent);

            dx = box.parent.getNumber("x");
            dy = box.parent.getNumber("y");

            switch (renderingType) {
                case VERTICAL:
                    dx -= box.width / 2;
                    dy += parentBox.height / 2;
                    break;
                case DISK:
                case HORIZONTAL:
                    dx += parentBox.width / 2;
                    dy -= box.height / 2;
                    break;
            }
        }

        box.translate(dx, dy);
    }

    protected void publishPositions() {
        for (Node n : internalGraph) {
            if (n.hasAttribute("changed")) {
                n.removeAttribute("changed");

                sendNodeAttributeChanged(sourceId, n.getId(), "xyz", null,
                        new double[]{n.getNumber("x"), n.getNumber("y"), 0});
            }
        }
    }

    public void freezeNode(String id, boolean frozen) {
    }

    public double getForce() {
        return 0;
    }

    public Point3 getHiPoint() {
        return hi;
    }

    public long getLastStepTime() {
        return lastStep;
    }

    public String getLayoutAlgorithmName() {
        return "Hierarchical";
    }

    public Point3 getLowPoint() {
        return lo;
    }

    public int getNodeMovedCount() {
        return nodeMoved;
    }

    public double getQuality() {
        return 0;
    }

    public double getStabilization() {
        return 1 - nodeMoved / (double) internalGraph.getNodeCount();
    }

    public double getStabilizationLimit() {
        return 1;
    }

    public int getSteps() {
        return 0;
    }

    public void moveNode(String id, double x, double y, double z) {
    }

    public void setForce(double value) {
    }

    public void setQuality(double qualityLevel) {
    }

    public void setSendNodeInfos(boolean send) {
    }

    public void setStabilizationLimit(double value) {
    }

    public void shake() {
    }

    public void nodeAdded(String sourceId, long timeId, String nodeId) {
        internalGraph.addNode(nodeId);
        structureChanged = true;
    }

    public void nodeRemoved(String sourceId, long timeId, String nodeId) {
        internalGraph.removeNode(nodeId);
        structureChanged = true;
    }

    public void edgeAdded(String sourceId, long timeId, String edgeId, String fromId, String toId, boolean directed) {
        internalGraph.addEdge(edgeId, fromId, toId, directed);
        structureChanged = true;
    }

    public void edgeRemoved(String sourceId, long timeId, String edgeId) {
        internalGraph.removeEdge(edgeId);
        structureChanged = true;
    }

    public void graphCleared(String sourceId, long timeId) {
        internalGraph.clear();
        structureChanged = true;
    }

    static class Box extends LinkedList<Node> {
        private static final long serialVersionUID = -1929536876444346726L;

        Node parent;
        int level;
        double x, y;
        double width, height;
        int order;

        Box() {
            this(null, 0);
        }

        Box(Node parent, int level) {
            this.parent = parent;
            this.level = level;
            this.width = 5;
            this.height = 1;
            this.order = 0;
            this.x = 0;
            this.y = 0;
        }

        void scale(double sx, double sy) {
            width *= sx;
            height *= sy;

            for (Node edges : this) {
                edges.setAttribute("x", sx * edges.getNumber("x"));
                edges.setAttribute("y", sy * edges.getNumber("y"));
            }
        }

        void translate(double dx, double dy) {
            for (Node edges : this) {
                edges.setAttribute("x", dx + edges.getNumber("x"));
                edges.setAttribute("y", dy + edges.getNumber("y"));
            }
        }
    }

    static class LevelBox extends LinkedList<Box> {
        private static final long serialVersionUID = -5818919480025868466L;

        int level;

        LevelBox(int level) {
            this.level = level;
        }

        void sort() {
            if (level > 0) {
                Collections.sort(this, new Comparator<Box>() {
                    public int compare(Box b0, Box b1) {
                        Box pb0 = getBox(b0.parent);
                        Box pb1 = getBox(b1.parent);

                        if (pb0.order < pb1.order)
                            return -1;
                        else if (pb0.order > pb1.order)
                            return 1;

                        return 0;
                    }
                });
            }

            for (int i = 0; i < size(); i++)
                get(i).order = i;
        }
    }
}

