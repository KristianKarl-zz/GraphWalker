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
package org.graphwalker.io.common;

import org.graphwalker.core.Bundle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Nils Olsson
 */
public final class ResourceUtils {

    private ResourceUtils() {
    }

    /**
     * <p>getText.</p>
     *
     * @param bundle a {@link java.lang.String} object.
     * @param key    a {@link java.lang.String} object.
     * @param params a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getText(final String bundle, final String key, final Object... params) {
        return getText(bundle, Locale.getDefault(), key, params);
    }

    /**
     * <p>getText.</p>
     *
     * @param bundle a {@link java.lang.String} object.
     * @param key    a {@link java.lang.String} object.
     * @param locale a {@link java.util.Locale} object.
     * @param params a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getText(final String bundle, final Locale locale, final String key, final Object... params) {
        String message = ResourceBundle.getBundle(bundle, locale).getString(key);
        if (null != message) {
            MessageFormat messageFormat = new MessageFormat("");
            messageFormat.setLocale(locale);
            messageFormat.applyPattern(message);
            return messageFormat.format(params);
        }
        throw new ResourceException();
    }

    /**
     * <p>getResourceAsIcon.</p>
     *
     * @param bundle a {@link java.lang.String} object.
     * @param key    a {@link java.lang.String} object.
     * @return a {@link javax.swing.Icon} object.
     */
    public static Icon getResourceAsIcon(final String bundle, final String key) {
        return getResourceAsIcon(getText(bundle, key));
    }

    /**
     * <p>getResourceAsIcon.</p>
     *
     * @param filename a {@link java.lang.String} object.
     * @return a {@link javax.swing.Icon} object.
     */
    public static Icon getResourceAsIcon(final String filename) {
        File file = createFile(filename);
        if (file.exists()) {
            return new ImageIcon(file.getPath());
        } else {
            try {
                return new ImageIcon(ImageIO.read(getResourceAsStream(file.getPath())));
            } catch (IOException e) {
                throw new ResourceException(getText(Bundle.NAME, "exception.file.missing", filename));
            }
        }
    }

    /**
     * <p>getResourceAsFile.</p>
     *
     * @param filename a {@link java.lang.String} object.
     * @return a {@link java.io.File} object.
     */
    public static File getResourceAsFile(final String filename) {
        File file = createFile(filename);
        if (file.exists()) {
            return file;
        } else {
            URL resource = ResourceUtils.class.getResource(filename);
            if (null == resource) {
                resource = Thread.currentThread().getContextClassLoader().getResource(filename);
            }
            if (null != resource) {
                return new File(resource.getFile());
            }
            throw new ResourceException(getText(Bundle.NAME, "exception.file.missing", filename));
        }
    }

    /**
     * <p>getResourceAsStream.</p>
     *
     * @param filename a {@link java.lang.String} object.
     * @return a {@link java.io.InputStream} object.
     */
    public static InputStream getResourceAsStream(final String filename) {
        File file = createFile(filename);
        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new ResourceException(getText(Bundle.NAME, "exception.file.missing", filename));
            }
        } else {
            InputStream resource = ResourceUtils.class.getResourceAsStream(filename);
            if (null == resource) {
                resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            }
            if (null != resource) {
                return resource;
            }
            throw new ResourceException(getText(Bundle.NAME, "exception.file.missing", filename));
        }
    }

    private static String[] splitPath(String filename) {
        return filename.split("[\\\\/]");
    }

    private static File createFile(String filename) {
        File createdFile = null;
        for (String part : splitPath(filename)) {
            createdFile = new File(createdFile, part);
        }
        return createdFile;
    }

}
