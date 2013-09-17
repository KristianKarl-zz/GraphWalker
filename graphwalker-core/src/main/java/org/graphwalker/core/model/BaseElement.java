package org.graphwalker.core.model;

import org.graphwalker.api.graph.Element;

/**
 * @author Nils Olsson
 */
public abstract class BaseElement implements Element {

    private final String name;

    protected BaseElement(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
