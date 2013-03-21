package org.graphwalker.java;

import org.graphwalker.core.conditions.support.EdgeCoverage;
import org.graphwalker.core.filter.EdgeFilter;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.generators.support.RandomPath;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelElement;
import org.graphwalker.core.model.support.ModelContext;
import org.graphwalker.java.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphWalker {

    private final Map<Model, ModelContext> contexts = new HashMap<Model, ModelContext>();
    private final Map<Model, Object> implementations = new HashMap<Model, Object>();
    private Machine machine;

    public void addModel(Model model, Object object) {
        ModelContext context = new ModelContext(model);
        context.setPathGenerator(new RandomPath(new EdgeCoverage(100)));
        contexts.put(model, context);
        implementations.put(model, object);
    }

    public void addModel(Model model, PathGenerator pathGenerator, Object object) {
        ModelContext context = new ModelContext(model);
        context.setPathGenerator(pathGenerator);
        contexts.put(model, context);
        implementations.put(model, object);
    }

    public void addModel(Model model, PathGenerator pathGenerator, String scriptLanguage, Object object) {
        ModelContext context = new ModelContext(model);
        context.setPathGenerator(pathGenerator);
        context.setEdgeFilter(new EdgeFilter(scriptLanguage));
        contexts.put(model, context);
        implementations.put(model, object);
    }

    public void execute(Model model) {
        machine = new Machine(new ArrayList<ModelContext>(contexts.values()));
        machine.setCurrentContext(contexts.get(model));
        while (machine.hasMoreSteps()) {
            ModelElement element = machine.getNextStep();
            ModelContext context = machine.getCurrentContext();
            if (implementations.containsKey(model)) {
                // TODO: Lägg till så argument skickas med till metoden
                Reflection.execute(implementations.get(context.getModel()), element.getName());
            }
        }
    }

    public boolean isAllModelsDone() {
        return machine.hasMoreSteps();
    }
}
