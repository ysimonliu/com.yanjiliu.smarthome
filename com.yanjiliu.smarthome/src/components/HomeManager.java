package components;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


import pseudoRPC.HomeManagerPseudoRPCClientStub;
import pseudoRPC.HomeManagerPseudoRPCServerStub;
import pseudoRPC.Message;
import pseudoRPC.SensorPseudoRPCClientStub;
import pseudoRPC.SensorPseudoRPCServerStub;

public class HomeManager {
	
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
	private int currentTemperature;
	private String tempAdjustLog;
	private int tempAdjustTime;
	// energy tracking
	private int currentEnergy, previousEnergy;
	// input parameter
	private static String elvinURLInput;
	// helper variables
	private static final String EOL = System.getProperty("line.separator"); 
	  
	// constructor
	public HomeManager(String elvinURL){
		// initialize userslocation
		this.usersLocation = new UsersLocation();
		// initialize RPC
		this.server = new HomeManagerPseudoRPCServerStub(elvinURL, this);
		this.controller = new HomeManagerPseudoRPCClientStub(elvinURL);
		// initialize scheduler
		this.scheduler = Executors.newScheduledThreadPool(NUM_THREADS);
		// initialize temperature, temp adjustment time and temp adjustment log
		this.currentTemperature = 0;
		this.tempAdjustTime = 0;
		this.tempAdjustLog = "";
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
				// control the aircon to adjust temperature
				controlTemperature(currentTemperature, usersLocation);
				// this is executed periodically, so thread sleeps 1s
			} else {
				// decrement time if aircon is adjusting temp
				tempAdjustTime--;
			}
		}
	}

	/**
	 * This method controls temperature based on current temperature and whether someone's home
	 * @param currentTemperature
	 * @param usersLocation
	 */
	public void controlTemperature(int currentTemperature, UsersLocation usersLocation) {
		if (usersLocation.getStatus().equals(Message.STATUS_HOME)) {
			if (currentTemperature != Message.HOME_TEMP) {
				adjustTemp(currentTemperature, usersLocation.getWhosHome());
			}
		} else if (usersLocation.getStatus().equals(Message.STATUS_AWAY)) {
			if (currentTemperature < Message.AWAY_MIN_TEMP || currentTemperature > Message.AWAY_MAX_TEMP) {
				adjustTemp(currentTemperature, usersLocation.getWhosHome());
			}
		}
	}

	/**
	 * This method will start adjusting the temperature
	 * @param currentTemperature
	 * @param whosHome
	 */
	private void adjustTemp(int currentTemperature, String[] whosHome) {
		this.tempAdjustTime = 5;
		this.tempAdjustLog += "Air-conditioning adjusted." + EOL + "Temperature: at " + currentTemperature + " degrees" + EOL +
				"At Home: " + whosHome(whosHome) + EOL;
	}

	/**
	 * Given the String[], this method flattens the string output with a comma in between elements
	 * @param stringArray
	 * @return
	 */
	private static String whosHome(String[] homeArray) {
		switch (homeArray.length) {
		case 1: return homeArray[0];
		case 2: return homeArray[0] + " and " + homeArray[1];
		default: return "";
		}
	}

	public void evaluateLocation() {
		// only send notification when who's home is now different than last time
		if (!usersLocation.getPreviousStatus().equals(usersLocation.getStatus())) {
			switch(usersLocation.getStatus()) {
			case Message.STATUS_AWAY: controller.switchTempMode(Message.NON_PERIODIC);
				break;
			case Message.STATUS_HOME: controller.switchTempMode(Message.PERIODIC);
				break;
			}
		}
	}

	public void monitorEnergy() {
		if (currentEnergy > 4000 && (currentEnergy != previousEnergy)) {
			controller.warnUI(String.valueOf(currentEnergy));
		}
	}
	
	public void setTemperature(String temperature) {
		this.currentTemperature = Integer.parseInt(temperature);
	}
	
	public void setEnergy(String energy) {
		this.previousEnergy = this.currentEnergy;
		this.currentEnergy = Integer.parseInt(energy);
	}
	
	public UsersLocation getUsersLocation() {
		return this.usersLocation;
	}
	
	public String getTempAdjustLog() {
		// if the log is empty then return "Log of temperature adjustment is empty"
		if (this.tempAdjustLog.isEmpty()) {
			return "Log of temperature adjustment is empty";
		}
		return this.tempAdjustLog;
	}
	
	public String getMediaFiles() {
		return controller.requestFromEMM(Message.GET_FILES, "");
	}

	public String getTracks(String value) {
		return controller.requestFromEMM(Message.GET_TRACKS, value);
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