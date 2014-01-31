/*
 * #%L
 * GraphWalker Command Line Interface
 * %%
 * Copyright (C) 2011 - 2014 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
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
