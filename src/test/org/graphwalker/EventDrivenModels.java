package test.org.graphwalker;


import static org.junit.Assert.*;

import java.io.IOException;

import org.graphwalker.ModelBasedTesting;
import org.graphwalker.Util;
import org.graphwalker.exceptions.GeneratorException;
import org.graphwalker.exceptions.StopConditionException;
import org.jdom.JDOMException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EventDrivenModels {

	ModelBasedTesting mbt = null;

	@Before
	public void setUp() throws Exception {
		mbt = ModelBasedTesting.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void constructor() {
		org.graphwalker.EventDrivenModels models = new org.graphwalker.EventDrivenModels();
		assertTrue(models != null);
	}

	@Test
	public void load2Models() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		org.graphwalker.EventDrivenModels edm = new org.graphwalker.EventDrivenModels();
		assertEquals(edm.getModels().size(), 0);

		Util u = new Util();
		edm.addModel(u.loadMbtFromXmlNonStatic("xml/test.org.graphwalker.unittest/org.graphwalker.EventDrivenModels.load2Models.1.xml"));
		assertEquals(edm.getModels().size(), 1);
		
		edm.addModel(u.loadMbtFromXmlNonStatic("xml/test.org.graphwalker.unittest/org.graphwalker.EventDrivenModels.load2Models.2.xml"));
		assertEquals(edm.getModels().size(), 2);
		
		ModelBasedTesting a = edm.getModels().get(0); 
		ModelBasedTesting b = edm.getModels().get(1);
		ModelBasedTesting c = ModelBasedTesting.getInstance();
		
		assertNotSame(a, b);		
		assertNotSame(a, c);		
		assertNotSame(b, c);
		assertSame(mbt, c);
	}
}
