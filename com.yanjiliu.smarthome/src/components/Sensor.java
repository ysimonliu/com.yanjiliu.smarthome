package components;

import java.io.IOException;
import org.avis.client.Elvin;
import org.avis.client.NotificationEvent;
import org.avis.client.NotificationListener;
import org.avis.client.Subscription;

import PseudoRPC.Message;

public class Sensor{
	
	// below is only for test data
	public final static String FILENAME = "H:\\git\\com.yanjiliu.smarthome\\com.yanjiliu.smarthome\\src\\sensors\\Temperature.txt";
	// end of test data
	private static String type, fileName, elvinURL;
	private static Elvin elvin;
	// different sensor types
	public final static String TYPE_TEMPERATURE = "temperature";
	public final static String TYPE_LOCATION = "location";
	public final static String TYPE_ENERGY = "energy";
	
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
			type = TYPE_TEMPERATURE;
			fileName = FILENAME;
			elvinURL = Message.DEFAULT_ELVIN_URL;
		//} else {
		//	System.exit(1);
		//}
			
		// start a separate thread to produce sensor readings, this process is marked as final for shutoff and changeMode use
		final SensorReadingProducer srp = new SensorReadingProducer(type, fileName, elvinURL);
		srp.start();
		
		// subscribe to elvin instructions
    	try{
    		elvin = new Elvin(elvinURL);
    		Subscription sub = elvin.subscribe(Message.QUERY + " == " + type);
    		sub.addListener(new NotificationListener(){
    			public void notificationReceived(NotificationEvent event){
    				// if told to shutdown, do it
    				if(event.notification.get(Message.QUERY) == Message.SHUTDOWN) {
    					try {
							srp.exitSensor();
							// close elvin and current program
							elvin.close();
							System.exit(0);
						} catch (IOException e) {
							e.printStackTrace();
						}
    				}
    				// if type is temperature, then listen for mode changing instructions
    				else if (type == TYPE_TEMPERATURE){
    					if(event.notification.get(Message.QUERY) == Message.PERIODIC) {
    						srp.changeTemperatureMode(Message.PERIODIC);
    					}
    					else if (event.notification.get(Message.QUERY) == Message.NON_PERIODIC) {
    						srp.changeTemperatureMode(Message.NON_PERIODIC);
    					}
    				}
               }
             });
    		} catch (Exception e){
    			e.printStackTrace();
    		}
	}

}
