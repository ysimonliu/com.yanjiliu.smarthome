package test;

import java.io.IOException;

import org.avis.client.Elvin;
import org.avis.client.NotificationEvent;
import org.avis.client.NotificationListener;
import org.avis.client.Subscription;

import pseudoRPC.Message;

import components.SmartHomeUI;

public class SmartUIWarningTest {
	
	private Elvin elvin;
	private Subscription subscription;
	
	// this listener listens to the message from Home Manager only about the energy consumption warnings
	private NotificationListener UIListener = new NotificationListener(){
		// upon notification received, simply print out warning on to system standard out
		public void notificationReceived(NotificationEvent event){
			//smartHomeUI.energyWarning(event.notification.getString(Message.VALUE));
			System.out.println(event.notification.getString(Message.VALUE));
		}
	};
	
	/**
	 * Constructor
	 * @param elvinURL
	 */
	public SmartUIWarningTest(String elvinURL) {
		//add listener to energy over consumption
		try {
			this.elvin = new Elvin(elvinURL);
			String criteria = Message.FROM + " == " + Message.HOME_MANAGER_CLIENT_STUB + " && " +
					Message.TO + " == " + Message.SMART_UI_NAME + " && " +
					Message.QUERY + " == " + Message.WARN;
			System.out.println(criteria);
			this.subscription = elvin.subscribe(criteria);
			subscription.addListener(UIListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SmartUIWarningTest test = new SmartUIWarningTest(Message.DEFAULT_ELVIN_URL);
	}

	/**
	 * This will gracefully exit this server by removing subscription and closing elvin server connection
	 */
	public void exit() {
		try {
			subscription.remove();
		} catch (IOException e) {
			e.printStackTrace();
		}
		elvin.close();
	}
}
