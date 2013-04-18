package test;

import org.avis.client.*;

import pseudoRPC.Message;

public class EMMTesting {
	
	private static Message message;
	private Elvin elvin;
	private String criteria, result;
	private Subscription response;
	private static String elvinURL;
	
	public void requestFromEMM(String query, String value) {
		// send request message on the server
		message.clear();
		message.setFrom(Message.HOME_MANAGER_CLIENT_STUB);
		message.setTo(Message.EMM_NAME);
		message.setQuery(query);
		message.setValue(value);
		message.sendNotification();
		
		// connect to the server
		try {
			elvin = new Elvin(elvinURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// wait for response for the request. during this period, block calling
		criteria = Message.FROM + " == " + Message.EMM_NAME + " && " +
				Message.TO + " == " + Message.HOME_MANAGER_CLIENT_STUB + " && " +
				Message.QUERY + " == " + query + " && " +
				Message.VALUE + " == " + value;
		
		try {
			response = elvin.subscribe(criteria);
			response.addListener(new NotificationListener() {
				public void notificationReceived(NotificationEvent event) {
					result = event.notification.getString(Message.RESPONSE);
					System.out.println("The response is " + result);
					elvin.close();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		elvinURL = Message.DEFAULT_ELVIN_URL;
		message = new Message(Message.DEFAULT_ELVIN_URL);
	}
}