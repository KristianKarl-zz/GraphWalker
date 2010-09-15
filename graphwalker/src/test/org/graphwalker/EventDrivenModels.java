package test.org.graphwalker;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Random;

import org.apache.log4j.Logger;
import org.graphwalker.ModelBasedTesting;
import org.graphwalker.Util;
import org.graphwalker.exceptions.GeneratorException;
import org.graphwalker.exceptions.StopConditionException;
import org.jdom.JDOMException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EventDrivenModels {

	private static Logger logger = Util.setupLogger(EventDrivenModels.class);
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
		assertEquals(0, edm.getModels().size());

		Util u = new Util();

		edm.addModel(u.loadMbtFromXmlNonStatic("xml/test.org.graphwalker.unittest/org.graphwalker.EventDrivenModels.load2Models.1.xml"));
		assertEquals(1, edm.getModels().size());

		edm.addModel(u.loadMbtFromXmlNonStatic("xml/test.org.graphwalker.unittest/org.graphwalker.EventDrivenModels.load2Models.2.xml"));
		assertEquals(2, edm.getModels().size());
	}

	@Test
	public void runThreadeTest() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		TestTool tt = new TestTool();
		org.graphwalker.EventDrivenModels edm = new org.graphwalker.EventDrivenModels(tt);
		Util u = new Util();
		edm.addModel(u.loadMbtFromXmlNonStatic("xml/test.org.graphwalker.unittest/org.graphwalker.EventDrivenModels.runThreadeTest.A.xml"));
		edm.addModel(u.loadMbtFromXmlNonStatic("xml/test.org.graphwalker.unittest/org.graphwalker.EventDrivenModels.runThreadeTest.B.xml"));
		edm.addModel(u.loadMbtFromXmlNonStatic("xml/test.org.graphwalker.unittest/org.graphwalker.EventDrivenModels.runThreadeTest.C.xml"));
		
		
		Thread tc = new Thread( new TestClient(edm) );
		tc.setName("Test Client");
		tc.start();
		
		
		edm.switchModel("A");
		edm.waitToFinish();
	}
	
	public class TestClient implements Runnable {
		private org.graphwalker.EventDrivenModels edm;
		public TestClient(org.graphwalker.EventDrivenModels edm) {
	    this.edm = edm;
    }

		public void run() {
			try {
				logger.debug("In 5 seconds, I'm gonna trigger an event!");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			method_B();
		}
		
		public void method_A() {
			logger.debug("Calling modell A");
			edm.switchModel( "A" );
		}
		
		public void method_B() {
			logger.debug("Calling modell B");
			edm.switchModel( "B" );
		}
		
		public void method_C() {
			logger.debug("Calling modell C");
			edm.switchModel( "C" );
		}
	}

	public class TestTool {
		private Random rand = new Random();
		private void doWork() {
	    try {
	    	int sleep = rand.nextInt(3000);
				logger.debug("Will work for: " + sleep + " ms");
				Thread.sleep(rand.nextInt(sleep));
			} catch (InterruptedException e) {
	      Util.logStackTraceToError(e);
			}
    }
		public void A() {			
			logger.debug("Test Tool executing method A");
			doWork();
		}
		public void B() {			
			logger.debug("Test Tool executing method B");
			doWork();
		}
		public void C() {			
			logger.debug("Test Tool executing method C");
			doWork();
		}
		public void a() {			
			logger.debug("Test Tool executing method a");
			doWork();
		}
		public void b() {			
			logger.debug("Test Tool executing method a");
			doWork();
		}
		public void c() {			
			logger.debug("Test Tool executing method c");
			doWork();
		}
		public void A_1() {			
			logger.debug("Test Tool executing method A_1");
			doWork();
		}
		public void A_2() {			
			logger.debug("Test Tool executing method A_2");
			doWork();
		}
		public void A_3() {			
			logger.debug("Test Tool executing method A_3");
			doWork();
		}
		public void A_4() {			
			logger.debug("Test Tool executing method A_4");
			doWork();
		}
		public void A_5() {			
			logger.debug("Test Tool executing method A_5");
			doWork();
		}
		public void A_6() {			
			logger.debug("Test Tool executing method A_6");
			doWork();
		}
		public void B_1() {			
			logger.debug("Test Tool executing method B_1");
			doWork();
		}
		public void B_2() {			
			logger.debug("Test Tool executing method B_2");
			doWork();
		}
		public void B_3() {			
			logger.debug("Test Tool executing method B_3");
			doWork();
		}
		public void B_4() {			
			logger.debug("Test Tool executing method B_4");
			doWork();
		}
		public void B_5() {			
			logger.debug("Test Tool executing method B_5");
			doWork();
		}
		public void B_6() {			
			logger.debug("Test Tool executing method B_6");
			doWork();
		}
		public void C_1() {			
			logger.debug("Test Tool executing method C_1");
			doWork();
		}
		public void C_2() {			
			logger.debug("Test Tool executing method C_2");
			doWork();
		}
		public void C_3() {			
			logger.debug("Test Tool executing method C_3");
			doWork();
		}
		public void C_4() {			
			logger.debug("Test Tool executing method C_4");
			doWork();
		}
		public void C_5() {			
			logger.debug("Test Tool executing method C_5");
			doWork();
		}
		public void C_6() {			
			logger.debug("Test Tool executing method C_6");
			doWork();
		}
	}
}
