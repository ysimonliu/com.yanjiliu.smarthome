package temp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import components.UsersLocation;

import pseudoRPC.HomeManagerPseudoRPCClientStub;
import pseudoRPC.HomeManagerPseudoRPCServerStub;
import pseudoRPC.Message;
import pseudoRPC.SensorPseudoRPCClientStub;
import pseudoRPC.SensorPseudoRPCServerStub;

public final class HomeManager {
	
	// scheduler for tasks
	private static ScheduledFuture<?> scheduleFuture;
	private static ScheduledExecutorService scheduler;
	private final int INITIAL_DELAY = 0;
	private final int TICK_SPEED = 1;
	private final TimeUnit TICK_SPEED_UNIT = TimeUnit.SECONDS;
	private static final int NUM_THREADS = 1;
	private static final boolean DONT_INTERRUPT_IF_RUNNING = false;
	// Pseudo RPC
	private HomeManagerPseudoRPCServerStub server;
	private HomeManagerPseudoRPCClientStub controller;
	// User Location
	private UsersLocation usersLocation;
	// temp adjustment variables
	private String tempAdjustLog;
	private int tempAdjustTime;
	// input parameter
	private static String elvinURLInput;
	  
	// constructor
	public HomeManager(String elvinURL){
		// initialize userslocation
		this.usersLocation = new UsersLocation();
		// initialize RPC
		server = new HomeManagerPseudoRPCServerStub(elvinURL, usersLocation);
		controller = new HomeManagerPseudoRPCClientStub(elvinURL);
		// initialize scheduler
		scheduler = Executors.newScheduledThreadPool(NUM_THREADS);
		// initialize temp adjustment time and temp adjustment log
		tempAdjustTime = 0;
		tempAdjustLog = "";
	}

	public static void main(String[] args) {
		// reads the parameter into variable
		if (args.length == 1) {
			elvinURLInput = args[0];
		}
		else if (args.length == 0) {
			elvinURLInput = Message.DEFAULT_ELVIN_URL;
		}
		else {
			System.exit(1);
		}
		
		// instantiate Home Manager
		HomeManager homeManager = new HomeManager(elvinURLInput);
		
		// start Home Manager
		homeManager.activateHomeManager();
	}
	
	/**
	 * This method will activate the sensor and send periodic notifications onto elvin
	 */
	private void activateHomeManager(){
		Thread homeManagerTask = new homeManagerTask();
		scheduleFuture = scheduler.scheduleWithFixedDelay(homeManagerTask, INITIAL_DELAY, TICK_SPEED, TICK_SPEED_UNIT);
	}
	
	/**
	 * This class defines the main task that will be executed periodically
	 */
	private final class homeManagerTask extends Thread {

		/**
		 * This is the main execution of the thread
		 */
		public void run() {
			// this monitors the energy usage
			monitorEnergy();
			// depending on whether or not aircon is adjusting temp
			if (tempAdjustTime <= 0) {
				// this evaluates location status and switch modes of temp sensor
				evaluateLocation();
				// the intelligence of temperature is defined in its method
				controlTemperature(Integer.parseInt(temperature), usersLocation.getStatus(), usersLocation.getWhosHome());
				// this is executed periodically, so thread sleeps 1s
			} else {
				// decrement time if aircon is adjusting temp
				tempAdjustTime--;
			}
		}
	}
	
	/**
	 * This method will exit the current sensor, as well as close down the client stub. 
	 * The server stub will be closed because this method is triggered by the server stub
	 */
	public void exit(){
		// cancel future scheduled tasks and shut down the scheduler. if anything is running, don't interrupt it
		scheduleFuture.cancel(DONT_INTERRUPT_IF_RUNNING);
		scheduler.shutdown();
		
		// close the client stub
		controller.exit();
		
		// exit the entire program
		System.exit(0);
	}
  
} 