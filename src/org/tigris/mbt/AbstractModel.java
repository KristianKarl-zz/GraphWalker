//This file is part of the Model-based Testing java package
//Copyright (C) 2005  Kristian Karl
//
//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

package org.tigris.mbt;

import java.util.Hashtable;

/**
 * @author Johan Tejle
 *
 */
public class AbstractModel {

	public static String  START_NODE                  = "Start";
	public static String  STOP_NODE                   = "Stop";
	public static String  ID_KEY                      = "id";
	public static String  IMAGE_KEY                   = "image";
	public static String  WIDTH_KEY                   = "width";
	public static String  HEIGHT_KEY                  = "height";
	public static String  FILE_KEY                    = "file";
	public static String  LABEL_KEY                   = "label";
	public static String  FULL_LABEL_KEY              = "full_label";
	public static String  VISITED_KEY                 = "visited";
	public static String  WEIGHT_KEY                  = "weight";
	public static String  STATE_KEY                   = "state";
	public static String  CONDITION_KEY               = "condition";
	public static String  VARIABLE_KEY                = "variable";
	public static String  INDEX_KEY                   = "index";
	public static String  MERGE	                  	  = "merge";
	public static String  NO_MERGE	          	  	  = "no merge";
	public static String  MERGED_BY_MBT	          	  = "merged by mbt";
	public static String  MOTHER_GRAPH_START_VERTEX   = "mother graph start vertex";
	public static String  SUBGRAPH_START_VERTEX       = "subgraph start vertex";
	public static String  BLOCKED	                  = "BLOCKED";
	public static String  BACKTRACK	                  = "BACKTRACK";

	private static Hashtable _dataStore = new Hashtable();
	
	public static String getDataStore(String key) {
		return (String)_dataStore.get(key);
	}

	public static void setDataStore(String key, String value) {
		_dataStore.put(key, value);
	}

	public static boolean hasDataStore(String key)
	{
		return _dataStore.containsKey(key);		
	}
	
	AbstractModel()
	{
		//TODO implement basic functionality for handling directed sparse graphs.
	}

}
