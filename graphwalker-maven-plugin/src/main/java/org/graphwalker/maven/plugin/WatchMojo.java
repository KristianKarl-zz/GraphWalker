/*
 * #%L
 * GraphWalker Maven Plugin
 * %%
 * Copyright (C) 2011 - 2014 GraphWalker
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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.graphwalker.core.Model;
import org.graphwalker.maven.plugin.model.GraphMLModelFactory;
import org.graphwalker.maven.plugin.model.ModelFactory;
import org.graphwalker.maven.plugin.source.CodeGenerator;
import org.graphwalker.maven.plugin.source.SourceFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.sun.nio.file.SensitivityWatchEventModifier.HIGH;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * @author Nils Olsson
 */
@Mojo(name = "watch")
public class WatchMojo extends AbstractMojo {

    private static final WatchEvent.Kind[] EVENT_TYPES = new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY};

    @Component
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${project.build.sourceEncoding}", required = true, readonly = true)
    private String sourceEncoding;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/graphwalker")
    private File sourcesDirectory;

    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/graphwalker")
    private File testSourcesDirectory;

    private WatchService watchService;
    private final Map<Path, File> resourceMap = new HashMap<>();
    private final Map<WatchKey, Path> watchKeyMap = new HashMap<>();
    private final ModelFactory modelFactory = new GraphMLModelFactory();

    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /** {@inheritDoc} */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            addResources(mavenProject.getResources(), sourcesDirectory);
            addResources(mavenProject.getTestResources(), testSourcesDirectory);
            watch(resourceMap.keySet());
            for (;;) {
                WatchKey watchKey = getWatchService().take();
                Path path = watchKeyMap.get(watchKey);
                if (null == path) {
                    continue;
                }
                for (WatchEvent<?> event: watchKey.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == OVERFLOW) {
                        continue;
                    }
                    WatchEvent<Path> file = cast(event);
                    Path resolvedPath = path.resolve(file.context());
                    if (ENTRY_CREATE == event.kind() || ENTRY_MODIFY == event.kind()) {
                        update(getRootPath(resolvedPath), resolvedPath);
                    } else if (ENTRY_DELETE == event.kind()) {
                        delete(getRootPath(resolvedPath), resolvedPath);
                    }
                }
                watchKey.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MojoExecutionException("", e);
        }
    }

    private void addResources(List<Resource> resources, File outputDirectory) {
        for (Resource resource: resources) {
            resourceMap.put(FileSystems.getDefault().getPath(resource.getDirectory()), outputDirectory);
        }
    }

    /**
     * <p>Getter for the field <code>watchService</code>.</p>
     *
     * @return a {@link java.nio.file.WatchService} object.
     * @throws java.io.IOException if any.
     */
    public WatchService getWatchService() throws IOException {
        if (null == watchService) {
            watchService = FileSystems.getDefault().newWatchService();
        }
        return watchService;
    }

    private void watch(Set<Path> paths) throws IOException {
        for (Path path: paths) {
            watch(path);
        }
    }

    private void watch(Path path) throws IOException {
        if (Files.exists(path) && !watchKeyMap.values().contains(path)) {
            if (Files.isDirectory(path)) {
                WatchKey watchKey = path.register(getWatchService(), EVENT_TYPES, HIGH);
                watchKeyMap.put(watchKey, path);
                getLog().info("Watching: " + path.toString());
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attributes) throws IOException {
                        watch(path);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        }
    }

    private Path getRootPath(Path path) throws MojoExecutionException {
        for (Path rootPath: resourceMap.keySet()) {
            if (path.startsWith(rootPath)) {
                return rootPath;
            }
        }
        throw new MojoExecutionException("Path not found");
    }

    private boolean isModified(Path outputPath, Path path) throws IOException {
        return Files.getLastModifiedTime(outputPath).to(TimeUnit.MILLISECONDS)
                < Files.getLastModifiedTime(path).to(TimeUnit.MILLISECONDS);
    }

    private boolean isSupportedFileType(Path path) throws IOException {
        return modelFactory.accept(path);
    }

    private void generate(SourceFile sourceFile) {
        File outputFile = sourceFile.getOutputPath().toFile();
        try {
            Model model = modelFactory.create(Paths.get(sourceFile.getInputPath().toFile().getAbsolutePath()));
            String source = new CodeGenerator(sourceFile, model).generate();
            if (Files.exists(sourceFile.getOutputPath())) {
                String existingSource = StringUtils.removeDuplicateWhitespace(FileUtils.fileRead(outputFile, sourceEncoding));
                if (existingSource.equals(StringUtils.removeDuplicateWhitespace(new String(source.getBytes(), sourceEncoding)))) {
                    return;
                }
            }
            if (getLog().isInfoEnabled()) {
                getLog().info("Generate: " + sourceFile.getOutputPath());
            }
            FileUtils.mkdir(sourceFile.getOutputPath().getParent().toFile().getAbsolutePath());
            FileUtils.fileDelete(outputFile.getAbsolutePath());
            FileUtils.fileWrite(outputFile.getAbsolutePath(), sourceEncoding, source);
        } catch (Throwable t) {
            if (getLog().isInfoEnabled()) {
                getLog().info("Error: Generate: " + sourceFile.getOutputPath());
            }
            if (getLog().isDebugEnabled()) {
                getLog().debug("Error: Generate: " + sourceFile.getOutputPath(), t);
            }
        }
    }

    private void update(Path root, Path path) throws IOException {
        if (Files.exists(path) && !Files.isHidden(path)) {
            if (Files.isDirectory(path)) {
                watch(path);
            } else if (isSupportedFileType(path)) {
                SourceFile sourceFile = new SourceFile(path, root, resourceMap.get(root).toPath());
                if (!Files.exists(sourceFile.getOutputPath()) || isModified(sourceFile.getOutputPath(), path)) {
                    generate(sourceFile);
                }
            }
        }
    }

    private void delete(Path root, Path path) throws IOException {
        if (isSupportedFileType(path)) {
            SourceFile sourceFile = new SourceFile(path, root, resourceMap.get(root).toPath());
            if (Files.exists(sourceFile.getOutputPath())) {
                Files.delete(sourceFile.getOutputPath());
                if (!Files.exists(sourceFile.getOutputPath())) {
                    getLog().info("Delete: " + sourceFile.getOutputPath());
                }
            }
        }
    }
}
