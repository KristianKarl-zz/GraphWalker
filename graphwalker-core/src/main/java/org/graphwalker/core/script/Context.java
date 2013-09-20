package org.graphwalker.core.script;

import javax.script.SimpleScriptContext;

/**
 * @author Nils Olsson
 */
public final class Context extends SimpleScriptContext {

    public void setAttribute(String key, Object value) {
        setAttribute(key, value, ENGINE_SCOPE);
    }
}
