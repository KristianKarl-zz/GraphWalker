package org.tigris.mbt;

import java.util.Arrays;
import java.util.Vector;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.tigris.mbt.exceptions.InvalidDataException;

@WebService

public class SoapServices {

	private ModelBasedTesting mbt = null;
	static Logger logger = Util.setupLogger( ModelBasedTesting.class );
	private Vector stepPair = new Vector();
	private String xmlFile = "";
	private boolean hardStop = false;

	public SoapServices() {
	}

	public SoapServices( String xmlFile) {
		this.xmlFile = xmlFile; 
		mbt = Util.loadMbtFromXml( this.xmlFile, true );
	}

	public String getDataValue( String data ) {
		logger.debug( "SOAP service getDataValue recieving: " + data );
		String value = "";
		try {
			value = mbt.getDataValue( data );
		} catch (InvalidDataException e) {
			logger.error( e );
		}
		logger.debug( "SOAP service getDataValue returning: " + value );
		return value;
	}

	public String getNextStep() {
		logger.debug( "SOAP service getNextStep" );
		String value = "";
		
		if( !mbt.hasNextStep() && ( stepPair.size() == 0 ) )
			return value;
		if( stepPair.size() == 0 )
		{
			try {
				stepPair = new Vector( Arrays.asList( mbt.getNextStep() ) );
			} catch (Exception e) {
				hardStop = true;
				return "";
			}
		}
			
		value = (String) stepPair.remove(0);
		value = value.replaceAll( "/.*$", "");
		String addInfo = "";
		if( ( stepPair.size() == 1 && mbt.hasCurrentEdgeBackTracking() ) || 
			( stepPair.size() == 0 && mbt.hasCurrentVertexBackTracking() ) )
		{
			addInfo = " BACKTRACK";
		}

		if ( stepPair.size() == 1 )
		{
			mbt.logExecution( mbt.getMachine().getLastEdge(), addInfo );
			mbt.getStatisticsManager().addProgress( mbt.getMachine().getLastEdge() );
		}
		else
		{
			mbt.logExecution( mbt.getMachine().getCurrentState(), addInfo );
			mbt.getStatisticsManager().addProgress( mbt.getMachine().getCurrentState() );
		}
		return value;
	}

	public boolean hasNextStep() {
		logger.debug( "SOAP service hasNextStep" );
		boolean value = false;
		if ( hardStop )
		{
			value = false;
		}
		else
		{
			value = mbt.hasNextStep();
		}
		logger.debug( "SOAP service hasNextStep returning: " + value );
		if ( value == false )
			logger.info( mbt.getStatisticsString() );
		return value;
	}

	public void reload() {
		logger.debug( "SOAP service reload" );
		mbt = null;
		mbt = Util.loadMbtFromXml( this.xmlFile, true );
		hardStop = false;
		logger.debug( "SOAP service reload returning" );
	}

	public String getStatistics() {
		logger.debug( "SOAP service getStatistics" );
		logger.debug( "SOAP service getStatistics returning: " + mbt.getStatisticsString() );
		return mbt.getStatisticsString();
	}
}
