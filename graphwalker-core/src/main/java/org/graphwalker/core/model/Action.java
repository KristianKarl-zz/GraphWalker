package org.graphwalker.core.model;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * @author Nils Olsson
 */
public final class Action extends ScriptElement {

    public Action(String script) {
        super(script);
    }

    public void execute(ScriptEngine scriptEngine) throws ScriptException {
        scriptEngine.eval(getScript());
    }
}
