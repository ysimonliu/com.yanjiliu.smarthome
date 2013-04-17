package PseudoRPC;

import java.io.IOException;

import org.avis.client.*;

import components.HomeManager;

public class HomeManagerPseudoRPCServerStub {

	private Elvin elvin;
	private Subscription sub;
	private static String from, to, query, value, response;
	private static Message message;
	
	public HomeManagerPseudoRPCServerStub(String elvinURL){
		message = new Message(elvinURL);
		try {
			elvin = new Elvin(elvinURL);
			sub = elvin.subscribe(Message.TO + " == " + Message.HOME_MANAGER_SERVER_STUB);
			sub.addListener(HomeManagerServerStubListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private NotificationListener HomeManagerServerStubListener = new NotificationListener(){
		// upon notification received, execute the following actions
		public void notificationReceived(NotificationEvent event){
			from = event.notification.getString(Message.FROM);
			query = event.notification.getString(Message.QUERY);
			value = event.notification.getString(Message.VALUE);
			switch(from){
			case Message.SENSOR_NAME: updateSensorData(query, value);
			case Message.SMART_UI_NAME: processUIQuery(query, value);
			}
		}
		
		private void processUIQuery(String query, String value) {
			// TODO Auto-generated method stub
			
		}

		private void updateSensorData(String type, String data) {
			if (type == Message.TYPE_TEMPERATURE) {
				HomeManager.setTemperature(data);
			} else if (type == Message.TYPE_ENERGY){
				HomeManager.setEnergy(data);
			} else if (type == Message.TYPE_LOCATION) {
				//TODO: should set location here
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