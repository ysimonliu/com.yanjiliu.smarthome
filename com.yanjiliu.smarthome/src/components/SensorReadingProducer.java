package components;

import org.avis.client.*;
import org.avis.common.InvalidURIException;

import PseudoRPC.Message;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ConnectException;

public class SensorReadingProducer extends Thread {
	
	private static FileReader fr;
	private static BufferedReader br;
	private String type, fileName, elvinURL, lineContent, value, mode;
	private static int period, numValue, preValue;
	private String[] values;
	private Elvin elvin;
	private Notification notification;
	private static volatile boolean EXIT;
	private static Message message;

	/**
	 * Constructor
	 * @param type
	 * @param fileName
	 * @param elvinURL
	 */
	public SensorReadingProducer(String type, String fileName, String elvinURL) {
		this.type = type;
		this.fileName = fileName;
		this.elvinURL = elvinURL;
		period = 0;
		mode = Message.PERIODIC;
		EXIT = false;
		preValue = 0;
		numValue = 0;
	}

	/**
	 * run method for this thread
	 */
	public void run(){
		message = new Message(elvinURL);
		
		// read data from given file
		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// parse the file regardless of the type
		while(!EXIT) {
			
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
			while (period > 0 && !EXIT){
				// depends on the type, put notifications on Elvin
				if (type == "temperature" && mode == Message.NON_PERIODIC) {
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
		if ((numValue < Message.AWAY_MAX_TEMP || numValue > Message.AWAY_MIN_TEMP) && (numValue != preValue)) {
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
		message.setTo(Message.HOME_MANAGER_NAME);
		message.setQuery(type);
		message.setValue(value);
		message.sendNotification();
	}
	
	/**
	 * This method changes the mode of notification of temperature sensor readings
	 * @param mode
	 */
	public void changeTemperatureMode(String mode) {
		if (type == "temperature"){
			this.mode = mode;
		}
	}

	/**
	 * This class will exit the current sensor
	 * @throws IOException 
	 */
	public void exitSensor() throws IOException {
		EXIT = true;
		elvin.close();
		br.close();
		fr.close();
		Thread.currentThread().interrupt();
	}

}
