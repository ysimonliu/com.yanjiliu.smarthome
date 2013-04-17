package components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;

import org.avis.client.*;

import PseudoRPC.Message;

public class SmartHomeUI {
	
	private static String elvinURL, input;
	private final static String ENERGY_SUB_CRITERIA = Message.FROM + " == " + Message.HOME_MANAGER_CLIENT_STUB + " && " +
			Message.TO + " == " + Message.HOME_MANAGER_CLIENT_STUB + " && " +
			Message.QUERY + " == " + Message.WARN;
	private static Elvin elvin;
	private static Subscription subscription;
	private static BufferedReader stdin;
	
	private static NotificationListener UIListener = new NotificationListener(){
		// upon notification received, execute the following actions
		public void notificationReceived(NotificationEvent event){
			System.out.println("Energy Usage Warning: Current electricity consumption is " + 
					event.notification.getString(Message.VALUE) + ".");
			System.out.println("Please consider the environment before switching on any electrical appliance.");
		}
	};
	
	public static void main(String[] args) {
		// reads the parameter into variable
		if (args.length == 1) {
			elvinURL = args[0];
		}
		else if (args.length == 0) {
			elvinURL = Message.DEFAULT_ELVIN_URL;
		}
		else {
			System.exit(1);
		}
		
		//add listener to energy over consumption
		try {
			elvin = new Elvin(elvinURL);
			subscription = elvin.subscribe(ENERGY_SUB_CRITERIA);
			subscription.addListener(UIListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// load menu
		welcomeMessage();
		mainMenu();
	}
	
	private static void welcomeMessage() {
		try {
			System.out.println("Welcome to the Smart Home Monitoring System");
			System.out.println("Please enter your user name:");
			System.out.flush();
			input = stdin.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void mainMenu() {
		stdin = new BufferedReader (new InputStreamReader(System.in));
		
		try {
			System.out.println("Welcome to the Smart Home Monitoring System");
			System.out.println("Please select an option:");
			System.out.println("1. View Log - temperature adjustment");
			System.out.println("2. View media Files");
			System.out.println("3. View disc tracks");
			System.out.println("E. Exit");		
			System.out.flush();
			
			input = stdin.readLine();
			
			switch(input){
			case "1": System.out.println("viewLog();");
				break;
			case "2": System.out.println("viewMediaFiles();");
				break;
			case "3": System.out.println("viewDiscTracks();");
				break;
			case "E": exit();
				break;
			default: System.out.println("Invalid command");
				break;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// always go back to main menu
		mainMenu();
	}

	private static void exit() {
		// TODO Auto-generated method stub
		
	}

	private static void viewDiscTracks() {
		// TODO Auto-generated method stub
		
	}

	private static void viewMediaFiles() {
		// TODO Auto-generated method stub
		
	}

	private static void viewLog() {
		
	}
	
	
}
