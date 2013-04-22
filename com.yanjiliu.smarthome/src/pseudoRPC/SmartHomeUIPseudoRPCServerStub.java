package pseudoRPC;

import java.io.IOException;

import org.avis.client.Elvin;
import org.avis.client.NotificationEvent;
import org.avis.client.NotificationListener;
import org.avis.client.Subscription;

import components.SmartHomeUI;

public class SmartHomeUIPseudoRPCServerStub {
	
	private final String ENERGY_SUB_CRITERIA = Message.criteriaBuilder(Message.FROM, Message.HOME_MANAGER_CLIENT_STUB) + " && " +
			Message.criteriaBuilder(Message.TO, Message.SMART_UI_NAME) + " && " +
			Message.criteriaBuilder(Message.QUERY, Message.WARN);
	private Elvin elvin;
	private Subscription subscription;
	private SmartHomeUI smartHomeUI;
	
	// this listener listens to the message from Home Manager only about the energy consumption warnings
	private NotificationListener UIListener = new NotificationListener(){
		// upon notification received, simply print out warning on to system standard out
		public void notificationReceived(NotificationEvent event){
			smartHomeUI.energyWarning(event.notification.getString(Message.VALUE));
		}
	};
	
	/**
	 * Constructor
	 * @param elvinURL
	 */
	public SmartHomeUIPseudoRPCServerStub(String elvinURL, SmartHomeUI smartHomeUI) {
		this.smartHomeUI = smartHomeUI;
		//add listener to energy over consumption
		try {
			this.elvin = new Elvin(elvinURL);
			this.subscription = elvin.subscribe(ENERGY_SUB_CRITERIA);
			subscription.addListener(UIListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
