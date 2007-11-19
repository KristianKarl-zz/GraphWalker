package org.tigris.mbt.demo;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.SeleniumServer;
import org.tigris.mbt.Keywords;
import org.tigris.mbt.ModelBasedTesting;
import org.tigris.mbt.Util;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class UC01 extends TestCase
{
    private Selenium selenium;
	private Logger log;

	public void testUC01()
	{
		log = Util.setupLogger( UC01.class );

		ModelBasedTesting mbt = new ModelBasedTesting();
		
		// Tell mbt which model to use
		mbt.readGraph( "demo/Model/UC01.graphml" );
		
		// We want 100% edge coverage
		mbt.addCondition( Keywords.CONDITION_EDGE_COVERAGE, "100" );
		
		// Use random walk through the model
		mbt.setGenerator( Keywords.GENERATOR_RANDOM );
		
		while( mbt.hasNextStep() )
		{
			String[] stepPair = mbt.getNextStep();
			
			if ( Util.RunTest( this, log, stepPair[ 0 ] ) == false ) fail();
			if ( Util.RunTest( this, log, stepPair[ 1 ] ) == false ) fail();				
		}
	}
	
	/**
	 * This method implements the Edge 'e_DirectLink'
	 */
	public void e_DirectLink()
	{
	  log.info( "Edge: e_DirectLink" );
	  selenium.open("http://mbt.tigris.org/source/browse/mbt/branches/1.15/mbt/.classpath?view=markup");
	}

	/**
	 * This method implements the Edge 'e_EnterBaseURL'
	 */
	public void e_EnterBaseURL()
	{
		log.info( "Edge: e_EnterBaseURL" );
		selenium.open("http://mbt.tigris.org");
	}

	/**
	 * This method implements the Edge 'e_StartBrowser'
	 */
	public void e_StartBrowser()
	{
		log.info( "Edge: e_StartBrowser" );
		selenium.start();
	}

	/**
	 * This method implements the Edge 'e_StopBrowser'
	 */
	public void e_StopBrowser()
	{
	  log.info( "Edge: e_StopBrowser" );
	  selenium.stop();
	}

	/**
	 * This method implements the Edge 'e_SubversionLink'
	 */
	public void e_SubversionLink()
	{
	  log.info( "Edge: e_SubversionLink" );
	  selenium.click( "link=Subversion" );
	}

	/**
	 * This method implements the Edge 'e_init'
	 */
	public void e_init() throws Exception
	{
		log.info( "Edge: e_init" );
		SeleniumServer selServer = new SeleniumServer();
		selServer.start();
		String url = "http://mbt.tigris.org";
		selenium = new DefaultSelenium("localhost", SeleniumServer.getDefaultPort(), "*firefox", url);
	}

	/**
	 * This method implements the Vertex 'v_BaseSubversionURL'
	 */
	public void v_BaseSubversionURL()
	{
	  log.info( "Vertex: v_BaseSubversionURL" );
	  selenium.waitForPageToLoad( "10000" );
	  assertEquals( "mbt: Subversion", selenium.getTitle() );
	}

	/**
	 * This method implements the Vertex 'v_BaseURL'
	 */
	public void v_BaseURL()
	{
	  log.info( "Vertex: v_BaseURL" );
	  selenium.waitForPageToLoad( "10000" );
	  assertEquals( "mbt.tigris.org", selenium.getTitle() );
	}

	/**
	 * This method implements the Vertex 'v_BrowserStarted'
	 */
	public void v_BrowserStarted()
	{
		log.info( "Vertex: v_BrowserStarted" );
		String title = null;
		try
		{
		  title = selenium.getTitle();
		}
		catch ( Exception e)
		{
			fail( "Found no open browser" );
		}
		log.info( "Found browser with title: " + title );
	}

	/**
	 * This method implements the Vertex 'v_BrowserStopped'
	 */
	public void v_BrowserStopped()
	{
		log.info( "Vertex: v_BrowserStopped" );
		String title = null;
		try
		{
		  title = selenium.getTitle();
		}
		catch ( Exception e)
		{
			return;
		}
		fail( "We should not have reach this code. Found an open browser: " + title );
	}
}
