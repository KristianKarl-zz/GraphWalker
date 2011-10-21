/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 GraphWalker
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
package org.graphwalker.core.multiple;

import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.multipleModels.ModelAPI;

/**
 * Hello world!
 */
public class Model_A_API extends ModelAPI {

    public Model_A_API(String model, boolean efsm, PathGenerator generator) {
        super(model, efsm, generator, false);
    }

    public void e_ExitClient() {
    }

    public void e_Init() {
    }

    public void e_LogOut() {
    }

    public void e_Start() {
    }

    public void e_ToggleRememberMe() {
    }

    public void e_ValidLogin() {
    }

    public void v_ClientNotRunning() {
    }

    public void v_ClientRunning() {
    }

    public void v_Login() {
    }

    public void v_WhatsNew() {
    }
}
