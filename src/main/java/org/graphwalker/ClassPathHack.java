package org.graphwalker;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassPathHack {
  private static final Class<?>[] parameters = new Class[] {URL.class};

  @SuppressWarnings("deprecation")
  public static void addFile(File f) throws IOException {
    // f.toURL is deprecated
    addURL(f.toURL());
  }

  protected static void addURL(URL u) throws IOException {
    URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
    Class<?> sysclass = URLClassLoader.class;

    try {
      Method method = sysclass.getDeclaredMethod("addURL", parameters);
      method.setAccessible(true);
      method.invoke(sysloader, u);
    } catch (Exception e) {
      Util.logStackTraceToError(e);
      throw new IOException("Error, could not add URL to system classloader");
    }

  }
}
