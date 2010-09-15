package org.graphwalker;

import java.util.Observable;

public class EventDrivenModels extends Observable {

	public void addModel(Model model) {
		addObserver(model);	  
  }
}
