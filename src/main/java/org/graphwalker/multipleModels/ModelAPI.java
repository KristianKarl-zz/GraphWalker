package org.graphwalker.multipleModels;

import java.io.File;

import org.graphwalker.ModelBasedTesting;
import org.graphwalker.generators.PathGenerator;
import org.graphwalker.generators.RandomPathGenerator;

/**
 * @author krikar Any test should extend this class.
 */
public class ModelAPI {
  private ModelBasedTesting mbt = null;

  public ModelAPI(String model, boolean efsm, PathGenerator generator, boolean weight) {
    mbt = new ModelBasedTesting();
    mbt.readGraph(model);
    mbt.enableExtended(efsm);
    mbt.setGenerator(generator);
    mbt.setWeighted(weight);
  }

  public ModelAPI(File model, boolean efsm, PathGenerator generator, boolean weight) {
    mbt = new ModelBasedTesting();
    mbt.readGraph(model);
    mbt.enableExtended(efsm);
    mbt.setGenerator(generator);
    mbt.setWeighted(weight);
  }

  public ModelAPI(String model) {
    mbt = new ModelBasedTesting();
    mbt.readGraph(model);
  }

  public void setMbt(ModelBasedTesting mbt) {
    this.mbt = mbt;
  }

  public ModelBasedTesting getMbt() {
    return mbt;
  }

  public void setWeighted(boolean weight) {
    mbt.setWeighted(weight);
  }

  public void setExtended(boolean efsm) {
    mbt.enableExtended(efsm);
  }

  public void setGenerator(PathGenerator pathGenerator) {
    mbt.setGenerator(pathGenerator);
  }
}
