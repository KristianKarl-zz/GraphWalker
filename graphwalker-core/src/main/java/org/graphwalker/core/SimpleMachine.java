package org.graphwalker.core;

import org.graphwalker.core.script.Context;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @author Nils Olsson
 */
public final class SimpleMachine implements Machine {

    private final PathGenerator pathGenerator;
    private final StopCondition stopCondition;
    private final ScriptEngine scriptEngine;

    public SimpleMachine(PathGenerator pathGenerator, StopCondition stopCondition, String scriptLanguage) {
        this.pathGenerator = pathGenerator;
        this.stopCondition = stopCondition;
        this.scriptEngine = createScriptEngine(scriptLanguage);
    }

    private ScriptEngine createScriptEngine(String language) {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(language);
        scriptEngine.setContext(new Context());
        return scriptEngine;
    }

    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
