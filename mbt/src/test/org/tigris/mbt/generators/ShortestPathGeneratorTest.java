package test.org.tigris.mbt.generators;

import org.tigris.mbt.ExtendedFiniteStateMachine;
import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.GraphML;
import org.tigris.mbt.conditions.ReachedEdge;
import org.tigris.mbt.conditions.ReachedState;
import org.tigris.mbt.generators.PathGenerator;
import org.tigris.mbt.generators.ShortestPathGenerator;

import junit.framework.TestCase;

public class ShortestPathGeneratorTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void test_FSM_ShortestGenerationStateStop()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(false);
		PathGenerator pathGenerator = new ShortestPathGenerator(FSM, new ReachedState(FSM, "S3") );
		while(pathGenerator.hasNext())
		{
			String[] stepPair = pathGenerator.getNext();
			System.out.println("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] + " )" );
		}
		System.out.println("==============================");
    }

	public void test_FSM_ShortestGenerationEdgeStop()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(false);
		PathGenerator pathGenerator = new ShortestPathGenerator(FSM, new ReachedEdge(FSM, "S4_1_10") );
		while(pathGenerator.hasNext())
		{
			String[] stepPair = pathGenerator.getNext();
			System.out.println("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] + " )" );
		}
		System.out.println("==============================");
    }

	public void test_EFSM_ShortestGenerationStateStop()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/methods/ExtendedMain.graphml");
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(gml.getModel());
		EFSM.setWeighted(false);
		PathGenerator pathGenerator = new ShortestPathGenerator(EFSM, new ReachedState(EFSM, "v_InvalidKey/incorrect=3;databaseChanged=true;") );
		while(pathGenerator.hasNext())
		{
			String[] stepPair = pathGenerator.getNext();
			System.out.println("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] + " )" );
		}
		System.out.println("==============================");
    }

	public void test_EFSM_ShortestGenerationEdgeStop()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/methods/ExtendedMain.graphml");
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(gml.getModel());
		EFSM.setWeighted(false);
		PathGenerator pathGenerator = new ShortestPathGenerator(EFSM, new ReachedEdge(EFSM, "e_ChangeDatabase") );
		while(pathGenerator.hasNext())
		{
			String[] stepPair = pathGenerator.getNext();
			System.out.println("call( "+ stepPair[0] + " ) then verify( " + stepPair[1] + " )" );
		}
		System.out.println("==============================");
    }

}
