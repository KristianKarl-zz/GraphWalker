package test.org.tigris.mbt;

import java.util.Set;

import org.tigris.mbt.ExtendedFiniteStateMachine;
import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.GraphML;
import org.tigris.mbt.Keywords;

import junit.framework.TestCase;

public class ExtendedFiniteStateMachineTest extends TestCase {

    public void test_FSM_RandomGeneration50()
    {
		System.out.println( "TEST: test_FSM_RandomGeneration50" );
		System.out.println( "=======================================================================" );
		GraphML gml = new GraphML();
		gml.load("graphml/methods/Main.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
    	Set path = FSM.getPath(FSM.METHOD_RANDOMIZED, Keywords.START_NODE, "50");
		System.out.println( path.toArray()[0] );
		assertEquals(1, path.size());
    }

    public void test_EFSM_RandomGeneration50()
    {
		System.out.println( "TEST: test_EFSM_RandomGeneration50" );
		System.out.println( "=======================================================================" );
		GraphML gml = new GraphML();
		gml.load("graphml/methods/ExtendedMain.graphml");
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(gml.getModel());
    	Set path = EFSM.getPath(EFSM.METHOD_RANDOMIZED, Keywords.START_NODE, "50");
		System.out.println( path.toArray()[0] );
		assertEquals(1, path.size());
    }
}
