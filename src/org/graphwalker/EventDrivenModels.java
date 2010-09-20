package org.graphwalker;

import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;

public class EventDrivenModels {
	private static Logger logger = Util.setupLogger(EventDrivenModels.class);
	Vector<ModelExection> models = new Vector<ModelExection>();
	Stack<ModelExection> pausedModels = new Stack<ModelExection>(); 
	private Object executionClass = null;
	private ModelExection executingModel = null;
	static Object lockbox = new Object();

	public EventDrivenModels(Object objInstance) {
		executionClass = objInstance;
	}

	public EventDrivenModels() {
	}

	public Vector<ModelExection> getModels() {
		return models;
	}

	public void addModel(ModelBasedTesting model) {
		models.add(new ModelExection(model));
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
		for (ModelExection m : models) {
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
			pausedModels.push(executingModel);
		}
		for (ModelExection m : models) {
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
		boolean isThreadRunning;
		boolean allThreadsTerminated;
		try {
			while (true) {
				synchronized (lockbox) {
					isThreadRunning = false;
					allThreadsTerminated = true;
					for (ModelExection m : models) {
						logger.debug(m.getName() + ": " + m.getState());
						if ( m.getState() != Thread.State.TERMINATED ) {
							allThreadsTerminated = false;
							if ( m.getState() == Thread.State.RUNNABLE || m.getState() == Thread.State.TIMED_WAITING ) {
								isThreadRunning = true;
							}
						}						
					}
					
					if ( allThreadsTerminated ) {
						if ( pausedModels.isEmpty() ) {
							logger.debug("All threads terminated");
							return;							
						}
						throw new RuntimeException("This should never happen! Can't have all threads in TERMINATED state, and paused threads.");
					} else if ( isThreadRunning == false ){
						if ( !pausedModels.isEmpty() ) {
							logger.debug("Found suspended model: " + pausedModels.peek());
							pausedModels.pop().getModel().resume();
						}
					}					

					Thread.sleep(100);
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
