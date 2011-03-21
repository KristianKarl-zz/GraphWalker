package org.graphwalker.multipleModels;

import org.graphwalker.ModelBasedTesting;
import org.graphwalker.generators.PathGenerator;

/**
 * @author krikar Any test should extend this class.
 */
public class ModelAPI {
  private ModelBasedTesting mbt = null;

  public ModelAPI(String model, boolean efsm, PathGenerator generator) {
    mbt = new ModelBasedTesting();
    mbt.readGraph(model);
    mbt.enableExtended(efsm);
    mbt.setGenerator(generator);
  }

  public void setMbt(ModelBasedTesting mbt) {
    this.mbt = mbt;
  }

  public ModelBasedTesting getMbt() {
    return mbt;
  }
}