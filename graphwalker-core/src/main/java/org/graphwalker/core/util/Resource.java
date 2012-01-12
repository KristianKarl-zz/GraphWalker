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
package org.graphwalker.core.util;

import net.sf.oval.constraint.MinLength;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;
import org.graphwalker.core.Bundle;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p>Resource class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
@Guarded
public class Resource {

    private Resource() {
    }
    
    /**
     * <p>getText.</p>
     *
     * @param bundle a {@link java.lang.String} object.
     * @param key a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     * @param params a {@link java.lang.String} object.
     */
    public static String getText(@NotNull @MinLength(1) final String bundle, @NotNull @MinLength(1) final String key, final String... params) {
        return getText(bundle, Locale.getDefault(), key, params);
    }

    /**
     * <p>getText.</p>
     *
     * @param bundle a {@link java.lang.String} object.
     * @param key a {@link java.lang.String} object.
     * @param locale a {@link java.util.Locale} object.
     * @return a {@link java.lang.String} object.
     * @param params a {@link java.lang.String} object.
     */
    public static String getText(@NotNull @MinLength(1) final String bundle, @NotNull final Locale locale, @NotNull @MinLength(1) final String key, final String... params) {
        return MessageFormat.format(ResourceBundle.getBundle(bundle, locale).getString(key), params);
    }

    /**
     * <p>getIcon.</p>
     *
     * @param bundle a {@link java.lang.String} object.
     * @param key a {@link java.lang.String} object.
     * @return a {@link javax.swing.Icon} object.
     */
    public static Icon getIcon(@NotNull @MinLength(1) final String bundle, @NotNull @MinLength(1) final String key) {
        return getIcon(getText(bundle, key));
    }

    /**
     * <p>getIcon.</p>
     *
     * @param filename a {@link java.lang.String} object.
     * @return a {@link javax.swing.Icon} object.
     */
    public static Icon getIcon(@NotNull @MinLength(1) final String filename) {
        File file = createFile(filename);
        if (file.exists()) {
            return new ImageIcon(file.getPath());
        } else {
            return new ImageIcon(getResource(file.getPath()).getPath());
        }
    }

    /**
     * <p>getFile.</p>
     *
     * @param filename a {@link java.lang.String} object.
     * @return a {@link java.io.File} object.
     */
    public static File getFile(@NotNull @MinLength(1) final String filename) {
        File file = createFile(filename);
        if (file.exists()) {
            return file;
        } else {
            return getResource(file.getPath());
        }
    }

    private static String[] splitPath(String filename) {
        return filename.split("[\\\\/]");
    }
    
    private static File createFile(String filename) {
        File createdFile = null;
        for (String part: splitPath(filename)) {
            createdFile = new File(createdFile, part);
        }
        return createdFile;
    }

    private static File getResource(final String filename) {
        URL resource = Resource.class.getResource(filename);
        if (null == resource) {
            resource = Thread.currentThread().getContextClassLoader().getResource(filename);
        }
        if (null != resource) {
            return new File(resource.getFile());
        }
        throw new ResourceException(getText(Bundle.NAME, "exception.file.missing", filename));
    }

}
