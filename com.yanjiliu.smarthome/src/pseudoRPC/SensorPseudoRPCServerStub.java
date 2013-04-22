package pseudoRPC;

import java.io.IOException;

import org.avis.client.Elvin;
import org.avis.client.NotificationEvent;
import org.avis.client.NotificationListener;
import org.avis.client.Subscription;

import components.Sensor;

public class SensorPseudoRPCServerStub {
	
	private Sensor sensor;
	private Elvin elvin;
	private Subscription sub;

	private NotificationListener SensorListener = new NotificationListener(){
		// upon notification received, execute the following actions
		public void notificationReceived(NotificationEvent event){
			String query = event.notification.getString(Message.QUERY);
			// if told to shutdown, do it
			if(query.equals(Message.SHUTDOWN)) {
				try {
					sensor.exit();
					// remove subscription and close elvin connection
					sub.remove();
					elvin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// if type is temperature, then listen for mode changing instructions
			else if (sensor.getSensorType().equals(Message.TYPE_TEMPERATURE)){
				if(query.equals(Message.PERIODIC)) {
					sensor.setTemperatureMode(Message.PERIODIC);
				}
				else if (query.equals(Message.NON_PERIODIC)) {
					sensor.setTemperatureMode(Message.NON_PERIODIC);
				}
			}
       }
	};
	
	/**
	 * Constructor
	 * @param elvinURL
	 * @param sensor
	 */
	public SensorPseudoRPCServerStub(String elvinURL, Sensor sensor) {
		this.sensor = sensor;
		// connect to elvin server and add listener subscription
		try {
			elvin = new Elvin(elvinURL);
			String criteria = Message.criteriaBuilder(Message.TO, Message.SENSOR_NAME) + " && " + 
					Message.criteriaBuilder(Message.TYPE, sensor.getSensorType());
			sub = elvin.subscribe(criteria);
			sub.addListener(SensorListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
