package org.graphwalker.maven.plugin;

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

import com.sun.nio.file.SensitivityWatchEventModifier;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.graphwalker.maven.plugin.model.GraphMLModelFactory;
import org.graphwalker.maven.plugin.model.ModelFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

/**
 * @author Nils Olsson
 */
@Mojo(name = "watch")
public class WatchMojo extends AbstractGenerateMojo {

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/graphwalker")
    private File generatedSourcesDirectory;

    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/graphwalker")
    private File generatedTestSourcesDirectory;

    private static final WatchEvent.Kind[] WATCH_KIND =  new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY};

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys = new HashMap<>();

    public WatchMojo() throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
    }

    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        // When first executed regenerate all interface
        try {
            registerWatch(getMavenProject().getResources());
            registerWatch(getMavenProject().getTestResources());
            for (;;) {
                WatchKey key = watcher.take();
                Path path = keys.get(key);
                if (null == path) {
                    continue;
                }
                for (WatchEvent<?> event: key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == OVERFLOW) {
                        continue;
                    }
                    WatchEvent<Path> file = cast(event);
                    Path resolvedPath = path.resolve(file.context());
                    if (ENTRY_MODIFY == event.kind()) {
                        if (!Files.isDirectory(resolvedPath, NOFOLLOW_LINKS)) {
                            generate(resolvedPath);
                        }
                    } else if (ENTRY_CREATE == event.kind()) {
                        if (Files.isDirectory(resolvedPath, NOFOLLOW_LINKS)) {
                            registerPath(resolvedPath);
                        } else {
                            generate(resolvedPath);
                        }
                    } else if (ENTRY_DELETE == event.kind()) {
                        delete(resolvedPath);
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            throw new MojoExecutionException("", e);
        }
    }

    private void registerWatch(List<Resource> resources) throws IOException {
        for (Resource resource: resources) {
            Path path = FileSystems.getDefault().getPath(resource.getDirectory());
            if (Files.exists(path)) {
                registerPath(path);
            }
        }
    }

    private void registerPath(Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attributes) throws IOException {
                keys.put(path.register(watcher, WATCH_KIND, SensitivityWatchEventModifier.HIGH), path);
                getLog().info("Watching: "+path.toString());
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void generate(Path path) {
        File baseDirectory = path.toFile().getParentFile();
        ModelFactory factory = new GraphMLModelFactory();
        if (Files.isDirectory(path, NOFOLLOW_LINKS)) {
            for (File file: findFiles(factory.getSupportedFileTypes(), null, baseDirectory)) {
                generate(file, baseDirectory, getGeneratedSourcesDirectory(path));
            }
        } else if (factory.getSupportedFileTypes().contains("**/*."+FileUtils.extension(path.toString()))) {
            generate(path.toFile(), baseDirectory, getGeneratedSourcesDirectory(path));
        }
    }

    private void delete(Path file) {
        getLog().info("remove interface: "+file.toString());
        for (Path path: keys.values()) {
            if (path.startsWith(file)) {
                keys.remove(path);
                getLog().info("Unwatching: "+path.toString());
            }
        }
        // TODO: remove generated files
    }

    @Override
    protected File getGeneratedSourcesDirectory() {
        return null;
    }

    protected File getGeneratedSourcesDirectory(Path path) {
        for (Resource resource: getMavenProject().getResources()) {
            if (path.startsWith(FileSystems.getDefault().getPath(resource.getDirectory()))) {
                return generatedSourcesDirectory;
            }
        }
        for (Resource resource: getMavenProject().getTestResources()) {
            if (path.startsWith(FileSystems.getDefault().getPath(resource.getDirectory()))) {
                return generatedTestSourcesDirectory;
            }
        }
        return null;
    }
}
