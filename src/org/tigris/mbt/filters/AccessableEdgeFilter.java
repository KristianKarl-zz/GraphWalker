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

import org.tigris.mbt.Keywords;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * @author Johan Tejle
 *
 */
public class AccessableEdgeFilter extends GeneralEdgeAcceptFilter implements EfficientFilter
{
	private Interpreter i;
	public AccessableEdgeFilter(Interpreter intepreter) {
		super();
		i = intepreter;
	}

	public boolean acceptEdge(Edge e) 
	{
		if ( !e.containsUserDatumKey(Keywords.GUARD_KEY) )
		{
			return true;
		}

		try 
		{
			return ((Boolean)i.eval((String)e.getUserDatum( Keywords.GUARD_KEY ))).booleanValue();
		} 
		catch (EvalError e1) 
		{
			e1.printStackTrace();
		}
		return false;
	}

	public String getName() {
		return "AccessableEdgeFilter";
	}

}
