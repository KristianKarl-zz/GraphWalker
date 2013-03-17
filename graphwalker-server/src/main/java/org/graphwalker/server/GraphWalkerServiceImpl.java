/*
 * #%L
 * GraphWalker Server
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
package org.graphwalker.server;

import org.graphwalker.core.configuration.Configuration;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.model.support.ModelContext;
import org.graphwalker.service.*;

import java.util.*;

/**
 * <p>GraphWalkerImpl class.</p>
 *
 * @author nilols
 */
public class GraphWalkerServiceImpl implements GraphWalkerService.Iface {

    private Map<String, ModelContext> contexts = new HashMap<String, ModelContext>();
    private Configuration configuration = new Configuration();
    private Machine machine = new Machine();

    public String authenticate(String username, String password) {
        return UUID.randomUUID().toString();  // TODO: Fix me (Auto generated)
    }

    public List<Model> listModels(String token) {
        return new ArrayList<Model>();  // TODO: Fix me (Auto generated)
    }

    public Model getModel(String token, String id) {
        return new Model();  // TODO: Fix me (Auto generated)
    }

    public Model createModel(String token, Model model) {
        model.setId(UUID.randomUUID().toString());
        return model;  // TODO: Fix me (Auto generated)
    }

    public Model updateModel(String token, Model model) {
        return new Model();  // TODO: Fix me (Auto generated)
    }

    public void deleteModel(String token, Model model) {
        // TODO: Fix me (Auto generated)
    }

    public ExecutionContext execute(String token, ExecutionContext context) {
        return context;  // TODO: Fix me (Auto generated)
    }

    public boolean hasMoreSteps(String token, ExecutionContext context) {
        return false;  // TODO: Fix me (Auto generated)
    }

    public ExecutionContext getNextStep(String token, ExecutionContext context) {
        return null;  // TODO: Fix me (Auto generated)
    }

    public ExecutionContext fail(String token, ExecutionContext context) {
        // TODO: Fix me (Auto generated)
        return null;
    }

    public ResultList findResults(String token, ResultFilter filter) {
        return null;  // TODO: Fix me (Auto generated)
    }
}
