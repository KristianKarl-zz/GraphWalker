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

import javax.swing.*;
import java.io.File;
import java.net.URL;
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

    private static final String DEFAULT_BUNDLE = "core";
    
    private Resource() {
    }

    /**
     * <p>getText.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getText(@NotNull @MinLength(1) final String key) {
        return getText(DEFAULT_BUNDLE, key, Locale.getDefault());
    }    
    
    /**
     * <p>getText.</p>
     *
     * @param bundle a {@link java.lang.String} object.
     * @param key a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getText(@NotNull @MinLength(1) final String bundle, @NotNull @MinLength(1) final String key) {
        return getText(bundle, key, Locale.getDefault());
    }

    /**
     * <p>getText.</p>
     *
     * @param bundle a {@link java.lang.String} object.
     * @param key a {@link java.lang.String} object.
     * @param locale a {@link java.util.Locale} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getText(@NotNull @MinLength(1) final String bundle, @NotNull @MinLength(1) final String key, @NotNull final Locale locale) {
        return ResourceBundle.getBundle(bundle, locale).getString(key);
    }

    /**
     * <p>getIcon.</p>
     *
     * @param bundle a {@link java.lang.String} object.
     * @param key a {@link java.lang.String} object.
     * @return a {@link javax.swing.Icon} object.
     */
    public static Icon getIcon(@NotNull @MinLength(1) final String bundle, @NotNull @MinLength(1) final String key) {
        return new ImageIcon(getResource(getText(bundle, key)));
    }

    /**
     * <p>getIcon.</p>
     *
     * @param filename a {@link java.lang.String} object.
     * @return a {@link javax.swing.Icon} object.
     */
    public static Icon getIcon(@NotNull @MinLength(1) final String filename) {
        return new ImageIcon(getResource(filename));
    }

    /**
     * <p>getFile.</p>
     *
     * @param filename a {@link java.lang.String} object.
     * @return a {@link java.io.File} object.
     */
    public static File getFile(@NotNull @MinLength(1) final String filename) {
        File file = new File(filename);
        if (file.exists()) {
            return file;
        } else {
            return new File(getResource(filename).getFile());
        }
    }
    
    private static URL getResource(final String filename) {
        String filenameWithSeparator = filename;
        if (!filenameWithSeparator.startsWith(System.getProperty("file.separator"))) {
            filenameWithSeparator = System.getProperty("file.separator")+filename;
        }
        URL resource = Resource.class.getResource(filenameWithSeparator);
        if (null != resource) {
            return resource;
        }
        throw new ResourceException(Resource.getText("exception.file.missing"));
    }

}
