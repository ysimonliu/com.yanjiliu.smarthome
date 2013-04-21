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

public final class Sensor {
	
	// below is only for test data
	public final static String TEMP_TEST_NAME = "H:\\git\\com.yanjiliu.smarthome\\com.yanjiliu.smarthome\\src\\testFiles\\Temperature.txt";
	//public final static String USER1_LOCATION_TEST_NAME = "H:\\git\\com.yanjiliu.smarthome\\com.yanjiliu.smarthome\\src\\testFiles\\User1Location.txt";
	//public final static String USER2_LOCATION_TEST_NAME = "H:\\git\\com.yanjiliu.smarthome\\com.yanjiliu.smarthome\\src\\testFiles\\User2Location.txt";
	
	// scheduler for tasks
	private static ScheduledFuture<?> scheduleFuture;
	private static ScheduledExecutorService scheduler;
	private final int INITIAL_DELAY = 0;
	private final int TICK_SPEED = 1;
	private final TimeUnit TICK_SPEED_UNIT = TimeUnit.SECONDS;
	private static final int NUM_THREADS = 1;
	private static final boolean DONT_INTERRUPT_IF_RUNNING = false;
	// Pseudo RPC
	private static SensorPseudoRPCServerStub server;
	private static SensorPseudoRPCClientStub controller;
	// parameters taken from standard input
	private static String sensorTypeInput, fileNameInput, elvinURL;
	// other variables
	private String[] values;
	private String fileName, sensorType, line, value, tempSensorMode;
	private int period;
	private FileReader fr;
	private BufferedReader br;
	  
	// constructor
	public Sensor(String sensorTypeInput, String fileNameInput){
		this.sensorType = sensorTypeInput;
		this.fileName = fileNameInput;
		
		// initialize tempSensorMode
		this.tempSensorMode = Message.PERIODIC;
		
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
		//FIXME: change back after test
		/*if (args.length == 3) {
			sensorTypeInput = args[0];
			fileNameInput = args[1];
			elvinURL = args[2];
		} else {
			System.exit(1);
		}
		*/
		sensorTypeInput = Message.TYPE_TEMPERATURE;
		elvinURL = Message.DEFAULT_ELVIN_URL;
		fileNameInput = TEMP_TEST_NAME;
		
		
		// instantiate sensor
		Sensor sensor = new Sensor(sensorTypeInput, fileNameInput);
		
		// initialize RPC
		server = new SensorPseudoRPCServerStub(elvinURL, sensor);
		controller = new SensorPseudoRPCClientStub(elvinURL);
		
		// activate the sensor
		sensor.activateSensor();
	}
	
	private void activateSensor(){
		Thread sensorTask = new SensorTask();
		scheduleFuture = scheduler.scheduleWithFixedDelay(sensorTask, INITIAL_DELAY, TICK_SPEED, TICK_SPEED_UNIT);
	}
	
	private final class SensorTask extends Thread {

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
				controller.sendNonPeriodicTempNot(sensorType, value);
			} else {
				controller.sendNotification(sensorType, value);
			}
							
			// decrement period
			period--;
		}
	}
  
	public void exit(){
		// cancel future scheduled tasks and shut down the scheduler. if anything is running, don't interrupt it
		scheduleFuture.cancel(DONT_INTERRUPT_IF_RUNNING);
		scheduler.shutdown();
		// exit the entire program
		System.exit(0);
	}

	public void setTemperatureMode(String mode) {
		this.tempSensorMode = mode;
	}
	
	public String getSensorType() {
		return this.sensorType;
	}
  
} 