package test;

import org.avis.client.*;

import pseudoRPC.Message;

import components.HomeManager;

public class SensorTesting{

    private Elvin elvin;
    private String from, type, query, value, user;

    private NotificationListener testListener = new NotificationListener(){
		// upon notification received, execute the following actions
		public void notificationReceived(NotificationEvent event){
			
			from = event.notification.getString(Message.FROM);
			type = event.notification.getString(Message.TYPE);
			query = event.notification.getString(Message.QUERY);
			value = event.notification.getString(Message.VALUE);
			
			//System.out.println("From: " + from);
			//System.out.println("Type: " + type);
			//System.out.println("Value: " + value);
			if (type.equals(Message.TYPE_LOCATION)) {
				user = event.notification.getString(Message.USER);
				//System.out.println("User: " + user);
			}
			System.out.println();
		}
		
	};
	
	public SensorTesting(String url){

	try{

	    elvin = new Elvin(url);
	    Subscription sub = elvin.subscribe(Message.criteriaBuilder(Message.TO, Message.HOME_MANAGER_SERVER_STUB));

	    sub.addListener(testListener);

	} catch (Exception e){
    	e.printStackTrace();
	}
	}
	
	public static void switchTempMode(String elvinURL, String mode){
		Message message = new Message(elvinURL);
		message.clear();
		message.setFrom(Message.HOME_MANAGER_CLIENT_STUB);
		message.setTo(Message.SENSOR_NAME);
		message.setType(Message.TYPE_TEMPERATURE);
		message.setQuery(mode);
		
		message.sendNotification();
	}

	public static void main(String [] args){

		SensorTesting me = new SensorTesting(Message.DEFAULT_ELVIN_URL);
		
		switchTempMode(Message.DEFAULT_ELVIN_URL, Message.NON_PERIODIC);

	} 
}

