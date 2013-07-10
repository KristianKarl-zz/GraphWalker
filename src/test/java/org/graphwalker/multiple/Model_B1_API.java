package org.graphwalker.multiple;

import org.graphwalker.generators.PathGenerator;
import org.graphwalker.multipleModels.ModelAPI;

/**
 * Hello world!
 * 
 */
public class Model_B1_API extends ModelAPI {

  public Model_B1_API(String model, boolean efsm, PathGenerator generator) {
    super(model, efsm, generator, false);
  }

  public void e_Init() {}

  public void e_Logout() {}

  public void e_Close() {}

  public void e_ExitClient() {}

  public void v_ClientNotRunning() {}

  public void v_LoginPrompted() {}

  public void v_WhatsNew() {}
}
