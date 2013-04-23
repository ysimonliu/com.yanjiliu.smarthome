package pseudoRPC;

import java.io.IOException;

import org.avis.client.*;

public class SmartHomeUIPseudoRPCClientStub {

	private static Elvin elvin;
	private String criteria, result;
	private Message message;
	private Subscription response;
	private static Object lock = new Object();
	
	/**
	 * Constructor
	 * @param elvinURL
	 */
	public SmartHomeUIPseudoRPCClientStub(String elvinURL) {
		message = new Message(elvinURL);
		
		// connect to the elvin server
		try {
			elvin = new Elvin(elvinURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Request from the home manager
	 * @param query
	 * @param value
	 * @return
	 */
	public String requestFromHomeManager(String query, String value) {
		
		// set up listener for the response
		criteria = Message.criteriaBuilder(Message.FROM, Message.HOME_MANAGER_SERVER_STUB) + " && " +
				Message.criteriaBuilder(Message.TO, Message.SMART_UI_NAME) + " && " +
				Message.criteriaBuilder(Message.QUERY, query) + " && " + Message.criteriaBuilder(Message.VALUE, value);
		
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
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// send request message on the server
		message.clear();
		message.setFrom(Message.SMART_UI_NAME);
		message.setTo(Message.HOME_MANAGER_SERVER_STUB);
		message.setQuery(query);
		message.setValue(value);
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
	 * This method sends the shutdown command to home manager
	 */
	public void shutdownHomeManager() {
		message.clear();
		message.setFrom(Message.SMART_UI_NAME);
		message.setTo(Message.HOME_MANAGER_SERVER_STUB);
		message.setQuery(Message.SHUTDOWN);
		message.sendNotification();
	}

	/**
	 * This method will first notify the home manager to exit and then destroy message (close elvin connection)
	 */
	public void exit() {
		shutdownHomeManager();
		message.destroy();
		// close connection to elvin server
		elvin.close();
	}

}
