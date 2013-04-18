package test;

import org.avis.client.*;

import pseudoRPC.Message;

public class EMMTesting {
	
	private static Message message;
	private static Elvin elvin;
	private static String criteria, result;
	private static Subscription response;
	private static String elvinURL;
	
	public static String requestFromEMM(String query, String value) {
		// send request message on the server
		message.clear();
		message.setFrom(Message.HOME_MANAGER_CLIENT_STUB);
		message.setTo(Message.EMM_NAME);
		message.setQuery(query);
		message.setValue(value);
		/*
		// for testing purpose
		System.out.println("From: " + message.getFrom());
		System.out.println("To: " + message.getTo());
		System.out.println("Query: " + message.getQuery());
		System.out.println("Value: " + message.getValue());
		*/
		message.sendNotification();
		
		// connect to the server
		try {
			elvin = new Elvin(elvinURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("DEBUG: check point 3");
		
		// wait for response for the request. during this period, block calling
		criteria = Message.criteriaBuilder(Message.FROM, Message.EMM_NAME) + " && " +
				Message.criteriaBuilder(Message.TO, Message.HOME_MANAGER_CLIENT_STUB) + " && " +
				Message.criteriaBuilder(Message.QUERY, query) + " && " +
				Message.criteriaBuilder(Message.VALUE, value);
		
		try {
			response = elvin.subscribe(criteria);
			response.addListener(new NotificationListener() {
				public void notificationReceived(NotificationEvent event) {
					System.out.println("DEBUG: check point 4");
					result = event.notification.getString(Message.RESPONSE);
					elvin.close();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// FIXME: Does it wait till it returns the result?
		
		// return the result
		while(result == null) {};
		return result;
	}
	
	public static void main(String[] args) {
		elvinURL = Message.DEFAULT_ELVIN_URL;
		message = new Message(Message.DEFAULT_ELVIN_URL);
		System.out.println(requestFromEMM(Message.GET_TITLE, "track2.mp3"));
		System.out.println(requestFromEMM(Message.GET_DISC, "Let It Be"));
		System.out.println(requestFromEMM(Message.GET_TRACKS, "Let It Be"));
		System.out.println(requestFromEMM(Message.GET_FILES, ""));
	}
}