package org.graphwalker.generators;

import org.apache.log4j.Logger;
import org.graphwalker.Util;

public class FloydWarshallGenerator extends PathGenerator {
	static Logger logger = Util.setupLogger(FloydWarshallGenerator.class);

	public String[] getNext() {
		return null;
	}

}
