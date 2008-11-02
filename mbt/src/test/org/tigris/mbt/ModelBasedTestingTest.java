/**
 * 
 */
package test.org.tigris.mbt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.tigris.mbt.ModelBasedTesting;
import org.tigris.mbt.Util;
import org.tigris.mbt.exceptions.InvalidDataException;

import junit.framework.TestCase;

/**
 * @author Johan Tejle
 *
 */
public class ModelBasedTestingTest extends TestCase {

	private InputStream redirectIn()
	{
		return new InputStream() {
			public int read() throws IOException {
				try {
					Thread.sleep( 100 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return '0';
			}
		};
	}
	
	public void testXmlLoading_Simple()
	{
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init.xml", false, false);
		assertEquals("RANDOM{EC>=100}", mbt.toString());
	}

	public void testXmlLoading_Moderate()
	{
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init2.xml",false, false);
		assertEquals("RANDOM{((EC>=100 AND SC>=100) OR L=50)}", mbt.toString());
	}

	public void testXmlLoading_Advanced()
	{
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init3.xml",false, false);
		assertEquals("RANDOM{EC>=10}\nRANDOM{(SC>=30 AND EC>=10)}", mbt.toString());
	}

	public void testXmlLoading_OfflineStub()
	{
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init4.xml",false, false);
		assertEquals("CODE", mbt.toString());
		File f = new File("mbt_init4.java");
		assertTrue(f.exists());
		assertTrue(f.delete());
		assertFalse(f.exists());
	}

	public void testXmlLoading_JavaExecution()
	{
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init5.xml",false, false);
		assertEquals("RANDOM{SC>=40}", mbt.toString());
	}

	public void testXmlLoading_OfflineRequirements()
	{
		PrintStream oldOut = System.out;
		ByteArrayOutputStream innerOut = new ByteArrayOutputStream();

		System.setOut( new PrintStream(innerOut) );
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init6.xml",false, false);
		System.setOut( oldOut );

		assertEquals("REQUIREMENTS", mbt.toString());
		assertEquals(6, innerOut.toString().trim().split("\r\n|\r|\n").length);
	}
	
	public void testXmlLoading_OnlineRequirements()
	{
		InputStream oldIn = System.in;
		PrintStream oldOut = System.out;
		ByteArrayOutputStream innerOut = new ByteArrayOutputStream();

		System.setOut( new PrintStream(innerOut) );
		System.setIn( redirectIn() );
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init7.xml",false, false);
		System.setIn( oldIn );
		System.setOut( oldOut );

		assertEquals("REQUIREMENTS", mbt.toString());
		assertEquals(11, innerOut.toString().trim().split("\r\n|\r|\n").length);
	}
	
	public void testGetdataValue() throws InvalidDataException
	{
		InputStream oldIn = System.in;
		PrintStream oldOut = System.out;
		ByteArrayOutputStream innerOut = new ByteArrayOutputStream();

		System.setOut( new PrintStream(innerOut) );
		System.setIn( redirectIn() );
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/getDataValue/getDataValue.xml",false, false);
		System.setIn( oldIn );
		System.setOut( oldOut );
		
		System.out.print(mbt.getDataValue("v"));

		assertEquals("[Hello,  , world]", mbt.getDataValue("v"));
	}
	
}
