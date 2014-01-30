package org.graphwalker.cli;


import org.graphwalker.core.PathGenerator;
import org.graphwalker.core.condition.*;
import org.graphwalker.core.generator.AStarPath;
import org.graphwalker.core.generator.CombinedPathGenerator;
import org.graphwalker.core.generator.RandomPath;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

public class GeneratorParserTest {

  @Test // Single stop condition
  public void test1() {
    PathGenerator generator = GeneratorParser.parse("random(edge_coverage(100))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(EdgeCoverage.class) );
    Assert.assertThat(generator.getStopCondition().getValue(), is("100") );
  }

  @Test // Single stop condition
  public void test2() {
    PathGenerator generator = GeneratorParser.parse("random(vertex_coverage(100))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(VertexCoverage.class) );
    Assert.assertThat(generator.getStopCondition().getValue(), is("100") );
  }

  @Test // Single stop condition
  public void test3() {
    PathGenerator generator = GeneratorParser.parse("a_star(reached_vertex(\"v_ABC\"))");
    Assert.assertThat(generator, instanceOf(AStarPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(ReachedVertex.class) );
    Assert.assertThat(generator.getStopCondition().getValue(), is("v_ABC") );
  }

  @Test // Single stop condition
  public void test4() {
    PathGenerator generator = GeneratorParser.parse("random(reached_edge(\"edgeName\"))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(ReachedEdge.class) );
    Assert.assertThat(generator.getStopCondition().getValue(), is("edgeName") );
  }

  @Test // Single stop condition
  public void test5() {
    PathGenerator generator = GeneratorParser.parse("random(reached_vertex(\"vertexName\"))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(ReachedVertex.class) );
    Assert.assertThat(generator.getStopCondition().getValue(), is("vertexName") );
  }

  @Test // Single stop condition
  public void test6() {
    PathGenerator generator = GeneratorParser.parse("random(time_duration(600))");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(TimeDuration.class) );
    Assert.assertThat(generator.getStopCondition().getValue(), is("600") );
  }

  @Test // 2 stop condition, logical OR'd, and with white spaces in expression
  public void test7() {
    PathGenerator generator = GeneratorParser.parse("random ( edge_coverage(100) and vertex_coverage (100) )");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(CombinationalCondition.class) );
  }

  @Test // 2 stop condition, logical OR'd, and with white spaces in expression
  public void test8() {
    PathGenerator generator = GeneratorParser.parse("random ( reached_vertex(\"Some_vertex\") or reached_edge ( \"Some_edge\" ) )");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(AlternativeCondition.class) );

    AlternativeCondition condition = (AlternativeCondition)generator.getStopCondition();
    Assert.assertThat(condition.getStopConditionAtIndex(0), instanceOf(ReachedVertex.class) );
    Assert.assertThat(condition.getStopConditionAtIndex(0).getValue(), is("Some_vertex") );
    Assert.assertThat(condition.getStopConditionAtIndex(1), instanceOf(ReachedEdge.class) );
    Assert.assertThat(condition.getStopConditionAtIndex(1).getValue(), is("Some_edge") );
  }

  @Test // 2 stop condition, logical OR'd, and with white spaces in expression
  public void test9() {
    PathGenerator generator = GeneratorParser.parse("random ( edge_coverage(100) or vertex_coverage (100) )");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(AlternativeCondition.class) );
  }

  @Test // 2 stop condition, logical AND'd, and with white spaces in expression
  public void test10() {
    PathGenerator generator = GeneratorParser.parse("random( edge_coverage(100) or time_duration(500) ), a_star(reached_vertex(\"v_ABC\"))");
    Assert.assertThat(generator, instanceOf(CombinedPathGenerator.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(AlternativeCondition.class) );

    AlternativeCondition condition = (AlternativeCondition)generator.getStopCondition();
    Assert.assertThat(condition.getStopConditionAtIndex(0), instanceOf(EdgeCoverage.class) );
    Assert.assertThat(condition.getStopConditionAtIndex(1), instanceOf(TimeDuration.class) );


    Assert.assertThat(((CombinedPathGenerator)generator).getPathGeneratorAtIndex(0).getStopCondition(), instanceOf(AlternativeCondition.class) );
    Assert.assertThat(((CombinedPathGenerator)generator).getPathGeneratorAtIndex(1).getStopCondition(), instanceOf(ReachedVertex.class) );
  }

  @Test // 2 stop condition, logical OR'd, and with white spaces in expression
  public void test11() {
    PathGenerator generator = GeneratorParser.parse("RANDOM ( REACHED_VERTEX(\"Some_vertex\") OR REACHED_EDGE ( \"Some_edge\" ) )");
    Assert.assertThat(generator, instanceOf(RandomPath.class));
    Assert.assertThat(generator.getStopCondition(), instanceOf(AlternativeCondition.class) );

    AlternativeCondition condition = (AlternativeCondition)generator.getStopCondition();
    Assert.assertThat(condition.getStopConditionAtIndex(0), instanceOf(ReachedVertex.class) );
    Assert.assertThat(condition.getStopConditionAtIndex(0).getValue(), is("Some_vertex") );
    Assert.assertThat(condition.getStopConditionAtIndex(1), instanceOf(ReachedEdge.class) );
    Assert.assertThat(condition.getStopConditionAtIndex(1).getValue(), is("Some_edge") );
  }
}
