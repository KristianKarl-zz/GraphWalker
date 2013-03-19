package org.graphwalker.java;

import org.graphwalker.core.conditions.support.EdgeCoverage;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.generators.support.RandomPath;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.support.ModelContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphWalker {

    private List<ModelContext> contexts = new ArrayList<ModelContext>();
    private Map<Model, Object> implementations = new HashMap<Model, Object>();
    private Machine machine = null;

    public void addModel(Model model, Object object) {
        ModelContext context = new ModelContext(model);
        context.setPathGenerator(new RandomPath(new EdgeCoverage(100)));
        contexts.add(context);
        implementations.put(model, object);
    }

    public void addModel(Model model, PathGenerator pathGenerator, Object object) {
        ModelContext context = new ModelContext(model);
        context.setPathGenerator(pathGenerator);
        contexts.add(context);
        implementations.put(model, object);
    }

    private Machine getMachine() {
        //if (null == machine) {
        //    machine = new Machine();
        //}
        return machine;
    }

    public void execute(Model model) {
        /*
        Machine machine = new Machine(contexts, model);
        while (machine.hasMoreSteps()) {
            String step = machine.getNextStep();
            Machine currentModel = machine.getCurrentModel();
            if (implementations.containsKey(model)) {
                // execute method
            }
        }
        */
    }

    public boolean hasMoreSteps() {
        return getMachine().hasMoreSteps();
    }

    public String getNextStep() {
        return getMachine().getNextStep();
    }

    public String getCurrentStep() {
        return getMachine().getCurrentStep();
    }
}
