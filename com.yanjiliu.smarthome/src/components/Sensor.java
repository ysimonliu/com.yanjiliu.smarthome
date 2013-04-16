package components;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.avis.client.Elvin;
import org.avis.client.NotificationEvent;
import org.avis.client.NotificationListener;
import org.avis.client.Subscription;

public class Sensor{
	
	// below is only for test data
	public final static String FILENAME = "H:\\git\\com.yanjiliu.smarthome\\com.yanjiliu.smarthome\\src\\sensors\\Temperature.txt";
	public final static String ELVIN_URL = "elvin://0.0.0.0:2917";
	// end of test data
	private static String type, fileName, elvinURL;
	private static Elvin elvin;
	
	/**
	 * main method
	 * @author Yanji Liu
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
			
		// start a separate thread to produce sensor readings, this process is marked as final for shutoff and changeMode use
		final SensorReadingProducer srp = new SensorReadingProducer(type, fileName, elvinURL);
		srp.start();
		
		// subscribe to elvin instructions
    	try{
    		elvin = new Elvin(elvinURL);
    		Subscription sub = elvin.subscribe("TYPE == 'sensor'"); 
    		sub.addListener(new NotificationListener(){
    			public void notificationReceived(NotificationEvent event){
    				// if told to shutdown, do it
    				if(event.notification.get("VALUE") == "shutdown") {
    					try {
							srp.exitSensor();
							// close elvin and current program
							elvin.close();
							System.exit(0);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    				}
    				// if type is temperature, then listen for mode changing instructions
    				else if (type == "temperature"){
    					if(event.notification.get("VALUE") == SensorReadingProducer.PERIODIC) {
    						srp.changeTemperatureMode("periodic");
    					}
    					else if (event.notification.get("VALUE") == SensorReadingProducer.NON_PERIODIC) {
    						srp.changeTemperatureMode("nonperiodic");
    					}
    				}
               }
             });
    		} catch (Exception e){
    			e.printStackTrace();
    		}
	}

}
