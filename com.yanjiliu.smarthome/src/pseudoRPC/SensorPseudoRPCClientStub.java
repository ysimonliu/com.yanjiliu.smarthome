package pseudoRPC;

import components.Sensor;

public class SensorPseudoRPCClientStub extends Thread {
	
	private Message message;
	private Sensor sensor;
	private String type;
	// only used in temperature non periodic mode
	private int previousValue, currentValue;
	
	public SensorPseudoRPCClientStub(String elvinURL, Sensor sensor) {
		message = new Message(elvinURL);
		this.sensor = sensor;
		this.type = sensor.getSensorType();
	}
	
	/**
	 * This method determines what values to put on Elvin when it's a temperature sensor in Non Periodic mode
	 * @param type
	 * @param value
	 */
	public void sendNonPeriodicTempNot(String value) {
		currentValue = Integer.parseInt(value);
		if ((currentValue > Message.AWAY_MAX_TEMP || currentValue < Message.AWAY_MIN_TEMP) && (currentValue != previousValue)) {
			sendNotification(value);
		}
		previousValue = currentValue;
	}

	/**
	 * This method sends the value notification to home manager
	 * @param value
	 */
	public void sendNotification(String value) {
		message.clear();
		message.setFrom(Message.SENSOR_NAME);
		message.setTo(Message.HOME_MANAGER_SERVER_STUB);
		message.setType(this.type);
		message.setValue(value);
		
		// if it's location sensor, then set userName too
		if (type.equals(Message.TYPE_LOCATION)) {
			message.setUser(sensor.getUserName());
			// FIXME: this is for testing
			//System.out.println("USER: " + message.getUser());
		}
		message.sendNotification();
	}

	/**
	 * exit the controller by destroying the message
	 */
	public void exit() {
		message.destroy();
	}
}
