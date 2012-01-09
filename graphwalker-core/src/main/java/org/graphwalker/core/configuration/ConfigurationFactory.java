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

import org.graphwalker.core.conditions.All;
import org.graphwalker.core.conditions.Any;
import org.graphwalker.core.conditions.StopCondition;
import org.graphwalker.core.conditions.StopConditionFactory;
import org.graphwalker.core.filter.EdgeFilterImpl;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.generators.PathGeneratorFactory;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelFactory;
import org.graphwalker.core.util.Resource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;

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
        Model model = ModelFactory.create(modelType.getId(), modelFile.getAbsolutePath());
        if (null != modelType.getPathGenerator()) {
            model.setPathGenerator(parse(modelType.getPathGenerator()));
        }
        return model;
    }

    private static PathGenerator parse(PathGeneratorType pathGeneratorType) {
        PathGenerator pathGenerator = PathGeneratorFactory.create(pathGeneratorType.getType());
        if (null != pathGeneratorType.getAny()) {
            pathGenerator.setStopCondition(parse(pathGeneratorType.getAny()));
        } else if (null != pathGeneratorType.getAll()) {
            pathGenerator.setStopCondition(parse(pathGeneratorType.getAll()));
        } else if (null != pathGeneratorType.getStopCondition()) {
            pathGenerator.setStopCondition(parse(pathGeneratorType.getStopCondition()));
        } else {
            throw new ConfigurationException(Resource.getText("exception.condition.missing"));
        }
        return pathGenerator;
    }

    private static StopCondition parse(AnyStopConditionType anyStopConditionsType) {
        Any any = new Any();
        for (Object stopCondition: anyStopConditionsType.getAllOrAnyOrStopCondition()) {
            if (stopCondition instanceof AllStopConditionsType) {
                any.addStopCondition(parse((AllStopConditionsType)stopCondition));
            } else if (stopCondition instanceof AnyStopConditionType) {
                any.addStopCondition(parse((AnyStopConditionType)stopCondition));
            } else if (stopCondition instanceof StopConditionType) {
                any.addStopCondition(parse((StopConditionType)stopCondition));
            }       
        }   
        return any;
    }

    private static StopCondition parse(AllStopConditionsType allStopConditionsType) {
        All all = new All();
        for (Object stopCondition: allStopConditionsType.getAllOrAnyOrStopCondition()) {
            if (stopCondition instanceof AllStopConditionsType) {
                all.addStopCondition(parse((AllStopConditionsType)stopCondition));
            } else if (stopCondition instanceof AnyStopConditionType) {
                all.addStopCondition(parse((AnyStopConditionType)stopCondition));
            } else if (stopCondition instanceof StopConditionType) {
                all.addStopCondition(parse((StopConditionType)stopCondition));
            }
        }
        return all;
    }

    private static StopCondition parse(StopConditionType stopConditionType) {
        return StopConditionFactory.create(stopConditionType.getType(), stopConditionType.getValue());
    }
}
