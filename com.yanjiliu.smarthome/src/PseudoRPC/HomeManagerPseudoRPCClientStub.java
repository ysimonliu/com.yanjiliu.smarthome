package PseudoRPC;

import org.avis.client.*;

public class HomeManagerPseudoRPCClientStub {
	
	private Message message;
	private String elvinURL;
	private Elvin elvin;
	private String criteria;
	private static String result;
	private Subscription response;
	
	public HomeManagerPseudoRPCClientStub(String elvinURL) {
		this.elvinURL = elvinURL;
	}
	
	/**
	 * Request from EMM of something
	 * @param elvinURL
	 * @param query
	 * @param value
	 * @return
	 */
	public String request(String query, String value) {
		// connect and send to the server
		try {
			elvin = new Elvin(elvinURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// send reqeust message on the server
		message.clear();
		message.setFrom(Message.HOME_MANAGER_NAME);
		message.setTo(Message.EMM_NAME);
		message.setQuery(query);
		message.setValue(value);
		message.sendNotification();
		
		// wait for response for the request. during this period, block calling
		criteria = Message.FROM + " == " + Message.EMM_NAME + " && " +
				Message.TO + " == " + Message.HOME_MANAGER_NAME + " && " +
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
