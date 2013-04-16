package components;

import PseudoRPC.*;

public class HomeManager {
	
	private static String elvinURL;
	private static Message message;
	private static HomeManagerPseudoRPCServerStub server;
	private static HomeManagerPseudoRPCClientStub controller;
	private static int energy, temperature, location;
	
	/**
	 * main method upon start of this program
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 1) {
			elvinURL = args[0];
		}
		else if (args.length == 0) {
			elvinURL = Message.DEFAULT_ELVIN_URL;
		}
		else {
			System.exit(1);
		}
		
		server = new HomeManagerPseudoRPCServerStub();
		controller = new HomeManagerPseudoRPCClientStub();
	}
	
	public static void monitorEnergy() {
		if (energy > 4000) {
			warnUI(energy);
		}
	}

	public static void adjustTemperature() {
		
	}
	
	/**
	 * This method sends out a notification to the UI to warn about energy overusage
	 * @param energyOverusage
	 */
	private static void warnUI(int energyOverusage) {
		message.clear();
		message.setFrom(Message.HOME_MANAGER_NAME);
		message.setTo(Message.SMART_UI_NAME);
		message.setQuery(Message.WARN);
		message.setValue(String.valueOf(energyOverusage));
		message.sendNotification();
	}
	
	/**
	 * This method sends out a notification to shut off a component
	 * @param to - destination component
	 */
	public void shutdown(String to) {
		message.clear();
		message.setFrom(Message.HOME_MANAGER_NAME);
		message.setTo(to);
		message.setQuery(Message.SHUTDOWN);
		message.sendNotification();
	}
	
	/**
	 * This method sends out a notification to temperature sensor to switch mode
	 * @param mode - periodic? OR non-periodic?
	 */
	public void switchTempMode(String mode){
		message.clear();
		message.setFrom(Message.HOME_MANAGER_NAME);
		message.setTo(Message.SENSOR_NAME);
		message.setQuery(mode);
		message.sendNotification();
	}
}
