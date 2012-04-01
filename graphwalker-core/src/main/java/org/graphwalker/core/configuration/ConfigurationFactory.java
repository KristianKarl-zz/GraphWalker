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

import org.graphwalker.core.Bundle;
import org.graphwalker.core.annotations.GraphWalker;
import org.graphwalker.core.conditions.StopCondition;
import org.graphwalker.core.conditions.StopConditionFactory;
import org.graphwalker.core.filter.EdgeFilterImpl;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.generators.PathGeneratorFactory;
import org.graphwalker.core.machine.ExceptionStrategy;
import org.graphwalker.core.model.GraphMLModelFactory;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelFactory;
import org.graphwalker.core.utils.Reflection;
import org.graphwalker.core.utils.Resource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
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
     * @param clazzes a {@link java.util.List} object.
     * @return a {@link org.graphwalker.core.configuration.Configuration} object.
     */
    public static Configuration create(List<Class<?>> clazzes) {
        Configuration configuration = new ConfigurationImpl();
        for (Class<?> clazz: clazzes) {
            GraphWalker metadata = clazz.getAnnotation(GraphWalker.class);
            if (null != metadata) {
                Model model = new GraphMLModelFactory().create(metadata.id(), metadata.model());
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

    /**
     * <p>create.</p>
     *
     * @param file a {@link java.lang.String} object.
     * @return a configuration that can be used by GraphWalker
     */
    public static Configuration create(String file) {
        return create(Resource.getFile(file));
    }

    /**
     * <p>create.</p>
     *
     * @param file a {@link java.io.File} object.
     * @return a configuration that can be used by GraphWalker
     */
    public static Configuration create(File file) {
        return parse(unmarshal(file), file);
    }

    private static GraphWalkerType unmarshal(File file) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ConfigurationFactory.class.getPackage().getName());
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement jaxbElement = (JAXBElement)unmarshaller.unmarshal(new FileInputStream(file));
            return (GraphWalkerType)jaxbElement.getValue();
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
    }

    private static Configuration parse(GraphWalkerType graphWalkerType, File file) {
        Configuration configuration = new ConfigurationImpl();
        configuration.setConfigurationFile(file);
        return parse(configuration, graphWalkerType);
    }

    private static Configuration parse(Configuration configuration, GraphWalkerType graphWalkerType) {
        for (ModelType modelType: graphWalkerType.getModels().getModel()) {
            configuration.addModel(parse(configuration, modelType));
        }
        if (null != graphWalkerType.getDefaultModelId()) {
            ModelType modelType = (ModelType)graphWalkerType.getDefaultModelId();
            configuration.setDefaultModelId(modelType.getId());
        }
        String scriptLanguage = graphWalkerType.getScriptLanguage();
        if (!"".equalsIgnoreCase(scriptLanguage)) {
            configuration.setEdgeFilter(new EdgeFilterImpl(scriptLanguage));
        }
        if (null != graphWalkerType.getDefaultPathGenerator()) {
            configuration.setDefaultPathGenerator(parse(graphWalkerType.getDefaultPathGenerator()));
        }
        return configuration;
    }

    private static Model parse(Configuration configuration, ModelType modelType) {
        File modelFile = new File(modelType.getFile());
        if (!modelFile.exists()) {
            modelFile = new File(configuration.getConfigurationFile().getParentFile(), modelType.getFile());
        }
        //TODO: Handle more than graphml files
        ModelFactory modelFactory = new GraphMLModelFactory();
        Model model = modelFactory.create(modelType.getId(), modelFile.getAbsolutePath());
        if (null != modelType.getPathGenerator()) {
            model.setPathGenerator(parse(modelType.getPathGenerator()));
        }
        if (null != modelType.getClazz()) {
            try {
                Class clazz = Thread.currentThread().getContextClassLoader().loadClass(modelType.getClazz());
                model.setImplementation(clazz.newInstance());
            } catch (ClassNotFoundException e) {
                throw new ConfigurationException(Resource.getText(Bundle.NAME, "exception.class.missing", modelType.getClazz()));
            } catch (InstantiationException e) {
                throw new ConfigurationException(Resource.getText(Bundle.NAME, "exception.class.instantiation", modelType.getClazz()));
            } catch (IllegalAccessException e) {
                throw new ConfigurationException(Resource.getText(Bundle.NAME, "exception.class.instantiation", modelType.getClazz()));
            }
        }
        return model;
    }

    private static PathGenerator parse(PathGeneratorType pathGeneratorType) {
        PathGenerator pathGenerator = PathGeneratorFactory.create(pathGeneratorType.getType());
        if (null != pathGeneratorType.getStopCondition()) {
            pathGenerator.setStopCondition(parse(pathGeneratorType.getStopCondition()));
        } else {
            throw new ConfigurationException(Resource.getText(Bundle.NAME, "exception.condition.missing"));
        }
        return pathGenerator;
    }

    private static StopCondition parse(StopConditionType stopConditionType) {
        return StopConditionFactory.create(stopConditionType.getType(), stopConditionType.getValue());
    }
}
