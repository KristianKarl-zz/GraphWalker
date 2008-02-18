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

package org.tigris.mbt.exceptions;

/**
 * This exception is thrown during test sequence generation, when no out edge
 * is found from a given vertex. This could be due to a cul-de-sac, which may
 * or may not be perfectly all right.
 *
 */
public class FoundNoEdgeException extends Exception
{
	/**
	 * @param string
	 */
	public FoundNoEdgeException(String string) {
		super(string);
	}

	public FoundNoEdgeException(String string, Throwable e) {
		super(string, e);
	}

	private static final long serialVersionUID = -2597269122921601356L;
}
