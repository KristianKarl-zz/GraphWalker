package org.tigris.mbt.demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.openqa.selenium.server.SeleniumServer;
import org.tigris.mbt.Keywords;
import org.tigris.mbt.ModelBasedTesting;
import org.tigris.mbt.Util;
import org.tigris.mbt.exceptions.InvalidDataException;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class UC01 extends TestCase
{
	ModelBasedTesting mbt = new ModelBasedTesting();
    private Selenium browser;
	private Logger log;

	public void testUC01() 
	{
		log = Util.setupLogger( UC01.class );
		
		// Tell mbt which model to use
		mbt.enableExtended( true );
		
		// Tell mbt which model to use
		mbt.readGraph( "demo/Model/UC01.graphml" );
		
		// We want 100% edge coverage
		mbt.addCondition( Keywords.CONDITION_EDGE_COVERAGE, "100" );
		
		// Use random walk through the model
		mbt.setGenerator( Keywords.GENERATOR_RANDOM );
		
		// Execute using the methods available in this instance
		mbt.execute(this);
	}
	
	/**
	 * This method implements the Edge 'e_EnterBaseURL'
	 */
	public void e_EnterBaseURL()
	{
		log.info( "Edge: e_EnterBaseURL" );
		browser.open("http://www.amazon.com");
	}

	/**
	 * This method implements the Edge 'e_StartBrowser'
	 */
	public void e_StartBrowser()
	{
		log.info( "Edge: e_StartBrowser" );
		browser.start();
	}

	/**
	 * This method implements the Edge 'e_StopBrowser'
	 */
	public void e_StopBrowser()
	{
	  log.info( "Edge: e_StopBrowser" );
	  browser.stop();
	}

	/**
	 * This method implements the Edge 'e_SubversionLink'
	 */
	public void e_SubversionLink()
	{
	  log.info( "Edge: e_SubversionLink" );
	  browser.click( "link=Subversion" );
	}

	/**
	 * This method implements the Edge 'e_init'
	 */
	public void e_init() throws Exception
	{
		log.info( "Edge: e_init" );
		SeleniumServer selServer = new SeleniumServer();
		selServer.start();
		String url = "http://www.amazon.com";
		
		// For a linux machine with firefox installed in '/usr/lib/mozilla-firefox/'
		// where the firefox executable is not in the path.
		// See also: http://release.openqa.org/selenium-remote-control/0.9.2/doc/java/com/thoughtworks/selenium/DefaultSelenium.html#DefaultSelenium(java.lang.String,%20int,%20java.lang.String,%20java.lang.String)
		browser = new DefaultSelenium("localhost", SeleniumServer.getDefaultPort(), "*firefox /usr/lib/mozilla-firefox/firefox-bin", url);

		// Should work for any machine where the firefox executable is in the path.
		//browser = new DefaultSelenium("localhost", SeleniumServer.getDefaultPort(), "*firefox", url);
	}

	/**
	 * This method implements the Vertex 'v_BaseSubversionURL'
	 */
	public void v_BaseSubversionURL()
	{
	  log.info( "Vertex: v_BaseSubversionURL" );
	  browser.waitForPageToLoad( "10000" );
	  assertEquals( "mbt: Subversion", browser.getTitle() );
	}

	/**
	 * This method implements the Vertex 'v_BaseURL'
	 */
	public void v_BaseURL()
	{
	  log.info( "Vertex: v_BaseURL" );
	  browser.waitForPageToLoad( "10000" );
	  assertEquals( "Amazon.com: Online Shopping for Electronics, Apparel, Computers, Books, DVDs & more", browser.getTitle() );
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
		  title = browser.getTitle();
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
		  title = browser.getTitle();
		}
		catch ( Exception e)
		{
			return;
		}
		fail( "We should not have reach this code. Found an open browser: " + title );
	}
	
	/**
	 * This method implements the Edge 'e_AddBookToCart'
	 */
	public void e_AddBookToCart()
	{
	  log.info( "Edge: e_AddBookToCart" );
	  browser.click("submit.add-to-cart");
	}

	/**
	 * This method implements the Edge 'e_ClickBook'
	 */
	public void e_ClickBook()
	{
	  log.info( "Edge: e_ClickBook" );
	  browser.click("link=regexp:Practical Model-Based Testing: A Tools Approach");
	}

	/**
	 * This method implements the Edge 'e_SearchBook'
	 */
	public void e_SearchBook()
	{
	  log.info( "Edge: e_SearchBook" );
	  browser.type("twotabsearchtextbox", "model-based testing");
	  browser.click("navGoButtonPanel");
	}

	/**
	 * This method implements the Edge 'e_ShoppingCart'
	 */
	public void e_ShoppingCart()
	{
	  log.info( "Edge: e_ShoppingCart" );
	  browser.click("//img[@alt='Cart']");
	}

	/**
	 * This method implements the Vertex 'v_BookInformation'
	 */
	public void v_BookInformation()
	{
	  log.info( "Vertex: v_BookInformation" );
	  browser.waitForPageToLoad("30000");
	  String body = browser.getBodyText();
	  Pattern pattern = Pattern.compile( "Upgrade this book " );
	  Matcher matcher = pattern.matcher( body );
	  assertTrue( matcher.find() );
	  pattern = Pattern.compile( "Availability: " );
	  matcher = pattern.matcher( body );
	  assertTrue( matcher.find() );
	}

	/**
	 * This method implements the Vertex 'v_OtherBoughtBooks'
	 */
	public void v_OtherBoughtBooks()
	{
	  log.info( "Vertex: v_OtherBoughtBooks" );
	  browser.waitForPageToLoad("30000");
	  String body = browser.getBodyText();
	  Pattern pattern = Pattern.compile( "Customers who bought Practical Model-Based Testing: A Tools Approach also bought:" );
	  Matcher matcher = pattern.matcher( body );
	  assertTrue( matcher.find() );	  
	}

	/**
	 * This method implements the Vertex 'v_SearchResult'
	 */
	public void v_SearchResult()
	{
	  log.info( "Vertex: v_SearchResult" );
	  browser.waitForPageToLoad("30000");
	  String body = browser.getBodyText();
	  Pattern pattern = Pattern.compile( "Practical Model-Based Testing: A Tools Approach" );
	  Matcher matcher = pattern.matcher( body );
	  assertTrue( matcher.find() );
	}

	/**
	 * This method implements the Vertex 'v_ShoppingCart'
	 */
	public void v_ShoppingCart()
	{
	  log.info( "Vertex: v_ShoppingCart" );
	  browser.waitForPageToLoad("30000");
	  String expectedValue = "";
	  try {
		  expectedValue = mbt.getDataValue( "num_of_books" );
	  } catch (InvalidDataException e) {
		fail( e.getMessage() );
	  }
	  if ( expectedValue.equals("0") )
	  {
		  String body = browser.getBodyText();
		  Pattern pattern = Pattern.compile( "Your Shopping Cart is empty" );
		  Matcher matcher = pattern.matcher( body );
		  assertTrue( matcher.find() );
		  return;
	  }	  
	  String actualValue = browser.getValue("quantity.1");
	  assertTrue( actualValue.equals( expectedValue ) );
	}
}


