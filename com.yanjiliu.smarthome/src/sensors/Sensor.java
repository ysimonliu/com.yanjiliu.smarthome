package sensors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Sensor{

	private static FileReader fr;
	private static BufferedReader br;
	private static String type, filePath, elvinURL;
	
	/**
	 * This class starts reading data from a given file
	 * @param fileName
	 * @throws FileNotFoundException 
	 */
	public void readData(String filePath) throws FileNotFoundException {
		fr = new FileReader(filePath);
		br = new BufferedReader(fr);
	}
	
	/**
	 * main method
	 * @author simonliu
	 */
	public static void main(String[] args){
		if (args.length == 3) {
			type = args[0];
			filePath = args[1];
			elvinURL = args[2];
		} else {
			System.exit(1);
		}
	}
	
	/**
	 * This class will exit the current sensor
	 * @throws IOException 
	 */
	public void exitSensor() throws IOException {
		br.close();
		fr.close();
	}
}
