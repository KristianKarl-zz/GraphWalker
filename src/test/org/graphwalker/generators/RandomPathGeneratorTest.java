package test.org.graphwalker.generators;

import org.apache.log4j.Logger;
import org.graphwalker.Util;
import org.graphwalker.conditions.EdgeCoverage;
import org.graphwalker.conditions.StopCondition;
import org.graphwalker.exceptions.StopConditionException;
import org.graphwalker.generators.PathGenerator;
import org.graphwalker.generators.RandomPathGenerator;
import org.graphwalker.io.GraphML;
import org.graphwalker.machines.FiniteStateMachine;

import junit.framework.TestCase;

public class RandomPathGeneratorTest extends TestCase {

	private Logger logger = Util.setupLogger(RandomPathGeneratorTest.class);

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void test_WeightedRandomGeneration() throws StopConditionException, InterruptedException {
		logger.info("TEST: test_WeightedRandomGeneration");
		logger.info("=======================================================================");
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(true);

		StopCondition sc = new EdgeCoverage(1.0);
		PathGenerator pathGenerator = new RandomPathGenerator();
		pathGenerator.setMachine(FSM);
		pathGenerator.setStopCondition(sc);

		while (pathGenerator.hasNext()) {
			String[] stepPair = pathGenerator.getNext();

			int stats[] = FSM.getStatistics();
			int ec = 100 * stats[1] / stats[0];

			logger.debug("call( " + stepPair[0] + " ) then verify( " + stepPair[1] + " ) --> Edge coverage @ " + ec + "%");
		}
		logger.debug("==============================");
	}

	public void test_RandomGeneration() throws StopConditionException, InterruptedException {
		logger.info("TEST: test_RandomGeneration");
		logger.info("=======================================================================");
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(false);
		StopCondition sc = new EdgeCoverage(1.0);
		PathGenerator pathGenerator = new RandomPathGenerator();
		pathGenerator.setMachine(FSM);
		pathGenerator.setStopCondition(sc);

		while (pathGenerator.hasNext()) {
			String[] stepPair = pathGenerator.getNext();

			int stats[] = FSM.getStatistics();
			int ec = 100 * stats[1] / stats[0];

			logger.debug("call( " + stepPair[0] + " ) then verify( " + stepPair[1] + " ) --> Edge coverage @ " + ec + "%");
		}
		logger.debug("==============================");
	}
}
