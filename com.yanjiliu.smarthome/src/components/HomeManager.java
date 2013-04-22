package components;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
	// location info
	private ArrayList<String> whosHome, previousWhosHome;
	// temp adjustment variables
	private int currentTemperature;
	private String tempAdjustLog;
	private int tempAdjustTime;
	private final int WAIT_FIVE_SECONDS = 4;
	// energy tracking
	private int currentEnergy, previousEnergy;
	// input parameter
	private static String elvinURLInput;
	// helper variables
	private static final String EOL = System.getProperty("line.separator"); 
	  
	// constructor
	public HomeManager(String elvinURL){
		// initialize location info
		this.whosHome = new ArrayList<String> ();
		this.previousWhosHome = whosHome;
		// initialize RPC
		this.server = new HomeManagerPseudoRPCServerStub(elvinURL, this);
		this.controller = new HomeManagerPseudoRPCClientStub(elvinURL);
		// initialize scheduler
		this.scheduler = Executors.newScheduledThreadPool(NUM_THREADS);
		// initialize temperature, temp adjustment time and temp adjustment log
		this.previousEnergy = 0;
		this.currentEnergy = 0;
		this.currentTemperature = Message.HOME_TEMP;
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
			// depending on whether or not aircon on, we evaluate location and temperature
			if (tempAdjustTime <= 0) {
				// this evaluates location status and switch modes of temp sensor
				evaluateLocation();
				// control the aircon to adjust temperature
				controlTemperature(currentTemperature);
				// this is executed periodically, so thread sleeps 1s
			} else {
				// decrement time if aircon is adjusting temp
				tempAdjustTime--;
			}
		}
	}
	
	// monitors energy, if over 4000, warn the UI
	public void monitorEnergy() {
		if (currentEnergy > 4000 && (currentEnergy != previousEnergy)) {
			controller.warnUI(String.valueOf(currentEnergy));
		}
	}

	/**
	 * This method controls temperature based on current temperature and whether someone's home
	 * @param currentTemperature
	 * @param usersLocation
	 */
	public void controlTemperature(int currentTemperature) {
		if (!whosHome.isEmpty()) {
			if (currentTemperature != Message.HOME_TEMP) {
				adjustTemp(currentTemperature);
			}
		} else {
			if (currentTemperature < Message.AWAY_MIN_TEMP || currentTemperature > Message.AWAY_MAX_TEMP) {
				adjustTemp(currentTemperature);
			}
		}
	}

	/**
	 * This method will start adjusting the temperature
	 * @param currentTemperature
	 * @param whosHome
	 */
	private void adjustTemp(int currentTemperature) {
		// we wait for 4 seconds. But because in the if statement it compares with 0, here we set it to 4
		this.tempAdjustTime = WAIT_FIVE_SECONDS;
		this.tempAdjustLog += EOL + "Air-conditioning adjusted." + EOL + "Temperature: at " + currentTemperature + " degrees" + EOL +
				"At Home: " + getWhosHomeInString() + EOL;
	}

	// get who's home in a string, and if two users, connect their names with "and"
	private String getWhosHomeInString() {
		switch (whosHome.size()) {
		case 1: return whosHome.get(0);
		case 2: return whosHome.get(0) + " and " + whosHome.get(1);
		default: return "";
		}
	}

	/**
	 * This method evaluates the location info and set the temperature sensor to different modes
	 */
	public void evaluateLocation() {
		switch(whosHome.size()) {
		case 0: controller.switchTempMode(Message.NON_PERIODIC);
			break;
		default: controller.switchTempMode(Message.PERIODIC);
			break;
		}
	}
	
	public void setTemperature(String temperature) {
		this.currentTemperature = Integer.parseInt(temperature);
	}
	
	public void setEnergy(String energy) {
		this.previousEnergy = this.currentEnergy;
		this.currentEnergy = Integer.parseInt(energy);
	}
	
	/**
	 * This method sets whosHome based on the new data that's coming in, this is called by the server stub
	 * @param userName
	 * @param status
	 */
	public void setWhosHome(String userName, String status) {
		previousWhosHome = whosHome;
		// limit the number of users to 2
		if (this.whosHome.size() == 2 && status.equals(Message.STATUS_HOME) && (!whosHome.contains(userName))) {
			// ignore whatever is sent in
		} else {
			// if status is home, we add/keep the username
			if (status.equals(Message.STATUS_HOME)) {
				if (!whosHome.contains(userName)) {
					whosHome.add(userName);
				}
			}
			// else if the status is away, we remove/don't add username
			else if (status.equals(Message.STATUS_AWAY)){
				if (whosHome.contains(userName)) {
					whosHome.remove(userName);
				}
			}
		}
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