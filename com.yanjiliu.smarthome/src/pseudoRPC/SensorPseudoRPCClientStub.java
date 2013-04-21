package pseudoRPC;

import org.avis.client.*;



import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SensorPseudoRPCClientStub extends Thread {
	


	/**
	 * Constructor
	 * @param type
	 * @param fileName
	 * @param elvinURL
	 */
	public SensorPseudoRPCClientStub(String elvinURL) {
		this.type = type;
		if (type.equals(Message.TYPE_LOCATION)) {
			this.userName = parseLocationUserName(fileName);
		}
		this.fileName = fileName;
		this.elvinURL = elvinURL;
		period = 0;
		if (type.equals(Message.TYPE_TEMPERATURE)) {
			tempMode = Message.PERIODIC;
		}
		this.EXIT = false;
		preValue = 0;
		numValue = 0;
	}
	
	/**
	 * This method determines what values to put on Elvin when it's a temperature sensor in Non Periodic mode
	 * @param type
	 * @param value
	 */
	public void sendNonPeriodicTempNot(String type, String value) {
		numValue = Integer.parseInt(value);
		if ((numValue > Message.AWAY_MAX_TEMP || numValue < Message.AWAY_MIN_TEMP) && (numValue != preValue)) {
			sendNotification(type, value);
		}
		preValue = numValue;
	}

	/**
	 * This method sends the value notification to home manager
	 * @param type
	 * @param value
	 */
	public void sendNotification(String type, String value) {
		message.clear();
		message.setFrom(Message.SENSOR_NAME);
		message.setTo(Message.HOME_MANAGER_SERVER_STUB);
		message.setType(type);
		message.setValue(value);
		
		if (type.equals(Message.TYPE_LOCATION)) {
			message.setUser(userName);
			// FIXME: this is for testing
			//System.out.println("USER: " + message.getUser());
		}
		message.sendNotification();
	}
	
	/**
	 * This method changes the mode of notification of temperature sensor readings
	 * @param mode
	 */
	public void setTemperatureMode(String tempMode) {
		this.tempMode = tempMode;
	}

	/**
	 * This little helper function parses the file name and returns the name of the user when the sensor is location sensor
	 * @param file
	 * @return
	 */
	private String parseLocationUserName(String fileName) {
		// we assume the file name is "<username1> Location.txt"
		return fileName.substring(0, fileName.length()-12);
	}
	
	/**
	 * This method will send a notification to the home manager server stub to register user
	 * this is only called once when a location sensor data file is loaded
	 */
	public void registerUser() {
		message.clear();
		message.setFrom(Message.SENSOR_NAME);
		message.setTo(Message.HOME_MANAGER_SERVER_STUB);
		message.setType(type);
		message.setQuery(Message.VALUE_REGISTRATION);
		message.setUser(userName);
		message.sendNotification();
	}
	
	/**
	 * This method deregisters user from home manager server stub
	 */
	private void deregisterUser() {
		message.clear();
		message.setFrom(Message.SENSOR_NAME);
		message.setTo(Message.HOME_MANAGER_SERVER_STUB);
		message.setType(type);
		message.setQuery(Message.VALUE_REGISTRATION);
		message.setUser(userName);
		message.sendNotification();
	}
	
	/**
	 * This class will exit the current sensor
	 * @throws IOException 
	 */
	public void exitSensor() throws IOException {
		System.out.println("DEBUG: EXITING sensor");
		this.EXIT = true;
		if (type.equals(Message.TYPE_LOCATION)){
			deregisterUser();
		}
		br.close();
		fr.close();
		message.destroy();
		Thread.currentThread().interrupt();
	}

}
