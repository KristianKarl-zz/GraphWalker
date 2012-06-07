// This file is part of the GraphWalker java package
// The MIT License
//
// Copyright (c) 2010 graphwalker.org
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package org.graphwalker.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphwalker.Util;

public class ParseLog {

  public static class LoggedItem {
    public Integer index = null;
    public String data = null;
  }

  public static Vector<LoggedItem> readLog(File logFile) throws IOException {
    Vector<LoggedItem> list = new Vector<LoggedItem>();
    FileReader reader = null;
    try {
      reader = new FileReader(logFile);
      BufferedReader file = new BufferedReader(reader);
      String line;
      while ((line = file.readLine()) != null) {
        Pattern p = Pattern.compile("INFO  - (Vertex|Edge): '(\\S*)', INDEX=(\\d+) ");
        Matcher m = p.matcher(line);
        if (m.find()) {
          LoggedItem item = new LoggedItem();
          item.index = Integer.valueOf(m.group(3));
          if (m.group(1).equals("Vertex")) {
            p = Pattern.compile("DATA: (.*)");
            m = p.matcher(line);
            if (m.find()) {
              item.data = m.group(1);
            }
          }
          list.add(item);
        }
      }
      file.close();
    } catch (IOException e) {
      throw e; // TODO:
    } finally {
      Util.closeQuietly(reader);
    }
    return list;
  }
}
