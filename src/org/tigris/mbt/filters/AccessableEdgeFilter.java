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

package org.tigris.mbt.filters;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.filters.EfficientFilter;
import edu.uci.ics.jung.graph.filters.GeneralEdgeAcceptFilter;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Johan Tejle
 *
 */
public class AccessableEdgeFilter extends GeneralEdgeAcceptFilter implements EfficientFilter
{
	private Hashtable datastore;
	public AccessableEdgeFilter(Hashtable modelDatastore) {
		super();
		datastore = modelDatastore;
	}

	public boolean acceptEdge(Edge e) {
		String label = (String)e.getUserDatum( "label" );
		Pattern p = Pattern.compile("\\[(.*)\\]", Pattern.MULTILINE);
		Matcher m = p.matcher(label);
		if(!m.find()) return true;
		String guard = m.group(1);
		return evaluate(guard);
	}

	private boolean evaluate(String guard) 
	{
		if(guard.equalsIgnoreCase("true")) return true;
		if(guard.equalsIgnoreCase("false")) return false;
		
		if(guard.indexOf('=')>=0)
		{
			String[] array = guard.split("=");
			return evaluate(array[0],"=",array[1]);
		}
		else if(guard.indexOf('>')>=0)
		{
			String[] array = guard.split(">");
			return evaluate(array[0],">",array[1]);
		}
		else if(guard.indexOf('<')>=0)
		{
			String[] array = guard.split("<");
			return evaluate(array[0],"<",array[1]);
		}
		if(datastore.containsKey(guard))
			return evaluate((String) datastore.get(guard));
		return false;
	}

	private boolean evaluate(String leftSide, String comparrisson, String rightSide) 
	{
		if(datastore.containsKey(leftSide)) 
		{
			leftSide = (String) datastore.get(leftSide);
		}
		if(datastore.containsKey(rightSide))
		{
			rightSide = (String) datastore.get(rightSide);
		}
		if(comparrisson.equals("="))
		{
			return leftSide.equals(rightSide);
		}
		if(comparrisson.equals(">"))
		{
			return Double.compare(Double.parseDouble(leftSide),Double.parseDouble(rightSide)) > 0;
		}
		if(comparrisson.equals("<"))
		{
			return Double.compare(Double.parseDouble(leftSide),Double.parseDouble(rightSide)) < 0;
		}
		return false;
	}

	public String getName() {
		return "AccessableEdgeFilter";
	}

}
