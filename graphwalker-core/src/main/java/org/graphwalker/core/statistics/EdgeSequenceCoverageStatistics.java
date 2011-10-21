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

package org.graphwalker.core.statistics;

import org.graphwalker.core.graph.AbstractElement;
import org.graphwalker.core.graph.Edge;
import org.graphwalker.core.graph.Graph;

import java.util.HashSet;
import java.util.Stack;

/**
 * @author Johan Tejle
 */
public class EdgeSequenceCoverageStatistics extends Statistics {

    private HashSet<String> usedSequences;
    private HashSet<String> allSequences;
    private Stack<AbstractElement> pathHistory;
    private int length;

    /**
     * @param model
     * @param sequenceLength
     */
    @SuppressWarnings("unchecked")
    public EdgeSequenceCoverageStatistics(Graph model, int sequenceLength) {
        this.length = sequenceLength;
        usedSequences = new HashSet<String>();
        allSequences = new HashSet<String>();
        pathHistory = new Stack<AbstractElement>();

        Stack<Edge>[] possibilities = new Stack[sequenceLength];
        for (int i = 0; i < sequenceLength; i++) {
            possibilities[i] = new Stack<Edge>();
        }
        possibilities[0].addAll(model.getEdges());
        while (possibilities[0].size() > 0) {
            for (int i = 0; i < sequenceLength - 1; i++) {
                if (possibilities[i].size() == 0)
                    return;
                if (possibilities[i + 1].size() == 0)
                    possibilities[i + 1].addAll(model.getOutEdges(model.getDest(possibilities[i].peek())));
            }
            while (possibilities[sequenceLength - 1].size() > 0) {
                allSequences.add(getSequenceName(possibilities));
                possibilities[sequenceLength - 1].pop();
            }
            for (int i = sequenceLength - 1; i > 0; i--) {
                if (possibilities[i].size() == 0) {
                    possibilities[i - 1].pop();
                }
            }
        }
    }

    /**
     * @param possibilities
     * @return
     */
    private String getSequenceName(Stack<Edge>[] possibilities) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Stack<Edge> possibility : possibilities) {
            stringBuilder.append(possibility.peek().hashCode());
            stringBuilder.append(" ");
        }
        return stringBuilder.toString().trim();
    }

    /**
     * @return
     */
    private String getCurrentSequenceName() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < pathHistory.size(); i++) {
            stringBuilder.append(pathHistory.elementAt(i).hashCode());
            stringBuilder.append(" ");
        }
        return stringBuilder.toString().trim();
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * org.graphwalker.statistics.Statistics#addProgress(edu.uci.ics.jung.graph
      * .impl.AbstractElement)
      */
    @Override
    public void addProgress(AbstractElement element) {
        if (element instanceof Edge) {
            pathHistory.add(element);
        }
        if (pathHistory.size() > this.length) {
            pathHistory.remove(0);
        }
        if (pathHistory.size() == this.length) {
            usedSequences.add(getCurrentSequenceName());
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see org.graphwalker.statistics.Statistics#getCurrent()
      */
    @Override
    public int getCurrent() {
        return usedSequences.size();
    }

    /*
      * (non-Javadoc)
      *
      * @see org.graphwalker.statistics.Statistics#getMax()
      */
    @Override
    public int getMax() {
        return allSequences.size();
    }

}
