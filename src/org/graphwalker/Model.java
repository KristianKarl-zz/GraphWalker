package org.graphwalker;

import java.util.Observable;
import java.util.Observer;

public class Model implements Observer {
	
	ModelBasedTesting mbt;

	@Override
  public void update(Observable o, Object arg) {
	  // TODO Auto-generated method stub
	  
  }

	public ModelBasedTesting getMbt() {
  	return mbt;
  }

	public void setMbt(ModelBasedTesting mbt) {
  	this.mbt = mbt;
  }

}
