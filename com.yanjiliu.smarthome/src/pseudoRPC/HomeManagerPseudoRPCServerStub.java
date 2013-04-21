package pseudoRPC;

import java.io.IOException;

import org.avis.client.*;

import components.HomeManager;
import components.UsersLocation;

public class HomeManagerPseudoRPCServerStub {

	private Elvin elvin;
	private Subscription sub;
	private static String from, to, type, query, value, response;
	private static Message message;
	private static UsersLocation usersLocation;
	
	public HomeManagerPseudoRPCServerStub(String elvinURL, UsersLocation usersLocation){
		message = new Message(elvinURL);
		try {
			elvin = new Elvin(elvinURL);
			sub = elvin.subscribe(Message.criteriaBuilder(Message.TO, Message.HOME_MANAGER_SERVER_STUB));
			sub.addListener(HomeManagerServerStubListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HomeManagerPseudoRPCServerStub.usersLocation = usersLocation;
	}
	
	private NotificationListener HomeManagerServerStubListener = new NotificationListener(){
		// upon notification received, execute the following actions
		public void notificationReceived(NotificationEvent event){
			from = event.notification.getString(Message.FROM);
			query = event.notification.getString(Message.QUERY);
			type = event.notification.getString(Message.TYPE);
			value = event.notification.getString(Message.VALUE);
			switch(from){
			case Message.SENSOR_NAME: updateSensorData(type, value, event.notification);
			case Message.SMART_UI_NAME: processUIQuery(from, query, value);
			}
		}
		
		private void processUIQuery(String to, String query, String value) {
			// initialize response String
			response = "";
			// depends on the query, ask the home manager to process the request
			switch(query) {
			case (Message.VIEW_TEMPERATURE_LOG): response = HomeManager.getTempAdjustLog();
				break;
			case (Message.VIEW_MEDIA_FILES): response = HomeManager.getMediaFiles();
				break;
			case (Message.GET_TRACKS): response = HomeManager.getTracks(value);
				break;
			case (Message.SHUTDOWN): HomeManager.exit();
				break;
			}
			
			// send the result back to the request component, here smartHomeUI is the requesting server for sure
			message.clear();
			message.setFrom(Message.HOME_MANAGER_SERVER_STUB);
			message.setTo(to);
			message.setQuery(query);
			message.setValue(value);
			message.setResponse(response);
			message.sendNotification();
		}
		
		/**
		 * update sensor data depending on the notification received
		 * @param type
		 * @param data
		 * @param notification
		 */
		private void updateSensorData(String type, String data, Notification notification) {
			if (type.equals(Message.TYPE_TEMPERATURE)) {
				HomeManager.setTemperature(data);
			} else if (type.equals(Message.TYPE_ENERGY)){
				HomeManager.setEnergy(data);
			} else if (type.equals(Message.TYPE_LOCATION)) {
				if (value.equals(Message.VALUE_REGISTRATION)) {
					usersLocation.addUser(notification.getString(Message.USER));
				} else if (value.equals(Message.VALUE_DEREGISTRATION)) {
					usersLocation.removeUser(notification.getString(Message.USER));
				} else {
					usersLocation.setStatus(notification.getString(Message.USER), data);
				}
			}
		}
	};
	
	/**
	 * exit the home manager RPC server stub
	 */
	public void exit() {
		// remove subscription and close elvin connection
		try {
			sub.remove();
		} catch (IOException e) {
			e.printStackTrace();
		}
		elvin.close();
	}
}