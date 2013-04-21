package pseudoRPC;

import java.io.IOException;

import org.avis.client.*;

/**
 * This is the client stub for Home Manager Pseudo RPC
 * the client stub is responsible for sending out messages.
 *
 */
public class HomeManagerPseudoRPCClientStub {
	
	private static Message message;
	private String elvinURL;
	private Elvin elvin;
	private String criteria;
	private static String result;
	private Subscription response;
	private static Object lock = new Object();
	
	/**
	 * Constructor - takes elvinURL to connect to the server, also to instantiate the message class
	 * @param elvinURL
	 */
	public HomeManagerPseudoRPCClientStub(String elvinURL) {
		this.elvinURL = elvinURL;
		message = new Message(elvinURL);
	}
	
	/**
	 * Request from EMM of something
	 * @param elvinURL
	 * @param query
	 * @param value
	 * @return
	 */
	public String requestFromEMM(String query, String value) {
		
		// connect to the server
		try {
			elvin = new Elvin(elvinURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// set up listener for the response. during this period, block calling
		criteria = Message.criteriaBuilder(Message.FROM, Message.EMM_NAME) + " && " +
			Message.criteriaBuilder(Message.TO, Message.HOME_MANAGER_CLIENT_STUB) + " && " +
			Message.criteriaBuilder(Message.QUERY, query);
		// if value is provided then 
		if (!value.isEmpty()) {
			criteria = criteria + " && " + Message.criteriaBuilder(Message.VALUE, value);
		}
				
		try {
			response = elvin.subscribe(criteria);
			response.addListener(new NotificationListener() {
				public void notificationReceived(NotificationEvent event) {
					result = event.notification.getString(Message.RESPONSE);
					synchronized(lock) {
						lock.notify();
					}
					// remove this subscription and close elvin connection
					try {
						response.remove();
					} catch (IOException e) {
						e.printStackTrace();
					}
					elvin.close();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		// send request message on the server
		message.clear();
		message.setFrom(Message.HOME_MANAGER_CLIENT_STUB);
		message.setTo(Message.EMM_NAME);
		message.setQuery(query);
		if (value != null) {
			message.setValue(value);
		}
		message.sendNotification();

		// block calls until result is returned
		synchronized(lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// return the result
		return result;
	}
	
	/**
	 * This method sends out a notification to the UI to warn about energy overusage
	 * @param energyConsumption
	 */
	public void warnUI(String energyConsumption) {
		message.clear();
		message.setFrom(Message.HOME_MANAGER_CLIENT_STUB);
		message.setTo(Message.SMART_UI_NAME);
		message.setQuery(Message.WARN);
		message.setValue(energyConsumption);
		message.sendNotification();
	}
	
	/**
	 * This method sends out a notification to shut off a component
	 * @param to - destination component
	 * @return 
	 */
	public void shutdownComponent(String componentToShutDown) {
		message.clear();
		message.setFrom(Message.HOME_MANAGER_CLIENT_STUB);
		message.setTo(componentToShutDown);
		message.setQuery(Message.SHUTDOWN);
		message.sendNotification();
	}
	
	/**
	 * This method sends out a notification to shut off a sensor
	 * @param to - destination component
	 * @return 
	 */
	public void shutdownComponent(String componentToShutDown, String sensorType) {
		message.clear();
		message.setFrom(Message.HOME_MANAGER_CLIENT_STUB);
		message.setTo(componentToShutDown);
		message.setType(sensorType);
		message.setQuery(Message.SHUTDOWN);
		message.sendNotification();
	}
	
	/**
	 * This method sends out a notification to temperature sensor to switch mode
	 * @param mode - periodic? OR non-periodic?
	 */
	public void switchTempMode(String mode){
		message.clear();
		message.setFrom(Message.HOME_MANAGER_CLIENT_STUB);
		message.setTo(Message.SENSOR_NAME);
		message.setType(Message.TYPE_TEMPERATURE);
		message.setQuery(mode);
		
		message.sendNotification();
	}
	
	/**
	 * exit the client stub
	 */
	public void exit() {
		// notify the all sensors to shut down, if any is running
		shutdownComponent(Message.SENSOR_NAME, Message.TYPE_ENERGY);
		shutdownComponent(Message.SENSOR_NAME, Message.TYPE_LOCATION);
		shutdownComponent(Message.SENSOR_NAME, Message.TYPE_TEMPERATURE);
		
		// notify the EMM to shut down, if any instance is running
		shutdownComponent(Message.EMM_NAME);
		
		// destroy the message by closing elvin connection
		message.destroy();
	}
	
}
