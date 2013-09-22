package org.graphwalker.core;

import org.graphwalker.core.model.Element;
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
    private final Context context = new Context();
    private Element currentStep = null;

    public SimpleMachine(PathGenerator pathGenerator, StopCondition stopCondition) {
        this(pathGenerator, stopCondition, "JavaScript");
    }

    public SimpleMachine(PathGenerator pathGenerator, StopCondition stopCondition, String scriptLanguage) {
        this.pathGenerator = pathGenerator;
        this.stopCondition = stopCondition;
        this.scriptEngine = createScriptEngine(scriptLanguage);
    }

    private ScriptEngine createScriptEngine(String language) {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(language);
        scriptEngine.setContext(context);
        return scriptEngine;
    }

    public Element getNextStep() {
        // 1. om current är en vertex med exit actions så kör vi dem
        // 2. hämta det nya elementet
        // 2. om nya är en vertex kör vi entry actions
        // 3. om nya är en edge så kör vi actions
        return currentStep = pathGenerator.getNextStep();
    }

    public Element getCurrentStep() {
        return currentStep;
    }

    public Boolean hasNextStep() {
        return !stopCondition.isFulfilled();
    }

    public Context getContext() {
        return context;
    }
}
