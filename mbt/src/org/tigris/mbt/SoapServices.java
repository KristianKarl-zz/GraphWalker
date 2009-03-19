package org.tigris.mbt;

import java.io.PrintWriter;
import java.io.StringWriter;
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

	public SoapServices( String xmlFile ) {
		if ( xmlFile != null )
		{
			this.xmlFile = xmlFile; 
			mbt = Util.loadMbtAsWSFromXml( this.xmlFile );
		}
	}

	public String GetDataValue( String data ) {
		logger.debug( "SOAP service getDataValue recieving: " + data );
		String value = "";
		try {
			value = mbt.getDataValue( data );
		} catch (InvalidDataException e) {
			logger.error( e );
		}
		catch (Exception e) {
			logger.error( e );
		}
		logger.debug( "SOAP service getDataValue returning: " + value );
		return value;
	}

	public String ExecAction( String action ) {
		logger.debug( "SOAP service ExecAction recieving: " + action );
		String value = "";
		try {
			value = mbt.execAction( action );
		} catch (InvalidDataException e) {
			logger.error( e );
		}
		catch (Exception e) {
			logger.error( e );
		}
		logger.debug( "SOAP service ExecAction returning: " + value );
		return value;
	}

	public void PassRequirement( String pass ) {
		logger.debug( "SOAP service PassRequirement recieving: " + pass );
		if ( pass.toUpperCase().equals("TRUE") )
			mbt.passRequirement(true);
		else if ( pass.toUpperCase().equals("FALSE") )
			mbt.passRequirement(false);
		else
			logger.error( "SOAP service PassRequirement dont know how to handle: " + pass +
					"\nOnly the strings true or false are permitted" );
			
	}

	public String GetNextStep() {
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

	public boolean HasNextStep() {
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

	public boolean Reload() {
		logger.debug( "SOAP service reload" );
		mbt = null;
		boolean retValue = true;
		try
		{
			mbt = Util.loadMbtAsWSFromXml( this.xmlFile );
		}
		catch ( Exception e )
		{			
			StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter( sw );
		    e.printStackTrace( pw );
		    pw.close();	    		    
			logger.error( sw.toString() );
    		System.err.println( e.getMessage() );
    		retValue = false;	
		}
		hardStop = false;
		logger.debug( "SOAP service reload returning: " + retValue );
		return retValue;
	}

	public boolean Load( String xmlFile ) {
		logger.debug( "SOAP service load recieving: " + xmlFile );
		this.xmlFile = xmlFile; 
		mbt = null;
		boolean retValue = true;
		try
		{
			mbt = Util.loadMbtAsWSFromXml( this.xmlFile );
		}
		catch ( Exception e )
		{			
			StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter( sw );
		    e.printStackTrace( pw );
		    pw.close();	    		    
			logger.error( sw.toString() );
    		System.err.println( e.getMessage() );
    		retValue = false;	
		}
		hardStop = false;
		logger.debug( "SOAP service load returning: " + retValue );
		return retValue;
	}

	public String GetStatistics() {
		logger.debug( "SOAP service getStatistics" );
		logger.debug( "SOAP service getStatistics returning: " + mbt.getStatisticsString() );
		return mbt.getStatisticsString();
	}
}
