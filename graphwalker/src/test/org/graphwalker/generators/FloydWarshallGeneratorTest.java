package test.org.graphwalker.generators;

import org.graphwalker.conditions.EdgeCoverage;
import org.graphwalker.conditions.StopCondition;
import org.graphwalker.exceptions.StopConditionException;
import org.graphwalker.generators.FloydWarshallGenerator;
import org.graphwalker.io.GraphML;
import org.graphwalker.machines.FiniteStateMachine;

import junit.framework.TestCase;

public class FloydWarshallGeneratorTest extends TestCase {
	// private Logger logger = Util.setupLogger(FloydWarshallGeneratorTest.class);

	public void test_RandomGeneration() throws StopConditionException {
		GraphML gml = new GraphML();
		gml.load("graphml/backtrack/keepass.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(false);
		StopCondition sc = new EdgeCoverage(1.0);
		FloydWarshallGenerator fwg = new FloydWarshallGenerator();
		fwg.setMachine(FSM);
		fwg.setStopCondition(sc);

		while (fwg.hasNext()) {
			break;
			/*
			 * String[] stepPair = fwg.getNext();
			 * 
			 * int stats[] = FSM.getStatistics(); int ec = 100*stats[1]/stats[0];
			 * 
			 * logger.debug("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] +
			 * " ) --> Edge coverage @ " + ec +"%" );
			 */}
	}
}
