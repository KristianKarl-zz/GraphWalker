package org.tigris.mbt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Vector;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.tigris.mbt.GUI.App;
import org.tigris.mbt.exceptions.InvalidDataException;

@WebService

public class SoapServices {

	static Logger logger = Util.setupLogger( SoapServices.class );
	private Vector<String> stepPair = new Vector<String>();
	private String xmlFile = "";
	private boolean hardStop = false;

	public SoapServices() {
	}

	public SoapServices( String xmlFile ) {
		if ( xmlFile != null )
		{
			this.xmlFile = xmlFile; 
			Util.loadMbtAsWSFromXml( this.xmlFile );
		}
		Reset();
	}

	public boolean SetCurrentVertex( String newState ) {
		logger.debug( "SOAP service SetCurrentVertex recieving: " + newState );
		boolean value = ModelBasedTesting.getInstance().setCurrentVertex( newState );
		logger.debug( "SOAP service SetCurrentVertex returning: " + value );
		return value;
	}

	public String GetDataValue( String data ) {
		logger.debug( "SOAP service getDataValue recieving: " + data );
		String value = "";
		try {
			value = ModelBasedTesting.getInstance().getDataValue( data );
		} catch ( InvalidDataException e ) {
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
			value = ModelBasedTesting.getInstance().execAction( action );
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
			ModelBasedTesting.getInstance().passRequirement(true);
		else if ( pass.toUpperCase().equals("FALSE") )
			ModelBasedTesting.getInstance().passRequirement(false);
		else
			logger.error( "SOAP service PassRequirement dont know how to handle: " + pass +
					"\nOnly the strings true or false are permitted" );			
	}

	public String GetNextStep() {
		logger.debug( "SOAP service getNextStep" );
		try
		{
			String value = "";
			
			if ( !ModelBasedTesting.getInstance().hasNextStep() && ( stepPair.size() == 0 ) ) {
				return value;
			}
			
			if ( stepPair.size() == 0 ) {
				try {
					stepPair = new Vector<String>( Arrays.asList( ModelBasedTesting.getInstance().getNextStep() ) );
				}
				catch ( Exception e ) {
					hardStop = true;
					return "";
				}
			}
				
			value = (String) stepPair.remove(0);
			value = value.replaceAll( "/.*$", "");
			String addInfo = "";
			if ( ( stepPair.size() == 1 && ModelBasedTesting.getInstance().hasCurrentEdgeBackTracking() ) || 
				 ( stepPair.size() == 0 && ModelBasedTesting.getInstance().hasCurrentVertexBackTracking() ) ) {
				addInfo = " BACKTRACK";
			}
	
			if ( stepPair.size() == 1 ) {
				ModelBasedTesting.getInstance().logExecution( ModelBasedTesting.getInstance().getMachine().getLastEdge(), addInfo );
				if ( ModelBasedTesting.getInstance().isUseStatisticsManager() ) {
					ModelBasedTesting.getInstance().getStatisticsManager().addProgress( ModelBasedTesting.getInstance().getMachine().getLastEdge() );
				}
			}
			else {
				ModelBasedTesting.getInstance().logExecution( ModelBasedTesting.getInstance().getMachine().getCurrentState(), addInfo );
				if ( ModelBasedTesting.getInstance().isUseStatisticsManager() ) {
					ModelBasedTesting.getInstance().getStatisticsManager().addProgress( ModelBasedTesting.getInstance().getMachine().getCurrentState() );
				}
			}
			return value;
		}
		finally
		{
			if ( ModelBasedTesting.getInstance().isUseGUI() ) {
				App.getInstance().setButtons();					
			}
		}
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
			if ( stepPair.size() != 0 ) {
				value = true;
			}
			else {
				value = ModelBasedTesting.getInstance().hasNextStep();
			}
		}
		logger.debug( "SOAP service hasNextStep returning: " + value );
		if ( value == false )
			logger.info( ModelBasedTesting.getInstance().getStatisticsString() );
		return value;
	}

	public boolean Reload() {
		logger.debug( "SOAP service reload" );
		boolean retValue = true;
		try
		{
			Util.loadMbtAsWSFromXml( this.xmlFile );
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
		Reset();
		logger.debug( "SOAP service reload returning: " + retValue );
		return retValue;
	}

	public boolean Load( String xmlFile ) {
		logger.debug( "SOAP service load recieving: " + xmlFile );
		this.xmlFile = xmlFile; 
		boolean retValue = true;
		try
		{
			Util.loadMbtAsWSFromXml( this.xmlFile );
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
		Reset();
		logger.debug( "SOAP service load returning: " + retValue );
		return retValue;
	}

	public String GetStatistics() {
		logger.debug( "SOAP service getStatistics" );
		logger.debug( "SOAP service getStatistics returning: " + ModelBasedTesting.getInstance().getStatisticsString() );
		return ModelBasedTesting.getInstance().getStatisticsString();
	}
	
	private void Reset() {
		hardStop = false;
		stepPair.clear();		
	}
}
