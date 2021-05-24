/*
 * cameron campbell
 * advanced java
 * occc spring 2021
 * cigarette smoker's problem
 */

import java.util.concurrent.Semaphore;

public class CigaretteSmokers 
{
	/*
	 * class-relevant variables and semaphores
	 */
	static int paper = 0;
	static int matches = 0;
	static int tobacco = 0;
	static Semaphore tableSemaphore = new Semaphore(1);
	static Semaphore agentSemaphore = new Semaphore(0);
	
	
	// sleep simulation method for delay loops; "keeps things interesting"
	public static void sleepSim() 
	{
		try 
		{
			Thread.sleep((int)(Math.random()*2000));
		}
		catch(InterruptedException e) 
		{
			
		}
	}
	
	
	// main method
	public static void main(String[] args) 
	{
		// greeting message
		System.out.println("Welcome to Cigarette Smokers!");
		
		// smoker 1 has infinite paper and tobacco
		Smoker smoker1 = new Smoker(1);
		// smoker 2 has infinite paper and matches
		Smoker smoker2 = new Smoker(2);
		// smoker 3 has infinite matcher and tobacco
		Smoker smoker3 = new Smoker(3);
		// agent restocks the table when lone item is taken
		Agent agent = new Agent();
		
		// start threads and initialize run methods
		agent.start();
		smoker1.start();
		smoker2.start();
		smoker3.start();
	}
	
	
	// custom thread subclass for smokers
	public static class Smoker extends Thread
	{
		// smoker subclass variable and constructor
		int i;
		public Smoker(int i) 
		{
			super();
			this.i = i;
		}
		
		// smoker subclass overridden run method
		public void run() 
		{
			while(true) 
			{
				sleepSim();
				System.out.println("Smoker " + i + " is awake!");
				sleepSim();
				System.out.println("Smoker " + i + " is trying to acquire the table...");
				try 
				{
					// basic table acquisition before conditionals
					tableSemaphore.acquire();
					System.out.println("Smoker " + i + " has the table...");
					sleepSim();
					
					/*
					 * if-else-if block for the individual conditionals of each smoker. should
					 * a smoker be able to acquire the item they need on the table, the agent
					 * will be signaled to restock the table with one random item
					 */
					if(i == 1) 
					{
						System.out.println("Smoker 1 has paper and tobacco; looking for matches...");
						if(matches > 0) 
						{
							System.out.println("Smoker 1 has found matches! \nSmoker 1 is smoking...");
							matches = 0;
							tableSemaphore.release();
							agentSemaphore.release();
						}
						else 
						{
							System.out.println("Smoker 1 didn't find matches.");
							sleepSim();
							System.out.println("Smoker 1 going to sleep...");
							tableSemaphore.release();
						}
					}
					else if(i == 2) 
					{
						System.out.println("Smoker 2 has paper and matches; looking for tobacco...");
						if(tobacco > 0) 
						{
							System.out.println("Smoker 2 has found tobacco! \nSmoker 2 is smoking...");
							tobacco = 0;
							tableSemaphore.release();
							agentSemaphore.release();
						}
						else 
						{
							System.out.println("Smoker 2 didn't find tobacco.");
							sleepSim();
							System.out.println("Smoker 2 going to sleep...");
							tableSemaphore.release();
						}
					}
					else if(i == 3) 
					{
						System.out.println("Smoker 3 has matches and tobacco; looking for paper...");
						if(paper > 0) 
						{
							System.out.println("Smoker 3 has found paper! \nSmoker 3 is smoking...");
							paper = 0;
							tableSemaphore.release();
							agentSemaphore.release();
						}
						else 
						{
							System.out.println("Smoker 3 didn't find paper.");
							sleepSim();
							System.out.println("Smoker 3 going to sleep...");
							tableSemaphore.release();
						}
					}
				}
				catch(InterruptedException e) {}
			}
		}
	}
	
	
	// custom thread subclass for agents
	public static class Agent extends Thread 
	{
		/*
		 * agent subclass constructor. no need for variable,
		 * there will always only be one agent
		 */
		public Agent() 
		{
			super();
		}
		
		// agent subclass overridden run method
		public void run() 
		{
			while(true) 
			{
				sleepSim();
				System.out.println("Agent is awake!");
				sleepSim();
				System.out.println("Agent is trying to acquire the table...");
				try 
				{
					tableSemaphore.acquire();
					System.out.println("Agent has the table...");
					sleepSim();
					
					// random int between 0 and 2 for the following if-else-if block
					int itemToStock = ((int)(Math.random()*3));
					
					/*
					 * if-else-if block for determining which item to restock based on
					 * what is randomly chosen by the itemToStock variable. an integer
					 * between 0 and 2 is generated; if it is 0, a match is restocked.
					 * 1, and paper is restocked. 2, and tobacco is restocked.
					 */
					if(itemToStock == 0) 
					{
						System.out.println("Agent has put a match on the table...");
						matches++;
					}
					else if(itemToStock == 1) 
					{
						System.out.println("Agent has put paper on the table...");
						paper++;
					}
					else if(itemToStock == 2) 
					{
						System.out.println("Agent has put tobacco on the table...");
						tobacco++;
					}
					
					/*
					 * agent then releases table, and to go to sleep attempts to acquire
					 * himself. since agentSemaphore is initialized to 0, this is denied,
					 * and agent is guaranteed to sleep until called by the next smoker to
					 * clean out the table
					 */
					sleepSim();
					System.out.println("Agent going to sleep...");
					tableSemaphore.release();
					agentSemaphore.acquire();
				}
				catch(InterruptedException e) {}
			}
		}
	}
}
