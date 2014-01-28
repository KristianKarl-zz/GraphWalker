package org.graphwalker.core.condition;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class CombinationalConditionTest {

  @Test
  public void testConstructor() {
    new CombinationalCondition();
  }

  @Test
  public void testFulfillment() {
    CombinationalCondition condition = new CombinationalCondition();

    condition.add(new Never());
    Assert.assertThat("Should be zero", condition.getFulfilment(null), is(0.0));
  }

  @Test
  public void testIsFulfilled() {
    CombinationalCondition condition = new CombinationalCondition();

    condition.add(new Never());
    Assert.assertThat("Should be false", condition.isFulfilled(null), is(false));

    condition.add(new Never());
    Assert.assertThat("Should be false", condition.isFulfilled(null), is(false));

    condition.add(new Never());
    Assert.assertThat("Should be false", condition.isFulfilled(null), is(false));
  }
}
