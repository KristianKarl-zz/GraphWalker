package org.graphwalker;

import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;

public class EventDrivenModels {
	private static Logger logger = Util.setupLogger(EventDrivenModels.class);
	Vector<ModelBasedTesting> models = new Vector<ModelBasedTesting>();
	Stack<ThreadWrapper> pausedModels = new Stack<ThreadWrapper>(); 
	private Object executionClass = null;
	private ThreadWrapper executingModel = null;
	static Object lockbox = new Object();

	public EventDrivenModels(Object executionClass) {
		this.executionClass = executionClass;
	}

	public EventDrivenModels() {
	}

	public Vector<ModelBasedTesting> getModels() {
		return models;
	}

	public void addModel(ModelBasedTesting model) {
		models.add(model);
	}

	public void runModel(String modelName) {
		stopAndSwitchModel(modelName);
	}

	public void stopAndSwitchModel(String modelName) {
		logger.debug("Will switch to model: " + modelName);
		if (executingModel != null) {
			logger.debug("Stopping model thread: " + executingModel);
			executingModel.getModel().stop();
			executingModel = null;
		}
		for (ModelBasedTesting m : models) {
			if (modelName.equals(m.getGraph().getLabelKey())) {
				logger.debug("Found the model, will now start it: " + m.getGraph().getLabelKey());
				executingModel = new ThreadWrapper(m);
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
			pausedModels.push(executingModel);
			executingModel = null;
		}
		for (ModelBasedTesting m : models) {
			if (modelName.equals(m.getGraph().getLabelKey())) {
				logger.debug("Found the model, will now start it: " + m.getGraph().getLabelKey());
				executingModel = new ThreadWrapper(m);
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

					if ( executingModel == null ) {
						if ( pausedModels.isEmpty() ) {
							logger.debug("All threads terminated");
							return;							
						} 
						logger.debug("Found suspended model: " + pausedModels.peek());
						executingModel = pausedModels.pop();
						executingModel.getModel().resume();							
					} else {
						if ( executingModel.getState() == Thread.State.TERMINATED ) {
							logger.debug("Terminated: " + executingModel);
							executingModel = null;
						}
					}

					Thread.sleep(100);
				}
			}
		} catch (InterruptedException e) {
			Util.logStackTraceToError(e);
		}
	}

	public class ThreadWrapper extends Thread {
		private ModelBasedTesting model;

		public ModelBasedTesting getModel() {
			return model;
		}

		public void setModel(ModelBasedTesting model) {
			this.model = model;
		}

		public ThreadWrapper(ModelBasedTesting model) {
			this.model = model;
			setName(model.getGraph().getLabelKey());
		}

		public void run() {
			try {
				model.setCurrentVertex("Start");
				model.reload();
				model.executePath(executionClass);
			} catch (InterruptedException e) {
				Util.logStackTraceToError(e);
			} catch (RuntimeException e) {
				logger.debug(e.getMessage());
			}
		}
	}
}
