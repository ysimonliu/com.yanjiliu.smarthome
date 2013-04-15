package sensors;

import org.avis.client.*;
import org.avis.common.InvalidURIException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ConnectException;

public class SensorReadingProducer extends Thread {
	
	private static FileReader fr;
	private static BufferedReader br;
	private String type, fileName, elvinURL;
	private static int period;
	private String lineContent = "", value = "";
	private String[] values;
	private Elvin elvin;
	private Notification notification;

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
	}

	/**
	 * run method for this thread
	 */
	public void run(){
		// connect to elvin server
		try {
			elvin = new Elvin(elvinURL);
			System.out.println(type + " producer has connected to Elvin");
		} catch (ConnectException e1) {
			e1.printStackTrace();
		} catch (InvalidURIException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// read data from given file
		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// parse the file regardless of the type
		while(true) {
			
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
			for(int i = period; i > 0; i--) {
				// depends on the type, put notifications on Elvin
				sendNotification(type, value);
			
				// We only need to pipe data every second
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * This method sends a type and value tuple onto Elvin
	 * @param type
	 * @param value
	 */
	private void sendNotification(String type, String value) {
		// TODO: switch mode for temperature
		try {
			notification = new Notification();
			notification.set("TYPE", type);
			notification.set("VALUE", value);
			elvin.send(notification);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This class will exit the current sensor
	 * @throws IOException 
	 */
	public void exitSensor() throws IOException {
		br.close();
		fr.close();
		Thread.currentThread().interrupt();
	}
}
