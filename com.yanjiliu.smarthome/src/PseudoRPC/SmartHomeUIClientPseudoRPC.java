package PseudoRPC;

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
	
	public String requestFromSmartHome(String query, String value) {
		// send request message on the server
		message.clear();
		message.setFrom(Message.SMART_UI_NAME);
		message.setTo(Message.HOME_MANAGER_SERVER_STUB);
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
		criteria = Message.FROM + " == " + Message.HOME_MANAGER_SERVER_STUB + " && " +
				Message.TO + " == " + Message.SMART_UI_NAME + " && " +
				Message.QUERY + " == " + query + " && " +
				Message.VALUE + " == " + value;
		
		try {
			response = elvin.subscribe(criteria);
			response.addListener(new NotificationListener() {
				public void notificationReceived(NotificationEvent event) {
					result = event.notification.getString(Message.RESPONSE);
					elvin.close();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// return the result
		return result;
	}
}
