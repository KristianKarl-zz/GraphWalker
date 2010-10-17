package org.graphwalker.conditions;

import org.graphwalker.conditions.CombinationalCondition;
import org.graphwalker.conditions.NeverCondition;

import junit.framework.TestCase;

public class CombinationalConditionTest extends TestCase {

	public void testConstructor() {
		new CombinationalCondition();
	}

	public void testFulfillment() {
		CombinationalCondition condition = new CombinationalCondition();
		condition.add(new NeverCondition());
		assertEquals(0, condition.getFulfilment(), 0.01);
	}

	public void testIsFulfilled() {
		CombinationalCondition condition = new CombinationalCondition();
		condition.add(new NeverCondition());
		assertEquals(false, condition.isFulfilled());
		condition.add(new NeverCondition());
		assertEquals(false, condition.isFulfilled());
		condition.add(new NeverCondition());
		assertEquals(false, condition.isFulfilled());
	}
}
