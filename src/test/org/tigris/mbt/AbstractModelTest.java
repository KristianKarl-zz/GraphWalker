package test.org.tigris.mbt;

import org.tigris.mbt.AbstractModel;
import junit.framework.TestCase;

public class AbstractModelTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testAbstractModelDataStore1() 
	{
		AbstractModel.setDataStore("X", "true");
		assertEquals("true", AbstractModel.getDataStore("X"));
	}
	public void testAbstractModelDataStore2() 
	{
		AbstractModel.setDataStore("Y", "true");
		assertEquals("true", AbstractModel.getDataStore("Y"));
	}
	public void testAbstractModelDataStore3() 
	{
		AbstractModel.setDataStore("X", "true");
		AbstractModel.setDataStore("Y", "true");
		assertEquals("true", AbstractModel.getDataStore("X"));
		assertEquals("true", AbstractModel.getDataStore("Y"));
	}
	public void testAbstractModelDataStore4() 
	{
		AbstractModel.setDataStore("X", "1");
		AbstractModel.setDataStore("X", "2");
		AbstractModel.setDataStore("X", "3");
		AbstractModel.setDataStore("X", "4");
		assertEquals("4", AbstractModel.getDataStore("X"));
	}
	public void testAbstractModelDataStore5() 
	{
		AbstractModel.setDataStore("X", "true");
		assertEquals(true, AbstractModel.hasDataStore("X"));
	}
}
