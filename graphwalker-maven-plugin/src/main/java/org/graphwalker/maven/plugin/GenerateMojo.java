/*
 * #%L
 * GraphWalker Maven Plugin
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
package org.graphwalker.maven.plugin;

import org.apache.maven.model.Resource;
import org.apache.maven.plugins.annotations.*;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelElement;
import org.graphwalker.core.model.support.GraphMLModelFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>GenerateMojo class.</p>
 *
 * @author nilols
 */
@Mojo(name = "generate"
        , defaultPhase = LifecyclePhase.GENERATE_SOURCES
        , requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.INITIALIZE)
public class GenerateMojo extends AbstractGraphWalkerMojo {

    @Parameter( defaultValue = "${project.resources}", required = true, readonly = true)
    private List<Resource> resources;

    @Parameter( defaultValue = "${project.testResources}", required = true, readonly = true)
    private List<Resource> testResources;

    @Parameter(property = "graphwalker.generate.force", defaultValue = "true")
    private boolean force;

    @Override
    public void executeMojo() {
        generateInterface(getIncludes(), getExcludes(), resources);
        generateInterface(getIncludes(), getExcludes(), testResources);
    }

    protected List<String> getIncludes() {
        List<String> includes = super.getIncludes();
        if (includes.isEmpty()) {
            includes.add("**/*.graphml"); // TODO: hämta från core alla typer som stödjs
        }
        return includes;
    }

    private void generateInterface(List<String> includes, List<String> excludes, List<Resource> resources) {
        for (Resource resource: resources) {
            generateInterface(StringUtils.join(includes.toArray(), ",")
                    , StringUtils.join(excludes.toArray(), ",")
                    , new File(resource.getDirectory()));
        }
    }

    private void generateInterface(String includes, String excludes, File directory) {
        for (File file: findModels(includes, excludes, directory)) {
            generateInterface(directory, file);
        }
    }

    private void generateInterface(File directory, File file) {
        generateInterface(file
                , getInterfaceName(file)
                , getPackageName(directory, file)
                , getOutputFile(directory, file));
    }

    private void generateInterface(File modelFile, String interfaceName, String packageName, File outputFile) {
        if (force || !outputFile.exists()) {
            // TODO: snygga till meddelandet som skrivs ut i loggen
            getLog().info("Generate ["+modelFile+"] interface: "+interfaceName+" with package "+packageName+" output in "+outputFile);
            // TODO: ändra så att man inte behöver skapa en specifik factory utan core tar han om att rätt factory används
            GraphMLModelFactory factory = new GraphMLModelFactory();
            Model model = factory.create(interfaceName, modelFile.getPath());
            StringBuilder builder = new StringBuilder();
            // TODO: lägg till info om att filen genereras och att man inte ska uppdatera den om man tänkt att generera om den
            if (!"".equals(packageName)) {
                builder.append("package ").append(packageName).append(";\n\n");
            }
            builder.append("public interface ").append(interfaceName).append("{\n\n");
            Set<String> methodNames = new HashSet<String>();
            for (ModelElement element: model.getModelElements()) {
                methodNames.add(element.getName());
            }
            for (String methodName: methodNames) {
                builder.append("    void ").append(methodName).append("();\n");
            }
            builder.append("}");
            try {
                // TODO: använd projektets encoding
                FileUtils.mkdir(outputFile.getParent());
                FileUtils.fileDelete(outputFile.getPath());
                FileUtils.fileWrite(outputFile, builder.toString());
            } catch (IOException e) {
                // TODO: skriv ut ett felmeddelande att vi misslyckades att skapa interfacet
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            }
        }
    }

    private List<File> findModels(String includes, String excludes, File... directories) {
        List<File> models = new ArrayList<File>();
        for (File directory: directories) {
            try {
                for (Object filename: FileUtils.getFileNames(directory, includes, excludes, true, true)) {
                    models.add(new File((String)filename));
                }
            } catch (IOException e) {
                // TODO: skriv ut ett felmeddelande att vi misslyckades att skapa interface för denna katalog
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return models;
    }

    private String getInterfaceName(File file) {
        String interfaceName = file.getName();
        interfaceName = interfaceName.substring(0, interfaceName.lastIndexOf("."));
        interfaceName = interfaceName.substring(0, 1).toUpperCase() + interfaceName.substring(1);
        return interfaceName;
    }

    private String getPackageName(File directory, File file) {
        String packageName = (null!=file.getParent()?file.getParent():"");
        if (!"".equals(packageName)) {
            packageName = packageName.substring(directory.getPath().length());
            packageName = packageName.replaceAll(File.separator, ".");
            if (packageName.startsWith(".")) {
                packageName = packageName.substring(1);
            }
        }
        return packageName;
    }

    private File getOutputFile(File directory, File file) {
        File outputFile;
        if (null != directory.getParentFile()) {
            outputFile = new File(directory.getParentFile(), "java");
        } else {
            outputFile = new File("java");
        }
        if (null != file.getParent()) {
            String packageName = getPackageName(directory, file);
            outputFile = new File(outputFile, packageName.replaceAll("\\.", File.separator));
        }
        return new File(outputFile, getInterfaceName(file)+".java");
    }

}
