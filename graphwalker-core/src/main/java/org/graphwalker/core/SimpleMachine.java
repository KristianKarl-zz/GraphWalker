/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2013 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
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
