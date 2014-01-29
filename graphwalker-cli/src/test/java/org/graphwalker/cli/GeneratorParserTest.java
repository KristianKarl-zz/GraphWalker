package org.graphwalker.cli;


import org.graphwalker.core.PathGenerator;
import org.graphwalker.core.condition.AlternativeCondition;
import org.graphwalker.core.condition.EdgeCoverage;
import org.graphwalker.core.condition.VertexCoverage;
import org.graphwalker.core.generator.CombinedPathGenerator;
import org.graphwalker.core.generator.RandomPath;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

public class GeneratorParserTest {

  @Test // Single stop condition
  public void singleRandomGenerator1() {
    PathGenerator generator = GeneratorParser.parse("random(edge_coverage(100))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(EdgeCoverage.class) );
    Assert.assertThat(generator.getStopCondition().getValue(), is("100") );
  }

  @Test // 2 stop condition, logical OR'd, and with white spaces in expression
  public void singleRandomGenerator2() {
    PathGenerator generator = GeneratorParser.parse("random ( edge_coverage(100) or vertex_coverage (100) )");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(AlternativeCondition.class) );
  }

/*
  @Test // 2 stop condition, logical AND'd, and with white spaces in expression
  public void singleRandomGenerator3() {

    CombinedPathGenerator generator = OptionsParser.parseGenerator("random ( edge_coverage(100) and vertex_coverage (100) )");
    Assert.assertThat(generator, instanceOf(CombinedPathGenerator.class));
    Assert.assertThat(generator.getPathGeneratorAtIndex(0), instanceOf(RandomPath.class) );
    Assert.assertThat(generator.getPathGeneratorAtIndex(0).getStopCondition(), instanceOf(AlternativeCondition.class) );
    Assert.assertThat(((AlternativeCondition)generator.getPathGeneratorAtIndex(0).getStopCondition()).getStopConditionAtIndex(0), instanceOf(EdgeCoverage.class) );
    Assert.assertThat(((AlternativeCondition)generator.getPathGeneratorAtIndex(0).getStopCondition()).getStopConditionAtIndex(1), instanceOf(VertexCoverage.class) );
  }
*/
}
