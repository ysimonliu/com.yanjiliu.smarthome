package components;

import PseudoRPC.*;

public class HomeManager {
	
	private static String elvinURL;
	private static HomeManagerPseudoRPCServerStub server;
	private static HomeManagerPseudoRPCClientStub client;
	
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
		client = new HomeManagerPseudoRPCClientStub();
	}
	
	public static void controlTemperature() {
		
	}
}
