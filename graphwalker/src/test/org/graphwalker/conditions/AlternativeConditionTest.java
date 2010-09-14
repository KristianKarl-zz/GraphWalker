package test.org.graphwalker.conditions;

import org.graphwalker.conditions.AlternativeCondition;
import org.graphwalker.conditions.NeverCondition;

import junit.framework.TestCase;

public class AlternativeConditionTest extends TestCase {

	public void testConstructor() {
		new AlternativeCondition();
	}

	public void testFulfillment() {
		AlternativeCondition condition = new AlternativeCondition();
		condition.add(new NeverCondition());
		assertEquals((double) 0, condition.getFulfilment(), 0.01);
		condition.add(new NeverCondition());
		assertEquals((double) 0, condition.getFulfilment(), 0.01);
		condition.add(new NeverCondition());
		assertEquals((double) 0, condition.getFulfilment(), 0.01);
	}

	public void testIsFulfilled() {
		AlternativeCondition condition = new AlternativeCondition();
		condition.add(new NeverCondition());
		assertEquals(false, condition.isFulfilled());
		condition.add(new NeverCondition());
		assertEquals(false, condition.isFulfilled());
		condition.add(new NeverCondition());
		assertEquals(false, condition.isFulfilled());
	}
}
