package org.graphwalker.core.algorithm;

import org.graphwalker.core.Model;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Path;
import org.graphwalker.core.model.Vertex;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nils Olsson
 */
public final class Eulerian implements Algorithm {

    private final Model model;
    private final Map<Vertex, PolarityCounter> polarities;

    public Eulerian(Model model) {
        this.model = model;
        this.polarities = new HashMap<>(model.getVertices().size());
        polarize();
    }

    public enum EulerianType {
        EULERIAN, SEMI_EULERIAN, NOT_EULERIAN
    }

    private void polarize() {
        for (Edge edge : model.getEdges()) {
            getPolarityCounter(edge.getSourceVertex()).decrease();
            getPolarityCounter(edge.getTargetVertex()).increase();
        }
        for (Vertex vertex : model.getVertices()) {
            if (!polarities.get(vertex).hasPolarity()) {
                polarities.remove(vertex);
            }
        }
    }

    private PolarityCounter getPolarityCounter(Vertex vertex) {
        if (!polarities.containsKey(vertex)) {
            polarities.put(vertex, new PolarityCounter());
        }
        return polarities.get(vertex);
    }

    public EulerianType getEulerianType() {
        if (polarities.isEmpty()) {
            return EulerianType.EULERIAN;
        }
        if (2 == polarities.size()) {
            return EulerianType.SEMI_EULERIAN;
        }
        return EulerianType.NOT_EULERIAN;
    }

    public Model eulerize() {
        switch (getEulerianType()) {
            case EULERIAN:
                break; // missing start edge
            case NOT_EULERIAN:
                break; // TODO:
        }
        return model; // SEMI_EULERIAN;
    }

    public Path<Element> getEulerPath() {
        // TODO:
        return new Path<Element>(Collections.EMPTY_LIST);
    }

    class PolarityCounter {

        private int polarity = 0;

        public void increase() {
            polarity += 1;
        }

        public void decrease() {
            polarity -= 1;
        }

        public boolean hasPolarity() {
            return 0 != polarity;
        }

        public int getPolarity() {
            return polarity;
        }
    }
}
