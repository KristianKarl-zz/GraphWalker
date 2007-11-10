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
		
		String[] stepPair;
		stepPair = pathGenerator.getNext();
		assertEquals("Start_1_100",  stepPair[0]);
		assertEquals("S5", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("S5_3_80",  stepPair[0]);
		assertEquals("S3", stepPair[1]);
		assertFalse(pathGenerator.hasNext());
    }

	public void test_FSM_ShortestGenerationEdgeStop()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(false);
		PathGenerator pathGenerator = new ShortestPathGenerator(FSM, new ReachedEdge(FSM, "S4_1_10") );
		
		String[] stepPair;
		stepPair = pathGenerator.getNext();
		assertEquals("Start_1_100",  stepPair[0]);
		assertEquals("S5", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("S5_2_10",  stepPair[0]);
		assertEquals("S4", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("S4_1_10",  stepPair[0]);
		assertEquals("S2", stepPair[1]);
		assertFalse(pathGenerator.hasNext());
    }

	public void test_EFSM_ShortestGenerationStateStop()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/methods/ExtendedMain.graphml");
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(gml.getModel());
		EFSM.setWeighted(false);
		PathGenerator pathGenerator = new ShortestPathGenerator(EFSM, new ReachedState(EFSM, "v_InvalidKey/incorrect=3;databaseChanged=true;") );
		
		String[] stepPair;
		stepPair = pathGenerator.getNext();
		assertEquals("e_Initialize",  stepPair[0]);
		assertEquals("v_KeePassNotRunning/incorrect=0;databaseChanged=false;",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("e_StartWithDatabase",  stepPair[0]);
		assertEquals("v_EnterMasterCompositeMasterKey/incorrect=0;databaseChanged=false;",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("e_EnterCorrectKey",  stepPair[0]);
		assertEquals("v_MainWindow_DB_Loaded/incorrect=0;databaseChanged=false;",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("e_ChangeDatabase",  stepPair[0]);
		assertEquals("v_MainWindow_DB_Loaded/incorrect=0;databaseChanged=true;",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("e_CloseApp",  stepPair[0]);
		assertEquals("v_SaveBeforeCloseLock/incorrect=0;databaseChanged=true;",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("e_Yes",  stepPair[0]);
		assertEquals("v_KeePassNotRunning/incorrect=0;databaseChanged=true;",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("e_StartWithDatabase",  stepPair[0]);
		assertEquals("v_EnterMasterCompositeMasterKey/incorrect=0;databaseChanged=true;",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("e_EnterInvalidKey",  stepPair[0]);
		assertEquals("v_InvalidKey/incorrect=1;databaseChanged=true;",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("e_CloseDialog",  stepPair[0]);
		assertEquals("v_EnterMasterCompositeMasterKey/incorrect=1;databaseChanged=true;",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("e_EnterInvalidKey",  stepPair[0]);
		assertEquals("v_InvalidKey/incorrect=2;databaseChanged=true;",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("e_CloseDialog",  stepPair[0]);
		assertEquals("v_EnterMasterCompositeMasterKey/incorrect=2;databaseChanged=true;",  stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("e_EnterInvalidKey",  stepPair[0]);
		assertEquals("v_InvalidKey/incorrect=3;databaseChanged=true;",  stepPair[1]);
		assertFalse(pathGenerator.hasNext());

    }

	public void test_EFSM_ShortestGenerationEdgeStop()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/methods/ExtendedMain.graphml");
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(gml.getModel());
		EFSM.setWeighted(false);
		PathGenerator pathGenerator = new ShortestPathGenerator(EFSM, new ReachedEdge(EFSM, "e_ChangeDatabase") );
		
		String[] stepPair;
		stepPair = pathGenerator.getNext();
		assertEquals("e_Initialize",  stepPair[0]);
		assertEquals("v_KeePassNotRunning/incorrect=0;databaseChanged=false;", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("e_StartWithDatabase",  stepPair[0]);
		assertEquals("v_EnterMasterCompositeMasterKey/incorrect=0;databaseChanged=false;", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("e_EnterCorrectKey",  stepPair[0]);
		assertEquals("v_MainWindow_DB_Loaded/incorrect=0;databaseChanged=false;", stepPair[1]);
		stepPair = pathGenerator.getNext();
		assertEquals("e_ChangeDatabase",  stepPair[0]);
		assertEquals("v_MainWindow_DB_Loaded/incorrect=0;databaseChanged=true;", stepPair[1]);
		assertFalse(pathGenerator.hasNext());

    }

}
