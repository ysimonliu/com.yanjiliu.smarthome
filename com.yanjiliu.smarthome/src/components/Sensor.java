package components;

import java.io.IOException;
import org.avis.client.Elvin;
import org.avis.client.NotificationEvent;
import org.avis.client.NotificationListener;
import org.avis.client.Subscription;

import pseudoRPC.Message;


public class Sensor{
	
	// below is only for test data
	//public final static String TEMP_TEST_NAME = "C:\\git\\com.yanjiliu.smarthome\\com.yanjiliu.smarthome\\src\\testFiles\\Temperature.txt";
	//public final static String USER1_LOCATION_TEST_NAME = "C:\\git\\com.yanjiliu.smarthome\\com.yanjiliu.smarthome\\src\\testFiles\\User1Location.txt";
	//public final static String USER2_LOCATION_TEST_NAME = "C:\\git\\com.yanjiliu.smarthome\\com.yanjiliu.smarthome\\src\\testFiles\\User2Location.txt";

	// end of test data
	private static String type, fileName, elvinURL;
	private static Elvin elvin;
	
	/**
	 * main method
	 * @author Yanji Liu
	 */
	public static void main(String[] args){
		if (args.length == 3) {
			type = args[0];
			fileName = args[1];
			elvinURL = args[2];
		} else {
			System.exit(1);
		}
			
		// start a separate thread to produce sensor readings, this process is marked as final for shutoff and changeMode use
		final SensorReadingProducer srp = new SensorReadingProducer(type, fileName, elvinURL);
		srp.start();
		
		// subscribe to elvin instructions
    	try{
    		elvin = new Elvin(elvinURL);
    		Subscription sub = elvin.subscribe(Message.criteriaBuilder(Message.TO, Message.SENSOR_NAME) + " && " + Message.criteriaBuilder(Message.TYPE, type));
    		sub.addListener(new NotificationListener(){
    			public void notificationReceived(NotificationEvent event){
    				// if told to shutdown, do it
    				if(event.notification.get(Message.QUERY).equals(Message.SHUTDOWN)) {
    					try {
							srp.exitSensor();
							srp.interrupt();
							// close elvin and current program
							elvin.close();
							System.exit(0);
						} catch (IOException e) {
							e.printStackTrace();
						}
    				}
    				// if type is temperature, then listen for mode changing instructions
    				else if (event.notification.getString(Message.TYPE).equals(Message.TYPE_TEMPERATURE)){
    					if(event.notification.get(Message.QUERY).equals(Message.PERIODIC)) {
    						srp.setTemperatureMode(Message.PERIODIC);
    					}
    					else if (event.notification.get(Message.QUERY).equals(Message.NON_PERIODIC)) {
    						srp.setTemperatureMode(Message.NON_PERIODIC);
    					}
    				}
               }
             });
    		} catch (Exception e){
    			e.printStackTrace();
    		}
	}

}
