package sensors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Sensor{
	
	public final static String FILENAME = "H:\\git\\com.yanjiliu.smarthome\\com.yanjiliu.smarthome\\src\\sensors\\Temperature.txt";
	private static String type, fileName, elvinURL;
	public final static String ELVIN_URL = "elvin://0.0.0.0:2917";
	
	/**
	 * main method
	 * @author simonliu
	 */
	public static void main(String[] args){
		//if (args.length == 3) {
			// TODO: correct it back to arg[1] after test
			//type = args[0];
			//fileName = args[1];
			//elvinURL = args[2];
			type = "temperature";
			fileName = FILENAME;
			elvinURL = ELVIN_URL;
		//} else {
		//	System.exit(1);
		//}
		SensorReadingProducer srp = new SensorReadingProducer(type, fileName, elvinURL);
		srp.start();
		
		try {
			Thread.sleep(200000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			srp.exitSensor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
