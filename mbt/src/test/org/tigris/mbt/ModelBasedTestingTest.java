/**
 * 
 */
package test.org.tigris.mbt;

import org.tigris.mbt.ModelBasedTesting;
import org.tigris.mbt.Util;

import junit.framework.TestCase;

/**
 * @author Johan Tejle
 *
 */
public class ModelBasedTestingTest extends TestCase {

	public void testXmlLoading_simple()
	{
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init.xml");
		assertEquals(mbt.toString(), "RANDOM{EC>=100}");
	}

	public void testXmlLoading_moderate()
	{
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init2.xml");
		assertEquals(mbt.toString(), "RANDOM{((EC>=100 AND SC>=100) OR L=50)}");
	}

	public void testXmlLoading_advanced()
	{
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init3.xml");
		assertEquals(mbt.toString(), "RANDOM{EC>=10}\nRANDOM{(SC>=30 AND EC>=10)}");
	}

	public void testXmlLoading_requirementsAndStub()
	{
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init4.xml");
		assertEquals(mbt.toString(), "REQUIREMENTS\nCODE");
	}
}
