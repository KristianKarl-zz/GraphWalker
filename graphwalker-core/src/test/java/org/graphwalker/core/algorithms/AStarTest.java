package org.graphwalker.core.algorithms;

import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelElement;
import org.graphwalker.core.model.ModelFactory;
import org.graphwalker.core.model.support.GraphMLModelFactory;
import org.graphwalker.core.model.support.ModelContext;
import org.junit.Test;

import java.util.List;

public class AStarTest {

    @Test
    public void simplePath() {
        ModelFactory factory = new GraphMLModelFactory();
        Model model = factory.create("simple", "/models/algorithms/simple.graphml");
        ModelContext context = new ModelContext(model);
        List<ModelElement> path = AStar.getPath(context, model.getStartVertex(), model.getVerticesByName("Goal").get(0));

        int i = 0;
    }

    @Test
    public void blockedPath() {
        ModelFactory factory = new GraphMLModelFactory();
        Model model = factory.create("simpleBlocked", "/models/algorithms/blocked.graphml");
        ModelContext context = new ModelContext(model);
        List<ModelElement> path = AStar.getPath(context, model.getStartVertex(), model.getVerticesByName("Goal").get(0));

        int i = 0;
    }

}
