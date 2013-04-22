package test;

import pseudoRPC.Message;

public class SmartUIWarningTest {
	
	private Message message;

	/**
	 * This method sends out a notification to the UI to warn about energy overusage
	 * @param energyConsumption
	 */
	public void warnUI(String energyConsumption) {
		message.clear();
		message.setFrom(Message.HOME_MANAGER_CLIENT_STUB);
		message.setTo(Message.SMART_UI_NAME);
		message.setQuery(Message.WARN);
		message.setValue(energyConsumption);
		message.sendNotification();
	}
	
	public void main(String[] args) {
		message = new Message(Message.DEFAULT_ELVIN_URL);
		warnUI("20000");
	}
}
