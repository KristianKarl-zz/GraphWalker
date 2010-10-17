package org.graphwalker.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseLog {

	public static class LoggedItem {
		public Integer index = null;
		public String data = null;
	}
	
	public static Vector<LoggedItem> readLog(File logFile) throws IOException {
		Vector<LoggedItem> list = new Vector<LoggedItem>();
		
		BufferedReader file = new BufferedReader(new FileReader(logFile));
		String line = file.readLine();
	  while ( line != null ) {
			Pattern p = Pattern.compile("INFO  - (Vertex|Edge): '(\\S*)', INDEX=(\\d+) ");
			Matcher m = p.matcher(line);
			if (m.find()) {
				LoggedItem item = new LoggedItem();
				item.index = new Integer( m.group(3) );
				if (m.group(1).equals("Vertex")) {
					p = Pattern.compile("DATA: (.*)");
					m = p.matcher(line);
					if (m.find()) {
						item.data = m.group(1);
					}					
				}
				list.add(item);
			}
			line = file.readLine();
	  }
		
	  return list;
  }
}
