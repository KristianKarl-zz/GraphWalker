package org.tigris.mbt;

import java.util.Iterator;
import java.util.Vector;
import javax.swing.JOptionPane;

public class RunPerlScript {
	
	public void run(String[] args)
	{
		boolean pass = true;
		ModelBasedTesting mbt = new ModelBasedTesting( args[ 0 ] );
		Vector list = mbt.getTestSequence();
		
		long startTime = System.currentTimeMillis();
		for (Iterator iter = list.iterator(); iter.hasNext();)
		{
			String element = (String) iter.next();
			if ( run_Perl_Subrotine( "perl " + args[ 1 ] + " " + element ) != 0 )
			{
				pass = false;
				break;
			}
		}
		long endTime = System.currentTimeMillis();
		
		String message = "";
		if ( pass )
		{
			message = "The test passed.";
		}
		else
		{	
			message = "The test failed.";
		}
		message += " Execution time: " + ( ( endTime - startTime ) / 1000 ) + " seconds";
		System.out.println( message );
		JOptionPane.showMessageDialog( null, message );
	}
	
	public static void main(String[] args)
	{		
		if ( args.length < 2 )
		{
			System.out.println( "Too few arguments" );
			displayHelpMessage();
			return;
		}

		RunPerlScript test = new RunPerlScript();
		test.run(args);
		
		// By some reason, calling JOptionPane.showMessageDialog hangs the process,
		// so we have to call System.exit to make a clean getaway. 
		System.exit( 0 );
	}
	
	
	/**
	 * This method implemets the edge: e_Initialize
	 */
	public int run_Perl_Subrotine( String command )
	{
		int result = 1;
		// prepare buffers for process output and error streams
		StringBuffer err=new StringBuffer();
		StringBuffer out=new StringBuffer();    
		try
		{
			Process proc=Runtime.getRuntime().exec(command);
			//create thread for reading inputStream (process' stdout)
			StreamReaderThread outThread=new StreamReaderThread(proc.getInputStream(),out);
			//create thread for reading errorStream (process' stderr)
			StreamReaderThread errThread=new StreamReaderThread(proc.getErrorStream(),err);
			//start both threads
			outThread.start();
			errThread.start();
			//wait for process to end
			result=proc.waitFor();
			//finish reading whatever's left in the buffers
			outThread.join();
			errThread.join();
			
			System.out.print(out.toString());
			
			/*if (result!=0) 
			 {
			 System.out.println("Process "+command+ " returned non-zero value:"+result);
			 System.out.println("Process output:\n"+out.toString());
			 System.out.println("Process error:\n"+err.toString());
			 }
			 else
			 {
			 System.out.println("Process "+command+ " executed successfully");
			 System.out.println("Process output:\n"+out.toString());
			 System.out.println("Process error:\n"+err.toString());
			 }*/
		}
		catch (Exception e)
		{
			System.out.println("Error executing "+command);
			e.printStackTrace();
		}
		return result;
	}

	private static void displayHelpMessage()
	{
		System.out.println( "RunPerlScript <graphml file> <Perl script>" );
		System.out.println( "   <graphml file> The graphml file containing the model of the test" );
		System.out.println( "   <Perl script>  The perl script implementing the model." );
	}
}
