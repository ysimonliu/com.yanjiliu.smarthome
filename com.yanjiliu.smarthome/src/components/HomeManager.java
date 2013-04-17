package components;

import PseudoRPC.*;

public class HomeManager {
	
	private static String elvinURL;
	private static boolean EXIT;
	private static HomeManagerPseudoRPCServerStub server;
	private static HomeManagerPseudoRPCClientStub controller;
	private static String energy, previousEnergy, temperature, tempAdjustLog;
	private static String[] nowHome, previousHome;
	private static int tempAdjustTime;
	private static final String EOL = System.getProperty("line.separator"); 
	
	/**
	 * main method upon start of this program
	 * @param args
	 */
	public static void main(String[] args) {
		// reads the parameter into variable
		if (args.length == 1) {
			elvinURL = args[0];
		}
		else if (args.length == 0) {
			elvinURL = Message.DEFAULT_ELVIN_URL;
		}
		else {
			System.exit(1);
		}
		
		server = new HomeManagerPseudoRPCServerStub(elvinURL);
		controller = new HomeManagerPseudoRPCClientStub(elvinURL);
		// reset temp adjust time and log
		tempAdjustTime = 0;
		tempAdjustLog = "";
		
		// every second, the home manager will evaluate the energy and temperature
		while (!EXIT) {
			// this monitors the energy usage
			monitorEnergy();
			// depending on whether or not aircon is adjusting temp
			if (tempAdjustTime <= 0) {
				// this evaluates location status and switch modes of temp sensor
				evaluateLocation();
				// the intelligence of temperature is defined in its method
				controlTemperature(Integer.parseInt(temperature), nowHome);
				// this is executed periodically, so thread sleeps 1s
			} else {
				// decrement time if aircon is adjusting temp
				tempAdjustTime--;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method evaluates the location info, and upon a change, switch the mode for temp sensor
	 * @param lastLocation
	 * @param location
	 */
	private static void evaluateLocation() {
		// only send notification when who's home is now different than last time
		if (nowHome != previousHome) {
			switch(nowHome.length) {
			case 0: controller.switchTempMode(Message.NON_PERIODIC);
				break;
			default: controller.switchTempMode(Message.PERIODIC);
				break;
			}
		}
	}

	/**
	 * This method monitors the energy usage
	 */
	private static void monitorEnergy() {
		if (Integer.parseInt(energy) > 4000 && energy != previousEnergy) {
			controller.warnUI(energy);
		}
	}

	/**
	 * This method controls the temperature in the room
	 * depending on the location data as well as the temperature data
	 * Because too much logic here, I keep a copy of currentTemp and currentLocation for use
	 */
	private static void controlTemperature(int currentTemp, String[] nowHome) {
		if (nowHome.length != 0) {
			if (currentTemp != Message.HOME_TEMP) {
				adjustTemp(currentTemp, nowHome);
			}
		}
	}
	
	/**
	 * Adjust temperature
	 * @param currentTemp
	 * @param nowHome2
	 */
	private static void adjustTemp(int currentTemp, String[] nowHome) {
		tempAdjustTime = 5;
		// as the log we plan to keep is not long, I decide to keep it as a string variable in memory
		// I admit that this solution is a bit hacky, but it should be good enough for our test run
		tempAdjustLog += "Air-conditioning adjusted." + EOL + "Temperature: at " + currentTemp + " degrees" + EOL +
				"At Home: " + whosHome(nowHome) + EOL;
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

	/**
	 * setters for energy, accessible by client stub to update data
	 * @param newEnergy
	 */
	public static void setEnergy(String newEnergy) {
		previousEnergy = energy;
		energy = newEnergy;
	}
	
	/**
	 * setters for location, accessible by client stub to update data
	 * @param newLocation
	 */
	public static void setLocation(String[] whosHome) {
		previousHome = nowHome;
		nowHome = whosHome;
	}
	
	/**
	 * setters for temperature, accessible by client stub to update data
	 * @param newTemperature
	 */
	public static void setTemperature(String newTemperature) {
		temperature = newTemperature;
	}
	
	public static String getTempAdjustLog() {
		return tempAdjustLog;
	}
	
	/**
	 * Exit the home manager
	 */
	public void exit(){
		EXIT = true;
		server.exit();
		controller.exit();
		System.exit(0);
	}
}
