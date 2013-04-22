package components;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import pseudoRPC.Message;
import pseudoRPC.SensorPseudoRPCClientStub;
import pseudoRPC.SensorPseudoRPCServerStub;

public class Sensor {
	
	// below is only for test data
	//public final static String TEMP_TEST_NAME = "H:\\git\\com.yanjiliu.smarthome\\com.yanjiliu.smarthome\\src\\testFiles\\Temperature.txt";
	//public final static String USER1_LOCATION_TEST_NAME = "H:\\git\\com.yanjiliu.smarthome\\com.yanjiliu.smarthome\\src\\testFiles\\User1Location.txt";
	//public final static String USER2_LOCATION_TEST_NAME = "H:\\git\\com.yanjiliu.smarthome\\com.yanjiliu.smarthome\\src\\testFiles\\User2Location.txt";
	//public final static String ENERGY_TEST_FILE = "H:\\git\\com.yanjiliu.smarthome\\com.yanjiliu.smarthome\\src\\testFiles\\Energy.txt";

	// scheduler for tasks
	private static ScheduledFuture<?> scheduleFuture;
	private static ScheduledExecutorService scheduler;
	private final int INITIAL_DELAY = 0;
	private final int TICK_SPEED = 1;
	private final TimeUnit TICK_SPEED_UNIT = TimeUnit.SECONDS;
	private static final int NUM_THREADS = 1;
	private static final boolean DONT_INTERRUPT_IF_RUNNING = false;
	// Pseudo RPC
	private SensorPseudoRPCServerStub server;
	private SensorPseudoRPCClientStub controller;
	// parameters taken from standard input
	private static String sensorTypeInput, fileNameInput, elvinURL;
	// other variables
	private String[] values;
	private String fileName, sensorType, line, value, tempSensorMode, userName;
	private int period;
	private FileReader fr;
	private BufferedReader br;
	  
	// constructor
	public Sensor(String sensorTypeInput, String fileNameInput){
		this.sensorType = sensorTypeInput;
		this.fileName = fileNameInput;
		
		// initialize RPC
		server = new SensorPseudoRPCServerStub(elvinURL, this);
		controller = new SensorPseudoRPCClientStub(elvinURL, this);
		
		// if it's a location sensor, we need to register user first to notify the home manager server stub
		if (sensorType.equals(Message.TYPE_LOCATION)) {
			setUserNameFromFileName(fileName);
		}
		
		// initialize tempSensorMode
		if (sensorType.equals(Message.TYPE_TEMPERATURE)) {
			this.tempSensorMode = Message.PERIODIC;
		}
		
		scheduler = Executors.newScheduledThreadPool(NUM_THREADS);
		// open the data file to stream
		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		if (args.length == 3) {
			sensorTypeInput = args[0];
			fileNameInput = args[1];
			elvinURL = args[2];
		} else {
			System.out.println("Error: number of input parameters need to be 3");
			System.exit(1);
		}
		
		/*sensorTypeInput = Message.TYPE_ENERGY;
		elvinURL = Message.DEFAULT_ELVIN_URL;
		fileNameInput = ENERGY_TEST_FILE;*/
		
		// instantiate sensor
		Sensor sensor = new Sensor(sensorTypeInput, fileNameInput);
		
		// activate the sensor
		sensor.activateSensor();
	}
	
	/**
	 * This method will activate the sensor and send periodic notifications onto elvin
	 */
	private void activateSensor(){
		Thread sensorTask = new SensorTask();
		scheduleFuture = scheduler.scheduleWithFixedDelay(sensorTask, INITIAL_DELAY, TICK_SPEED, TICK_SPEED_UNIT);
	}
	
	/**
	 * This class defines the main task that will be executed periodically
	 */
	private final class SensorTask extends Thread {

		/**
		 * This is the main execution of the thread
		 */
		public void run() {
			// if period of last line = 0, then it's time to read the next line
			if (period <= 0) {
				// reads the next line. If reached the end of file, start over on the same file
				try {
					if((line = br.readLine()) == null) {
						fr.close();
						fr = new FileReader(fileName);
						br = new BufferedReader(fr);
						line = br.readLine();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
						
				// parse the line. from format, first is value, second field is timeTicker
				values = line.split(",");
				value = values[0];
				period = Integer.parseInt(values[1]);
			}
				
			// depends on the type, put notifications on Elvin
			if (sensorType.equals(Message.TYPE_TEMPERATURE) && tempSensorMode.equals(Message.NON_PERIODIC)) {
				controller.sendNonPeriodicTempNot(value);
			} else {
				controller.sendNotification(value);
			}
							
			// decrement period
			period--;
		}
	}
	
	/**
	 * This method parses out the user name from the data file name
	 * @param fileName
	 */
	private void setUserNameFromFileName(String fileName) {
		// this approach may be a bit hacky, but basically I cut off the last 12 characters of the fileName
		// which should leave us the user name
		setUserName(fileName.substring(0, fileName.length() - 12));
	}

	public void setTemperatureMode(String mode) {
		// if the input is legal, then set, else don't
		if (mode.equals(Message.PERIODIC) || mode.equals(Message.NON_PERIODIC)) {
			this.tempSensorMode = mode;
		}
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getSensorType() {
		return this.sensorType;
	}
	
	public String getUserName() {
		return this.userName;
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