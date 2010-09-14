package org.graphwalker;

import java.util.Vector;

public class EventDrivenModels {
  private Vector<ModelBasedTesting> models = new Vector<ModelBasedTesting>();

	public Vector<ModelBasedTesting> getModels() {
  	return models;
  }

	public void addModel(ModelBasedTesting mbt) {
		models.add(mbt);	  
  }
}
