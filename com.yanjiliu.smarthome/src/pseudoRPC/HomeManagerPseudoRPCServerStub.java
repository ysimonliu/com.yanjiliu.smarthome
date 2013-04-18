package pseudoRPC;

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
			case Message.SENSOR_NAME: updateSensorData(query, value, event.notification);
			case Message.SMART_UI_NAME: processUIQuery(type, value);
			}
		}
		
		private void processUIQuery(String query, String value) {
			// TODO Auto-generated method stub
			
		}
		
		/**
		 * update sensor data depending on the notification received
		 * @param type
		 * @param data
		 * @param notification
		 */
		private void updateSensorData(String type, String data, Notification notification) {
			if (type == Message.TYPE_TEMPERATURE) {
				HomeManager.setTemperature(data);
			} else if (type == Message.TYPE_ENERGY){
				HomeManager.setEnergy(data);
			} else if (type == Message.TYPE_LOCATION) {
				if (value == Message.VALUE_REGISTRATION) {
					usersLocation.addUser(notification.getString(Message.USER));
				} else if (value == Message.VALUE_DEREGISTRATION) {
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
		elvin.close();
	}
}