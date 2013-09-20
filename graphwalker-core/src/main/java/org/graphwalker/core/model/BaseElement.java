package org.graphwalker.core.model;

import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.Set;

/**
 * @author Nils Olsson
 */
public abstract class BaseElement implements Element {

    private final String name;

    public BaseElement(String name) {
        this.name = Validate.notNull(name);
    }

    protected <T> Set<T> unmodifiableSet(Set<? extends T> set) {
        return Collections.unmodifiableSet(null != set?set:Collections.<T>emptySet());
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (null == object) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (!(object instanceof BaseElement)) {
            return false;
        }
        BaseElement baseElement = (BaseElement)object;
        return baseElement.getName().equals(name);
    }
}
