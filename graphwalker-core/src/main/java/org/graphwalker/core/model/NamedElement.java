package org.graphwalker.core.model;

public abstract class NamedElement extends Element {

    private final String name;

    public NamedElement(String id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
