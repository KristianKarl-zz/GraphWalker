package test.org.tigris.mbt.generators;

import org.tigris.mbt.ExtendedFiniteStateMachine;
import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.GraphML;
import org.tigris.mbt.conditions.EdgeCoverage;
import org.tigris.mbt.conditions.ReachedEdge;
import org.tigris.mbt.conditions.ReachedState;
import org.tigris.mbt.conditions.StateCoverage;
import org.tigris.mbt.conditions.TestCaseLength;
import org.tigris.mbt.conditions.TimeDuration;
import org.tigris.mbt.generators.RandomPathGenerator;
import org.tigris.mbt.generators.PathGenerator;

import junit.framework.TestCase;

public class RandomPathGeneratorTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void test_EFSM_WeightedRandomGeneration100PercentEdgeCoverage()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(gml.getModel());
		EFSM.setWeighted(true);
		PathGenerator pathGenerator = new RandomPathGenerator(EFSM, new EdgeCoverage(EFSM, 1.0) );
		while(pathGenerator.hasNext())
		{
			String[] stepPair = pathGenerator.getNext();

			int stats[] = EFSM.getStatistics();
			int ec = 100*stats[1]/stats[0];

			System.out.println("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] + " ) --> Edge coverage @ " + ec +"%" );
		}
		System.out.println("==============================");
    }

	public void test_FSM_WeightedRandomGeneration100PercentEdgeCoverage()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(true);
		PathGenerator pathGenerator = new RandomPathGenerator(FSM, new EdgeCoverage(FSM, 1.0) );
		while(pathGenerator.hasNext())
		{
			String[] stepPair = pathGenerator.getNext();

			int stats[] = FSM.getStatistics();
			int ec = 100*stats[1]/stats[0];

			System.out.println("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] + " ) --> Edge coverage @ " + ec +"%" );
		}
		System.out.println("==============================");
    }

	public void test_EFSM_RandomGeneration100PercentEdgeCoverage()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(gml.getModel());
		EFSM.setWeighted(false);
		PathGenerator pathGenerator = new RandomPathGenerator(EFSM, new EdgeCoverage(EFSM, 1.0) );
		while(pathGenerator.hasNext())
		{
			String[] stepPair = pathGenerator.getNext();

			int stats[] = EFSM.getStatistics();
			int ec = 100*stats[1]/stats[0];

			System.out.println("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] + " ) --> Edge coverage @ " + ec +"%" );
		}
		System.out.println("==============================");
    }

	public void test_FSM_RandomGeneration100PercentEdgeCoverage()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(false);
		PathGenerator pathGenerator = new RandomPathGenerator(FSM, new EdgeCoverage(FSM, 1.0) );
		while(pathGenerator.hasNext())
		{
			String[] stepPair = pathGenerator.getNext();

			int stats[] = FSM.getStatistics();
			int ec = 100*stats[1]/stats[0];

			System.out.println("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] + " ) --> Edge coverage @ " + ec +"%" );
		}
		System.out.println("==============================");
    }

	public void test_FSM_RandomGeneration50PercentEdgeCoverage()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(false);
		PathGenerator pathGenerator = new RandomPathGenerator(FSM, new EdgeCoverage(FSM, 0.5) );
		while(pathGenerator.hasNext())
		{
			String[] stepPair = pathGenerator.getNext();

			int stats[] = FSM.getStatistics();
			int ec = 100*stats[1]/stats[0];

			System.out.println("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] + " ) --> Edge coverage @ " + ec +"%" );
		}
		System.out.println("==============================");
    }

	public void test_FSM_RandomGeneration100PercentStateCoverage()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(false);
		PathGenerator pathGenerator = new RandomPathGenerator(FSM, new StateCoverage(FSM, 1.0) );
		while(pathGenerator.hasNext())
		{
			String[] stepPair = pathGenerator.getNext();

			int stats[] = FSM.getStatistics();
			int vc = 100*stats[3]/stats[2];

			System.out.println("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] + " ) --> State coverage @ " + vc +"%" );
		}
		System.out.println("==============================");
    }

	public void test_FSM_RandomGeneration50PercentStateCoverage()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(false);
		PathGenerator pathGenerator = new RandomPathGenerator(FSM, new StateCoverage(FSM, 0.5) );
		while(pathGenerator.hasNext())
		{
			String[] stepPair = pathGenerator.getNext();

			int stats[] = FSM.getStatistics();
			int vc = 100*stats[3]/stats[2];

			System.out.println("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] + " ) --> State coverage @ " + vc +"%" );
		}
		System.out.println("==============================");
    }

	public void test_FSM_RandomGeneration2secTimeDuration()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(false);
		PathGenerator pathGenerator = new RandomPathGenerator(FSM, new TimeDuration(2) );
		while(pathGenerator.hasNext())
		{
			String[] stepPair = pathGenerator.getNext();
			System.out.println("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] + " )" );
		}
		System.out.println("==============================");
    }

	public void test_FSM_RandomGenerationStateStop()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(false);
		PathGenerator pathGenerator = new RandomPathGenerator(FSM, new ReachedState(FSM, "S3") );
		while(pathGenerator.hasNext())
		{
			String[] stepPair = pathGenerator.getNext();
			System.out.println("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] + " )" );
		}
		System.out.println("==============================");
    }

	public void test_FSM_RandomGenerationEdgeStop()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(false);
		PathGenerator pathGenerator = new RandomPathGenerator(FSM, new ReachedEdge(FSM, "S4_1_10") );
		while(pathGenerator.hasNext())
		{
			String[] stepPair = pathGenerator.getNext();
			System.out.println("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] + " )" );
		}
		System.out.println("==============================");
    }

	public void test_FSM_RandomGenerationTestCaseLength()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(false);
		PathGenerator pathGenerator = new RandomPathGenerator(FSM, new TestCaseLength(FSM, 5));
		while(pathGenerator.hasNext())
		{
			String[] stepPair = pathGenerator.getNext();
			System.out.println("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] + " )" );
		}
		System.out.println("==============================");
    }

}
