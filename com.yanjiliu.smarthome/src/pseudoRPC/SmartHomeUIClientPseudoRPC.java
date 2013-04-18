package pseudoRPC;

import java.io.IOException;

import org.avis.client.*;

public class SmartHomeUIClientPseudoRPC {

	private Elvin elvin;
	private String elvinURL, criteria, result;
	private Message message;
	private Subscription response;
	
	public SmartHomeUIClientPseudoRPC(String elvinURL) {
		this.elvinURL = elvinURL;
		message = new Message(elvinURL);
	}
	
	public String requestFromHomeManager(String query, String value) {
		// send request message on the server
		message.clear();
		message.setFrom(Message.SMART_UI_NAME);
		message.setTo(Message.HOME_MANAGER_SERVER_STUB);
		message.setQuery(query);
		// when requiring temp logs and list of media files, value is not needed
		if (!value.isEmpty()){
			message.setValue(value);
		}
		message.sendNotification();
		
		// connect to the server
		try {
			elvin = new Elvin(elvinURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// wait for response for the request. the key fields are exactly the same with what we just sent
		// out except that the FROM and TO field are the opposite
		criteria = Message.criteriaBuilder(Message.FROM, Message.HOME_MANAGER_SERVER_STUB) + " && " +
				Message.criteriaBuilder(Message.TO, Message.SMART_UI_NAME) + " && " +
				Message.criteriaBuilder(Message.QUERY, query);

		// when requiring temp logs and list of media files, value is not needed
		if (!value.isEmpty()) {
			criteria += " && " + Message.criteriaBuilder(Message.VALUE, value);
		}
		
		try {
			response = elvin.subscribe(criteria);
			response.addListener(new NotificationListener() {
				public void notificationReceived(NotificationEvent event) {
					result = event.notification.getString(Message.RESPONSE);
					// remove this subscription and close the elvin connection
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
		
		// block until the call returns
		while(result == null);
		
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
	}

}
