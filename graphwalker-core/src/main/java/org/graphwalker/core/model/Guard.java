package org.graphwalker.core.model;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * @author Nils Olsson
 */
public final class Guard extends ScriptElement {

    public Guard(String script) {
        super(script);
    }

    public Boolean isFulfilled(ScriptEngine scriptEngine) throws ScriptException {
        return (Boolean)scriptEngine.eval(getScript());
    }
}
