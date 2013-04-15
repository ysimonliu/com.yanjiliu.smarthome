package sensors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Sensor{
	
	private static String type, fileName, elvinURL;
	public final static String ELVIN_URL = "elvin://0.0.0.0:2917";
	
	/**
	 * main method
	 * @author simonliu
	 */
	public static void main(String[] args){
		if (args.length == 3) {
			type = args[0];
			fileName = args[1];
			elvinURL = args[2];
		} else {
			System.exit(1);
		}
		SensorReadingProducer srp = new SensorReadingProducer(type, fileName, elvinURL);
		srp.start();
	}
	

}
