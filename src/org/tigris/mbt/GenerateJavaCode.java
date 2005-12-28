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

import org.apache.log4j.*;

/**
 * @author Kristian Karl
 */
public class GenerateJavaCode {

	private static Logger _logger = Logger.getLogger( GenerateJavaCode.class );
	private static ModelBasedTesting _mtb;


	public static void main(String[] args)
	{
		BasicConfigurator.configure();
		_logger.setLevel( Level.INFO );

		if ( args.length < 2 )
		{
			_logger.warn( "Too few arguments" );
			displayHelpMessage();
			return;
		}

		try
		{
			_mtb = new ModelBasedTesting( args[ 0 ],
										  _logger );

			_mtb.generateJavaCode_XDE( args[ 1 ] );
		}
		catch ( RuntimeException e )
		{
			_logger.error( e.getMessage() );
		}
	}

	private static void displayHelpMessage()
	{
		System.out.println( "GenerateJavaCode <input file> <output file>" );
		System.out.println( "   input file is a graphml (yEd) formatted file." );
		System.out.println( "   ouput file is where the output is written to." );
	}
}
