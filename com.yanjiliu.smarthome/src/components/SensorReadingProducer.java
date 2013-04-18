package components;

import org.avis.client.*;

import pseudoRPC.Message;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SensorReadingProducer extends Thread {
	
	private static FileReader fr;
	private static BufferedReader br;
	private String type, fileName, elvinURL, lineContent, value, userName, mode;
	private static int period, numValue, preValue;
	private String[] values;
	private Elvin elvin;
	private volatile boolean EXIT;
	private static Message message;

	/**
	 * Constructor
	 * @param type
	 * @param fileName
	 * @param elvinURL
	 */
	public SensorReadingProducer(String type, String fileName, String elvinURL) {
		this.type = type;
		if (type == Message.TYPE_LOCATION) {
			this.userName = parseLocationUserName(fileName);
		}
		this.fileName = fileName;
		this.elvinURL = elvinURL;
		period = 0;
		mode = Message.PERIODIC;
		this.EXIT = false;
		preValue = 0;
		numValue = 0;
	}

	/**
	 * run method for this thread
	 */
	public void run(){
		message = new Message(elvinURL);
		
		// if it's location sensor. we need to send registration data to home manager to register the user
		if (type == Message.TYPE_LOCATION) {
			registerUser();
		}
		
		// read data from given file
		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		// parse the file regardless of the type
		while(!this.EXIT) {
			
			// reads the next line. If reached the end of file, start over on the same file
			try {
				if((lineContent = br.readLine()) == null) {
					fr.close();
					fr = new FileReader(fileName);
					lineContent = br.readLine();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// parse the line. from format, first is value, second field is timeTicker
			values = lineContent.split(",");
			value = values[0];
			period = Integer.parseInt(values[1]);
			
			// for the given period of time, keep sending notification to Elvin with the same value
			while (period > 0 && !this.EXIT){
				// depends on the type, put notifications on Elvin
				if (type == Message.TYPE_TEMPERATURE && mode == Message.NON_PERIODIC) {
					sendNonPeriodicTempNot(type, value);
				} else {
					sendNotification(type, value);
				}
				
				// We only need to pipe data every second
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// decrement period
				period--;
			}
		}
		
	}

	/**
	 * This method determines what values to put on Elvin when it's a temperature sensor in Non Periodic mode
	 * @param type
	 * @param value
	 */
	private void sendNonPeriodicTempNot(String type, String value) {
		numValue = Integer.parseInt(value);
		System.out.println("I'm in sendNonPeriodic");
		if ((numValue < Message.AWAY_MAX_TEMP || numValue > Message.AWAY_MIN_TEMP) && (numValue != preValue)) {
			System.out.println("I sent out notifications");
			sendNotification(type, value);
		}
		preValue = numValue;
	}

	/**
	 * This method sends the value notification to home manager
	 * @param type
	 * @param value
	 */
	private void sendNotification(String type, String value) {
		message.clear();
		message.setFrom(Message.SENSOR_NAME);
		message.setTo(Message.HOME_MANAGER_SERVER_STUB);
		message.setType(type);
		message.setValue(value);
		
		if (type == Message.TYPE_LOCATION) {
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
	public void changeTemperatureMode(String mode) {
		System.out.println("YES! I'm in changeTemperatureMode");
		this.mode = mode;
	}

	/**
	 * This little helper function parses the file name and returns the name of the user when the sensor is location sensor
	 * @param file
	 * @return
	 */
	private String parseLocationUserName(String fileName) {
		// we assume the file name is "<username1> Location.txt"
		return fileName.substring(fileName.length() - 13, fileName.length()-1);
	}
	
	/**
	 * This method will send a notification to the home manager server stub to register user
	 * this is only called once when a location sensor data file is loaded
	 */
	private void registerUser() {
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
		this.EXIT = true;
		if (type == Message.TYPE_LOCATION){
			deregisterUser();
		}
		elvin.close();
		br.close();
		fr.close();
		Thread.currentThread().interrupt();
	}

}
