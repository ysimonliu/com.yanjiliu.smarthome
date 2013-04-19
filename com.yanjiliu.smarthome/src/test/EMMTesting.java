package test;

import java.io.IOException;

import org.avis.client.*;

import pseudoRPC.Message;

public class EMMTesting {
	
	private static Message message;
	private static Elvin elvin;
	private static String criteria, result;
	private static Subscription response;
	private static String elvinURL;
	private static boolean RESPONSE_RECEIVED;
	private static Object lock = new Object();
	
	public static String requestFromEMM(String query, String value) {
		
		// initialize the result variable
		RESPONSE_RECEIVED = false;
		
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
		if (value != null) {
			criteria = criteria + " && " +Message.criteriaBuilder(Message.VALUE, value);
		}
				
		//FIXME:System.out.println("DEBUG: Checkpoint 1");
				
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
		
		//FIXME:System.out.println("DEBUG: Checkpoint 2");
		// block calls until result is returned
		synchronized(lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//FIXME:System.out.println("DEBUG: Checkpoint 3");
		// return the result
		return result;
	}
	
	public static void main(String[] args) {
		elvinURL = Message.DEFAULT_ELVIN_URL;
		message = new Message(Message.DEFAULT_ELVIN_URL);
		System.out.println("A: " + requestFromEMM(Message.GET_TITLE, "track3.mp3"));
		System.out.println("B: " + requestFromEMM(Message.GET_DISC, "track.mp3"));
		System.out.println("C: " + requestFromEMM(Message.GET_TRACKS, "Let It Be"));
		System.out.println("D: " + requestFromEMM(Message.GET_FILES, ""));
	}
}