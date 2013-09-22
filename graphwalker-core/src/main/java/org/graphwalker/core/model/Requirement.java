package org.graphwalker.core.model;

import org.apache.commons.lang3.Validate;

/**
 * @author Nils Olsson
 */
public final class Requirement {

    private final String id;

    public Requirement(String id) {
        this.id = Validate.notEmpty(Validate.notNull(id));
    }

    public String getId() {
        return id;
    }
}
