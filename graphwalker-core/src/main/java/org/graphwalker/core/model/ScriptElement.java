package org.graphwalker.core.model;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
        return new HashCodeBuilder(29, 53)
                .append(script)
                .hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (null == object) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object instanceof ScriptElement) {
            ScriptElement element = (ScriptElement)object;
            return new EqualsBuilder()
                    .append(script, element.getScript())
                    .isEquals();
        }
        return false;
    }
}
