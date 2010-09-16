package org.graphwalker;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap;

public class EventDrivenModels {
	private static Logger logger = Util.setupLogger(EventDrivenModels.class);
	HashMap<ModelExection, Thread.State> models = new HashMap<ModelExection, Thread.State>();
	private Object executionClass = null;
	private ModelExection executingModel = null;
	static Object lockbox = new Object();

	public EventDrivenModels(Object objInstance) {
		executionClass = objInstance;
	}

	public EventDrivenModels() {
	}

	public Set<ModelExection> getModels() {
		return models.keySet();
	}

	public void addModel(ModelBasedTesting model) {
		models.put(new ModelExection(model), Thread.State.NEW);
	}

	public void runModel(String modelName) {
		stopAndSwitchModel(modelName);
	}

	public void stopAndSwitchModel(String modelName) {
		logger.debug("Will switch to model: " + modelName);
		if (executingModel != null) {
			logger.debug("Stopping model thread: " + executingModel);
			executingModel.getModel().stop();
		}
		for (ModelExection m : models.keySet()) {
			if (modelName.equals(m.getModel().getGraph().getLabelKey())) {
				logger.debug("Found the model, will now start it: " + m);
				executingModel = m;
				executingModel.start();
				return;
			}
		}
		throw new RuntimeException("Did not find a model: " + modelName);
	}

	public void pauseAndSwitchModel(String modelName) {
		logger.debug("Will switch to model: " + modelName);
		if (executingModel != null) {
			logger.debug("Suspending model thread: " + executingModel);
			executingModel.getModel().suspend();
		}
		for (ModelExection m : models.keySet()) {
			if (modelName.equals(m.getModel().getGraph().getLabelKey())) {
				logger.debug("Found the model, will now start it: " + m);
				executingModel = m;
				executingModel.start();
				return;
			}
		}
		throw new RuntimeException("Did not find a model: " + modelName);
	}

	public void waitToFinish() {
		try {
			while (true) {
				synchronized (lockbox) {
					for (ModelExection m : models.keySet()) {
						logger.debug(m.getName() + ": " + m.getState());
						models.put(m, m.getState());
					}

					if (models.containsValue(Thread.State.RUNNABLE) || models.containsValue(Thread.State.TIMED_WAITING)) {
						;
					} else if (models.containsValue(Thread.State.WAITING)) {
						logger.debug("Searching suspended model");
						for (ModelExection m : models.keySet()) {
							if (m.getState() == Thread.State.WAITING) {
								logger.debug("Found suspended model: " + m);
								m.getModel().resume();
								break;
							}
						}
					} else if (models.containsValue(Thread.State.TERMINATED)) {
						logger.debug("All threads terminated");
						return;
					}
					Thread.sleep(1000);
				}
			}
		} catch (InterruptedException e) {
			Util.logStackTraceToError(e);
		}
	}

	public class ModelExection extends Thread {
		private ModelBasedTesting model;

		public ModelBasedTesting getModel() {
			return model;
		}

		public void setModel(ModelBasedTesting model) {
			this.model = model;
		}

		public ModelExection(ModelBasedTesting model) {
			this.model = model;
			setName(model.getGraph().getLabelKey());
		}

		public void run() {
			try {
				model.executePath(executionClass);
			} catch (InterruptedException e) {
				Util.logStackTraceToError(e);
			} catch (RuntimeException e) {
				logger.debug(e.getMessage());
			}
		}
	}
}
