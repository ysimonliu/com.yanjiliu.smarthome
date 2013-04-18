package test;

import org.avis.client.*;

import pseudoRPC.Message;

import components.HomeManager;

public class SensorTesting{

    private Elvin elvin;
    private String from, query, value, user;

    private NotificationListener testListener = new NotificationListener(){
		// upon notification received, execute the following actions
		public void notificationReceived(NotificationEvent event){
			from = event.notification.getString(Message.FROM);
			query = event.notification.getString(Message.QUERY);
			value = event.notification.getString(Message.VALUE);
			
			System.out.println("From: " + from);
			System.out.println("Query: " + query);
			System.out.println("Value: " + value);
			if (query == Message.TYPE_LOCATION) {
				user = event.notification.getString(Message.USER);
				System.out.println("User: " + user);
			}
			System.out.println();
		}
		
	};
	
	public SensorTesting(String url){

	try{

	    elvin = new Elvin(url);
	    Subscription sub = elvin.subscribe(Message.TO + " == " + Message.HOME_MANAGER_SERVER_STUB);

	    sub.addListener(testListener);


	} catch (Exception e){
    	e.printStackTrace();
	}
	}

	public static void main(String [] args){

		SensorTesting me = new SensorTesting(args[0]);

	} 
}

