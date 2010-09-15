package org.graphwalker;

import java.util.Vector;

import org.apache.log4j.Logger;

public class EventDrivenModels {
	private static Logger logger = Util.setupLogger(EventDrivenModels.class);
	private Vector<ModelExection> models = new Vector<ModelExection>();
	private Object executionClass = null;
	private ModelExection executingModel = null;

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

	public void switchModel(String modelName) {
		logger.debug("Will switch to model: " + modelName);
		if (executingModel != null) {
			logger.debug("Stopping model thread: " + executingModel );
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

	public void waitToFinish() {
		try {
			for (ModelExection m : models) {
				m.join();
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
			} catch ( RuntimeException e ) {
				logger.debug(e.getMessage());
			}
		}
	}
}
