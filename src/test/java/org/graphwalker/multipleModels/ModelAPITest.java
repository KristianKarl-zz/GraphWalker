package org.graphwalker.multipleModels;

import junit.framework.Assert;

import org.graphwalker.conditions.AlternativeCondition;
import org.graphwalker.conditions.CombinationalCondition;
import org.graphwalker.conditions.EdgeCoverage;
import org.graphwalker.conditions.ReachedVertex;
import org.graphwalker.conditions.RequirementCoverage;
import org.graphwalker.conditions.TimeDuration;
import org.graphwalker.exceptions.StopConditionException;
import org.graphwalker.generators.A_StarPathGenerator;
import org.graphwalker.generators.CombinedPathGenerator;
import org.graphwalker.generators.RandomPathGenerator;
import org.junit.Test;

public class ModelAPITest {

  @Test
  public void addMultipleGenerators() throws StopConditionException {
    ModelAPI model = new ModelAPI("graphml/org.graphwalker.multipleModels/a.graphml");
    model.setWeighted(false);
    model.setExtended(true);
    
    CombinationalCondition combinationalCondition = new CombinationalCondition();
    combinationalCondition.add(new RequirementCoverage(1.0));
    combinationalCondition.add(new EdgeCoverage(1.0));

    AlternativeCondition alternativeCondition = new AlternativeCondition();
    alternativeCondition.add(combinationalCondition);
    alternativeCondition.add(new TimeDuration(900));
    
    CombinedPathGenerator generator = new CombinedPathGenerator();
    generator.addPathGenerator(new A_StarPathGenerator(new ReachedVertex("C")));
    generator.addPathGenerator(new RandomPathGenerator(alternativeCondition));
    
    model.setGenerator(generator);
    Assert.assertTrue("Failed setting up the model", model.getMbt().hasNextStep());
  }
}
