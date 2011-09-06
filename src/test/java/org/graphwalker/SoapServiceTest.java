package org.graphwalker;

import org.junit.Test;

public class SoapServiceTest {

	@Test(expected = RuntimeException.class)
	public void testGetDataValue() {
		SoapServices ss = new SoapServices(new ModelBasedTesting());
		ss.GetDataValue(null);
	}

}
