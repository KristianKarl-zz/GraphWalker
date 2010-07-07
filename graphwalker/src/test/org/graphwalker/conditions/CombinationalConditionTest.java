package test.org.graphwalker.conditions;

import org.graphwalker.conditions.AlwaysCondition;
import org.graphwalker.conditions.CombinationalCondition;
import org.graphwalker.conditions.NeverCondition;

import junit.framework.TestCase;

public class CombinationalConditionTest extends TestCase {

	public void testConstructor() {
		new CombinationalCondition();
	}

	public void testFulfillment() {
		CombinationalCondition condition = new CombinationalCondition();
		condition.add(new AlwaysCondition());
		assertEquals((double) 1 / 1, condition.getFulfilment(), 0.01);
		condition.add(new AlwaysCondition());
		assertEquals((double) 2 / 2, condition.getFulfilment(), 0.01);
		condition.add(new NeverCondition());
		assertEquals((double) 2 / 3, condition.getFulfilment(), 0.01);
		condition.add(new AlwaysCondition());
		assertEquals((double) 3 / 4, condition.getFulfilment(), 0.01);
		condition.add(new NeverCondition());
		assertEquals((double) 3 / 5, condition.getFulfilment(), 0.01);
		condition.add(new NeverCondition());
		assertEquals((double) 3 / 6, condition.getFulfilment(), 0.01);
	}

	public void testIsFulfilled() {
		CombinationalCondition condition = new CombinationalCondition();
		condition.add(new AlwaysCondition());
		assertEquals(true, condition.isFulfilled());
		condition.add(new AlwaysCondition());
		assertEquals(true, condition.isFulfilled());
		condition.add(new NeverCondition());
		assertEquals(false, condition.isFulfilled());
		condition.add(new AlwaysCondition());
		assertEquals(false, condition.isFulfilled());
		condition.add(new NeverCondition());
		assertEquals(false, condition.isFulfilled());
		condition.add(new NeverCondition());
		assertEquals(false, condition.isFulfilled());
	}
}
