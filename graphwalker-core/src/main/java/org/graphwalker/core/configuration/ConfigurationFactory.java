/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2012 GraphWalker
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
package org.graphwalker.core.configuration;

import org.graphwalker.core.annotations.GraphWalker;
import org.graphwalker.core.conditions.StopCondition;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.machine.ExceptionStrategy;
import org.graphwalker.core.model.factories.GraphMLModelFactory;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.utils.Reflection;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>ConfigurationFactory class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class ConfigurationFactory {

    private ConfigurationFactory() {
    }

    /**
     * <p>create.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @return a {@link org.graphwalker.core.configuration.Configuration} object.
     */
    public static Configuration create(Class<?> clazz) {
        List<Class<?>> clazzes = new ArrayList<Class<?>>();
        clazzes.add(clazz);
        return create(clazzes);
    }

    /**
     * <p>create.</p>
     *
     * @param clazzes a {@link java.util.List} object.
     * @return a {@link org.graphwalker.core.configuration.Configuration} object.
     */
    public static Configuration create(List<Class<?>> clazzes) {
        Configuration configuration = new Configuration();
        for (Class<?> clazz : clazzes) {
            GraphWalker metadata = clazz.getAnnotation(GraphWalker.class);
            if (null != metadata) {
                Model model = new GraphMLModelFactory().create(expand(metadata.id(), clazz), expand(metadata.model(), clazz));
                model.setImplementation(Reflection.newInstance(clazz));
                PathGenerator pathGenerator = Reflection.newInstance(metadata.pathGenerator());
                StopCondition stopCondition = Reflection.newInstance(metadata.stopCondition(), metadata.stopConditionValue());
                pathGenerator.setStopCondition(stopCondition);
                model.setPathGenerator(pathGenerator);
                model.setGroup(metadata.group());
                ExceptionStrategy exceptionStrategy = Reflection.newInstance(metadata.exceptionStrategy());
                model.setExceptionStrategy(exceptionStrategy);
                configuration.addModel(model);
            }
        }
        return configuration;
    }

    private static String expand(String property, Class<?> clazz) {
        return property.replaceAll("\\$\\{className\\}", clazz.getSimpleName());
    }

}
