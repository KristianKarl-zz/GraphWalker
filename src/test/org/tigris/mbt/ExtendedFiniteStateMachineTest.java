package test.org.tigris.mbt;

import java.util.Hashtable;
import java.util.Set;

import org.tigris.mbt.ExtendedFiniteStateMachine;
import org.tigris.mbt.FiniteStateMachine;
import org.tigris.mbt.GraphML;
import org.tigris.mbt.Keywords;

import junit.framework.TestCase;

public class ExtendedFiniteStateMachineTest extends TestCase {

    public void test_FSM_RandomGeneration50()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/methods/Main.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
    	Set path = FSM.getPath(FSM.METHOD_RANDOMIZED, Keywords.START_NODE, "50");
    	String[] events = ((String)path.toArray()[0]).split("\n");
    	assertEquals(100, events.length);
    }

    public void test_EFSM_RandomGeneration50()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/methods/ExtendedMain.graphml");
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(gml.getModel());
    	Set path = EFSM.getPath(EFSM.METHOD_RANDOMIZED, Keywords.START_NODE, "50");
    	String[] events = ((String)path.toArray()[0]).split("\n");
    	assertEquals(100, events.length);
    }

    public void test_FSM_WeightedRandomGeneration5000()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
		FSM.setWeighted(true);
    	Set path = FSM.getPath(FSM.METHOD_RANDOMIZED, Keywords.START_NODE, "5000");
    	String[] events = ((String)path.toArray()[0]).split("\n");
    	assertEquals(10000, events.length);
    	Hashtable eventCounter = new Hashtable();
    	for(int i = 0; i<events.length; i++)
    	{
    		int count = 0;
    		if(eventCounter.containsKey(events[i]))
    			count = ((Integer)eventCounter.get(events[i])).intValue();
    		eventCounter.put(events[i], new Integer(count+1));
    	}
    	assertTrue("All accounted: S4 1 10%", 1 == Math.round((((Integer)eventCounter.get("S4_1_10")).doubleValue()*10 / ((Integer)eventCounter.get("S4")).doubleValue())) );
    	assertTrue("All accounted: S4 2 90%", 9 == Math.round((((Integer)eventCounter.get("S4_2_90")).doubleValue()*10 / ((Integer)eventCounter.get("S4")).doubleValue())) );
    	assertTrue("Auto: S5 1 10%", 1 == Math.round((((Integer)eventCounter.get("S5_1_10")).doubleValue()*10 / ((Integer)eventCounter.get("S5")).doubleValue())) );
    	assertTrue("Auto: S5 2 10%", 1 == Math.round((((Integer)eventCounter.get("S5_2_10")).doubleValue()*10 / ((Integer)eventCounter.get("S5")).doubleValue())) );
    	assertTrue("Auto: S5 3 80%", 8 == Math.round((((Integer)eventCounter.get("S5_3_80")).doubleValue()*10 / ((Integer)eventCounter.get("S5")).doubleValue())) );
		assertEquals("Only one path returned",1, path.size());
    }

    public void test_EFSM_WeightedRandomGeneration5000()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/weight/FSM.graphml");
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(gml.getModel());
		EFSM.setWeighted(true);
    	Set path = EFSM.getPath(EFSM.METHOD_RANDOMIZED, Keywords.START_NODE, "5000");
    	String[] events = ((String)path.toArray()[0]).split("\n");
    	assertEquals(10000, events.length);
    	Hashtable eventCounter = new Hashtable();
    	for(int i = 0; i<events.length; i++)
    	{
    		int count = 0;
    		if(eventCounter.containsKey(events[i]))
    			count = ((Integer)eventCounter.get(events[i])).intValue();
    		eventCounter.put(events[i], new Integer(count+1));
    	}
    	assertTrue("All accounted: S4 1 10%", 1 == Math.round((((Integer)eventCounter.get("S4_1_10")).doubleValue()*10 / ((Integer)eventCounter.get("S4")).doubleValue())) );
    	assertTrue("All accounted: S4 2 90%", 9 == Math.round((((Integer)eventCounter.get("S4_2_90")).doubleValue()*10 / ((Integer)eventCounter.get("S4")).doubleValue())) );
    	assertTrue("Auto: S5 1 10%", 1 == Math.round((((Integer)eventCounter.get("S5_1_10")).doubleValue()*10 / ((Integer)eventCounter.get("S5")).doubleValue())) );
    	assertTrue("Auto: S5 2 10%", 1 == Math.round((((Integer)eventCounter.get("S5_2_10")).doubleValue()*10 / ((Integer)eventCounter.get("S5")).doubleValue())) );
    	assertTrue("Auto: S5 3 80%", 8 == Math.round((((Integer)eventCounter.get("S5_3_80")).doubleValue()*10 / ((Integer)eventCounter.get("S5")).doubleValue())) );
		assertEquals("Only one path returned",1, path.size());
    }

    public void test_FSM_ShortestPath()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/methods/Main.graphml");
		FiniteStateMachine FSM = new FiniteStateMachine(gml.getModel());
    	Set path = FSM.getPath(FSM.METHOD_SHORTEST_PATH, Keywords.START_NODE, "v_SaveBeforeCloseLock");
    	String pathAlt1 = "e_Initialize\nv_KeePassNotRunning\ne_StartWithDatabase\nv_EnterMasterCompositeMasterKey\ne_EnterCorrectKey\nv_MainWindow_DB_Loaded\ne_CloseDB\nv_SaveBeforeCloseLock\n"; 
    	String pathAlt2 = "e_Initialize\nv_KeePassNotRunning\ne_StartWithDatabase\nv_EnterMasterCompositeMasterKey\ne_EnterCorrectKey\nv_MainWindow_DB_Loaded\ne_CloseApp\nv_SaveBeforeCloseLock\n"; 
    	String actualPath = (String) path.toArray()[0];
		assertTrue(actualPath.equals(pathAlt1) || actualPath.equals(pathAlt2) );
    }

    public void test_EFSM_ShortestPath()
    {
		GraphML gml = new GraphML();
		gml.load("graphml/methods/ExtendedMain.graphml");
		ExtendedFiniteStateMachine EFSM = new ExtendedFiniteStateMachine(gml.getModel());
    	Set path = EFSM.getPath(EFSM.METHOD_SHORTEST_PATH, Keywords.START_NODE, "v_SaveBeforeCloseLock");
    	String pathAlt1 = "e_Initialize\nv_KeePassNotRunning/incorrect=0;databaseChanged=false;\ne_StartWithDatabase\nv_EnterMasterCompositeMasterKey/incorrect=0;databaseChanged=false;\ne_EnterCorrectKey\nv_MainWindow_DB_Loaded/incorrect=0;databaseChanged=false;\ne_ChangeDatabase\nv_MainWindow_DB_Loaded/incorrect=0;databaseChanged=true;\ne_CloseDB\nv_SaveBeforeCloseLock/incorrect=0;databaseChanged=true;\n";
    	String pathAlt2 = "e_Initialize\nv_KeePassNotRunning/incorrect=0;databaseChanged=false;\ne_StartWithDatabase\nv_EnterMasterCompositeMasterKey/incorrect=0;databaseChanged=false;\ne_EnterCorrectKey\nv_MainWindow_DB_Loaded/incorrect=0;databaseChanged=false;\ne_ChangeDatabase\nv_MainWindow_DB_Loaded/incorrect=0;databaseChanged=true;\ne_CloseApp\nv_SaveBeforeCloseLock/incorrect=0;databaseChanged=true;\n";
    	String actualPath = (String) path.toArray()[0];
		assertTrue(actualPath.equals(pathAlt1) || actualPath.equals(pathAlt2) );
    }

}
