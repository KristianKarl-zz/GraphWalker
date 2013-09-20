package org.graphwalker.core.model;

import org.apache.commons.lang3.Validate;

/**
 * @author Nils Olsson
 */
public abstract class ScriptElement {

    private final String script;

    public ScriptElement(String script) {
        this.script = Validate.notNull(script);
    }

    public String getScript() {
        return script;
    }

    @Override
    public int hashCode() {
        return script.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (null == object) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (!(object instanceof ScriptElement)) {
            return false;
        }
        ScriptElement scriptElement = (ScriptElement)object;
        return scriptElement.getScript().equals(script);
    }
}
