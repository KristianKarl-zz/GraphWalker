package org.graphwalker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.graphwalker.ModelBasedTesting;
import org.graphwalker.MultipleModels;
import org.graphwalker.Util;
import org.graphwalker.exceptions.GeneratorException;
import org.graphwalker.exceptions.StopConditionException;
import org.jdom.JDOMException;
import org.junit.Test;

public class MultipleModelsTest {
	
	@Test
	public void testGetInstance() {
		MultipleModels a = MultipleModels.getInstance();		
		MultipleModels b = MultipleModels.getInstance();

    a.reset();
    b.reset();
		
		assertNotNull(a);
		assertSame(a, b);
	}
	
	@Test
	public void testGetUniqueName() {
    MultipleModels mmInstance = MultipleModels.getInstance();
    mmInstance.reset();
		
		String name = "A";
		mmInstance.getUniqueName("A");
		assertFalse(name.equals(mmInstance.getUniqueName(name)));
	}
	
	@Test
	public void testNameFormat() {
		MultipleModels mmInstance = MultipleModels.getInstance();
    mmInstance.reset();

    String desiredName = "My Model";
		assertEquals("My Model", mmInstance.getUniqueName(desiredName));
		assertEquals("My Model 1", mmInstance.getUniqueName(desiredName));
	}
	
	@Test
	public void testMaxNames() {
		MultipleModels mmInstance = MultipleModels.getInstance();
    mmInstance.reset();
		
		String name = "Max Name";
		for (int i = 0; i < 1000; i++) {
			mmInstance.getUniqueName(name);
		}
	}

	@Test(expected=IndexOutOfBoundsException.class)
	public void testMaxNamesBounds() {
		MultipleModels mmInstance = MultipleModels.getInstance();
    mmInstance.reset();
		
		String name = "Bound Name";
		for (int i = 0; i < 1001; i++) {
			mmInstance.getUniqueName(name);
		}
	}

	@Test
	public void testAddAndGetModel() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		MultipleModels mmInstance = MultipleModels.getInstance();
    mmInstance.reset();

    ModelBasedTesting model = Util.getNewMbtFromXml(Util.getFile("xml/multipleA.xml"));
		String modelName = mmInstance.getUniqueName("A");
		
		mmInstance.addModel(modelName, model);
		assertEquals(model, mmInstance.getModel(modelName));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddSameModelAgain() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		MultipleModels mmInstance = MultipleModels.getInstance();
    mmInstance.reset();

    ModelBasedTesting model1 = Util.getNewMbtFromXml(Util.getFile("xml/multipleA.xml"));
		ModelBasedTesting model2 = Util.getNewMbtFromXml(Util.getFile("xml/multipleA.xml"));

		String modelName = mmInstance.getUniqueName("A");
		
		mmInstance.addModel(modelName, model1);
		assertEquals(model1, mmInstance.getModel(modelName));
		mmInstance.addModel(modelName, model2);
	}

	@Test
	public void testAddNonReservedName() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		MultipleModels mmInstance = MultipleModels.getInstance();
    mmInstance.reset();

    ModelBasedTesting model = Util.getNewMbtFromXml(Util.getFile("xml/multipleA.xml"));
		String modelName = "Non reserved";
		mmInstance.addModel(modelName, model);
		assertTrue(!modelName.equals(mmInstance.getUniqueName(modelName)));
	}

	@Test
	public void testExecuteModel() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		MultipleModels mmInstance = MultipleModels.getInstance();
    mmInstance.reset();

    ModelBasedTesting model = Util.getNewMbtFromXml(Util.getFile("xml/multipleB.xml"));
		String modelName = mmInstance.getUniqueName("B");
		
		mmInstance.addModel(modelName, model);
		assertTrue(model.hasNextStep());
		
		mmInstance.executeModel(modelName, new ModelAPI(modelName));
		assertTrue(mmInstance.isExecuting(modelName));
		Thread.sleep(1000);
		assertFalse(model.hasNextStep());
		assertFalse(mmInstance.isExecuting(modelName));
		assertTrue(mmInstance.isFinished(modelName));
	}

	@Test(expected=IllegalStateException.class)
	public void testExecuteModelTwice() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		MultipleModels mmInstance = MultipleModels.getInstance();
    mmInstance.reset();

    ModelBasedTesting model = Util.getNewMbtFromXml(Util.getFile("xml/multipleB.xml"));
		String modelName = mmInstance.getUniqueName("B");
		
		mmInstance.addModel(modelName, model);
		mmInstance.executeModel(modelName, new ModelAPI(modelName));
		mmInstance.executeModel(modelName, new ModelAPI(modelName));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testExecuteNonAddedModel() {
		MultipleModels mmInstance = MultipleModels.getInstance();
    mmInstance.reset();
    
		String modelName = mmInstance.getUniqueName("Non-existing");		
		mmInstance.executeModel(modelName, new ModelAPI(modelName));
	}

	@Test
	public void testMultipleModels() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		MultipleModels mmInstance = MultipleModels.getInstance();
    mmInstance.reset();

    ModelBasedTesting model1 = Util.getNewMbtFromXml(Util.getFile("xml/multipleA.xml"));
		ModelBasedTesting model2 = Util.getNewMbtFromXml(Util.getFile("xml/multipleA.xml"));
		String model1Name = mmInstance.getUniqueName("A");
		String model2Name = mmInstance.getUniqueName("A");
		
		mmInstance.addModel(model1Name, model1);
		mmInstance.addModel(model2Name, model2);
		
		assertNotNull(mmInstance.getModel(model1Name));
		assertNotNull(mmInstance.getModel(model2Name));
	}
	
	@Test
	public void testSpawnMultipleModels() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		MultipleModels mmInstance = MultipleModels.getInstance();
    mmInstance.reset();

    ModelBasedTesting model  = Util.getNewMbtFromXml(Util.getFile("xml/multipleA.xml"));
		String modelName = mmInstance.getUniqueName("A");
		String spawnedModelName = mmInstance.getUniqueName("B");

		mmInstance.addModel(modelName, model);
		assertNull(mmInstance.getModel(spawnedModelName));
		mmInstance.executeModel(modelName, new ModelAPI(modelName, spawnedModelName, true, false));
		Thread.sleep(1000);
		assertNotNull(mmInstance.getModel(spawnedModelName));
	}
	
	@Test
	public void testPausedMultipleModels() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException, ExecutionException, TimeoutException {
		MultipleModels mmInstance = MultipleModels.getInstance();
    mmInstance.reset();

    ModelBasedTesting model  = Util.getNewMbtFromXml(Util.getFile("xml/multipleA.xml"));
		String modelName = mmInstance.getUniqueName("A");
		String spawnedModelName = mmInstance.getUniqueName("B");

		mmInstance.addModel(modelName, model);
		assertNull(mmInstance.getModel(spawnedModelName));
		mmInstance.executeModel(modelName, new ModelAPI(modelName, spawnedModelName, true, true));
		Thread.sleep(1000);
		model.getFuture().get(2, TimeUnit.SECONDS);
		assertTrue(model.getFuture().isDone());
		Thread.sleep(1000);
		ModelBasedTesting spawnedModel = mmInstance.getModel(spawnedModelName);
		assertNotNull(spawnedModel);
		spawnedModel.getFuture().get(2, TimeUnit.SECONDS);
		assertTrue(spawnedModel.getFuture().isDone());
	}
	
	@Test
	public void testGetStatistics() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		MultipleModels mmInstance = MultipleModels.getInstance();
    mmInstance.reset();

    ModelBasedTesting model = Util.getNewMbtFromXml(Util.getFile("xml/multipleB.xml"));
		String modelName = mmInstance.getUniqueName("B");
		
		mmInstance.addModel(modelName, model);
		mmInstance.executeModel(modelName, new ModelAPI(modelName));
		String actualResult = mmInstance.getStatistics();
		assertTrue(mmInstance.isFinished());
		assertFalse(model.hasNextStep());
		assertFalse(mmInstance.isExecuting(modelName));
		assertTrue(actualResult.contains(modelName));
	}

	@Test
	public void testGetStatisticsMultipleModels() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		MultipleModels mmInstance = MultipleModels.getInstance();
    mmInstance.reset();

    ModelBasedTesting model  = Util.getNewMbtFromXml(Util.getFile("xml/multipleA.xml"));
		String modelName = mmInstance.getUniqueName("A");
		String spawnedModelName = mmInstance.getUniqueName("B");

		mmInstance.addModel(modelName, model);
		mmInstance.executeModel(modelName, new ModelAPI(modelName, spawnedModelName, true, false));
		
		String actualResult = mmInstance.getStatistics();
		System.out.println(actualResult);

		assertTrue(actualResult.contains(modelName));
		assertTrue(actualResult.contains(spawnedModelName));
	}

	public class ModelAPI {
		private String modelName;
		private String spawnedModelName;
		private boolean spawn;
		private boolean pause;
		
		public ModelAPI(String modelName) {
			this(modelName, "", false, false);
		}
		
		public ModelAPI(String modelName, String spawnedModelName, boolean spawn, boolean pause) {
			this.modelName = modelName;
			this.spawnedModelName = spawnedModelName;
			this.spawn = spawn;
			this.pause = pause;
		}
		
		public void a() {
//			System.out.println("Execute edge: a");
		}

		public void b() {
			if (pause) {
				ModelBasedTesting model = MultipleModels.getInstance().getModel(modelName);
				model.setFuture(new FutureTaskImpl("b is finished for model " + modelName, 1, TimeUnit.SECONDS));
			}
			
		}

		public void c() {
//			System.out.println("Execute edge: c");
			
		}

		public void d() {
//			System.out.println("Execute edge: d");
			
		}

		public void e() {
//			System.out.println("Execute edge: e");
			
		}

		public void f() {
//			System.out.println("Execute edge: f");
			
		}
		
		// Vertices
		public void A() {
//			System.out.println("Execute vertex: A for model " + modelName);
			
		}

		public void B() {
//			System.out.println("Execute vertex: B");
			
		}

		public void C() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
			if (spawn) {
				MultipleModels mmInstance = MultipleModels.getInstance();
				ModelBasedTesting modelB = Util.getNewMbtFromXml(Util.getFile("xml/multipleB.xml"));			
				
				mmInstance.addModel(spawnedModelName, modelB);
				
				if (pause) {
					mmInstance.executeModel(spawnedModelName, new ModelAPI(spawnedModelName, "", false, true));
				} else {
					mmInstance.executeModel(spawnedModelName, new ModelAPI(spawnedModelName));
					
				}
			}
			
		}

		public void D() {
//			System.out.println("Execute vertex: D");
			
		}

		public void E() {
//			System.out.println("Execute vertex: E");
			
		}

		public void F() {

		}
	}

	public class FutureTaskImpl implements Future<String> {
		private String result;
		private long createdTime;
		private long computationTime;
		private boolean isDone;
		private boolean isCancelled;
		
		public FutureTaskImpl(String desiredResult, long computationTime, TimeUnit unit) {
			result = desiredResult;
			createdTime = System.currentTimeMillis();
			this.computationTime = TimeUnit.MILLISECONDS.convert(computationTime, unit);
			isDone = false;
			isCancelled = false;
		}
		
		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			if (isDone) {
				return false;
			} else {
				isCancelled = true;
				isDone = true;
				return true;
			}
		}

		@Override
		public String get() throws InterruptedException, ExecutionException {
			while(!isDone()) {
				Thread.sleep(10);
			}
			return result;
		}

		@Override
		public String get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			long startedTime = System.currentTimeMillis();
			while(!isDone()) {
				if (System.currentTimeMillis() >= startedTime + TimeUnit.MILLISECONDS.convert(timeout, unit)) {
					throw new TimeoutException("Future task timed out.");
				}
				Thread.sleep(10);
			}
			return result;
		}

		@Override
		public boolean isCancelled() {
			return isCancelled;
		}

		@Override
		public boolean isDone() {
			if (!isDone) {
				if (System.currentTimeMillis() >= createdTime + computationTime) {
					isDone = true;
				}
			}
			return isDone;
		}
		
	}
	
}
