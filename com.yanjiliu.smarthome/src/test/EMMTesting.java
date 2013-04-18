package test;

import org.avis.client.*;

import pseudoRPC.Message;

public class EMMTesting {
	
	private static Message message;
	private static Elvin elvin;
	private static String criteria, result;
	private static Subscription response;
	private static String elvinURL;
	
	public static void requestFromEMM(String query, String value) {
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
		System.out.println(criteria);
		
		try {
			response = elvin.subscribe(criteria);
			response.addListener(new NotificationListener() {
				public void notificationReceived(NotificationEvent event) {
					result = event.notification.getString(Message.RESPONSE);
					System.out.println("The From is " + event.notification.getString(Message.FROM));
					System.out.println("The To is " + event.notification.getString(Message.TO));
					System.out.println("The Query is " + event.notification.getString(Message.QUERY));
					System.out.println("The Value is " + event.notification.getString(Message.VALUE));
					System.out.println("The response is " + result);
					System.out.println();
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
		requestFromEMM(Message.GET_DISC, "Let it be");
	}
}