package org.graphwalker.core.generators;

import org.graphwalker.core.Bundle;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.utils.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomUnvisitedFirstPath extends AbstractPathGenerator {

    private final Random myRandomGenerator = new Random(System.nanoTime());

    public Element getNextStep(Machine machine) {
        List<Element> possibleElements = machine.getPossibleElements(machine.getCurrentElement());
        List<Element> unvisitedElements = new ArrayList<Element>();
        for (Element element: possibleElements) {
            if (!element.isVisited()) {
                unvisitedElements.add(element);
            }
        }
        if (0<unvisitedElements.size()) {
            return unvisitedElements.get(myRandomGenerator.nextInt(unvisitedElements.size()));
        } else if (0<possibleElements.size()) {
            return possibleElements.get(myRandomGenerator.nextInt(possibleElements.size()));
        }
        throw new PathGeneratorException(Resource.getText(Bundle.NAME, "exception.generator.path.missing"));
    }
}
