package components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.avis.client.*;

import pseudoRPC.Message;
import pseudoRPC.SmartHomeUIClientPseudoRPC;

public class SmartHomeUI {
	
	private static String elvinURL, input;
	private final static String ENERGY_SUB_CRITERIA = Message.FROM + " == " + Message.HOME_MANAGER_CLIENT_STUB + " && " +
			Message.TO + " == " + Message.HOME_MANAGER_CLIENT_STUB + " && " +
			Message.QUERY + " == " + Message.WARN;
	private static Elvin elvin;
	private static Subscription subscription;
	private static BufferedReader stdin;
	private static SmartHomeUIClientPseudoRPC UIclient;
	
	// this listener listens to the message from Home Manager only about the energy consumption warnings
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
		
		// instantiate the clientr stub and stdin
		UIclient = new SmartHomeUIClientPseudoRPC(elvinURL);
		stdin = new BufferedReader(new InputStreamReader(System.in)); 
		
		// load menu
		welcomeMessage();
		mainMenu();
	}
	
	/**
	 * This method displays the one time welcome message
	 */
	private static void welcomeMessage() {
		System.out.println("Welcome to the Smart Home Monitoring System");
		System.out.println("Please enter your user name:");
		try {
			input = stdin.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This message loads the main menu
	 */
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
			
			input = stdin.readLine().trim();
			
			switch(input){
			case "1": System.out.println("viewLog();");
				break;
			case "2": viewMediaFiles();
				break;
			case "3": viewDiscTracks();
				break;
			case "E": exit();
				break;
			// I'll be case insensitive here and take "e" as an legal input
			case "e": exit();
				break;
			default: System.out.println("Invalid command");
				System.out.println(1 );
				break;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// always go back to main menu
		mainMenu();
	}

	/**
	 * This method will notify the home manager to shut down, remove subscription, close elvin connection
	 * tells the client stub to exit, and then exit the whole program itself
	 */
	private static void exit() {
		UIclient.shutdownHomeManager();
		try {
			subscription.remove();
		} catch (IOException e) {
			e.printStackTrace();
		}
		elvin.close();
		UIclient.exit();
		System.exit(0);
	}

	/**
	 * This method will execute the UI part of the view disc tracks command
	 */
	private static void viewDiscTracks() {
		// ask and takes the input of the disc title
		System.out.println("Please enter the disc title: ");
		try {
			input = stdin.readLine().trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
		
		// get the result and display the result, the case of no tracks matched are already taken care of at lower levels of code
		System.out.println(UIclient.requestFromHomeManager(Message.GET_TRACKS, input));
		
		// go to enter to return at the end
		pressEnterToReturn();
	}

	/**
	 * This method executes the UI part of the view media files command
	 */
	private static void viewMediaFiles() {
		System.out.println();
		// print out the result. the case of no files found are already taken care of at lower levels of code
		System.out.println(UIclient.requestFromHomeManager(Message.VIEW_MEDIA_FILES, Message.VIEW_MEDIA_FILES));
		
		// go to enter to return at the end
		pressEnterToReturn();
	}

	/**
	 * This method executes the UI part of the view temperature log command
	 */
	private static void viewLog() {
		System.out.println();
		
		// display temperature log. the case of empty logs are already taken care of at lower levels of code
		System.out.println(UIclient.requestFromHomeManager(Message.VIEW_TEMPERATURE_LOG, Message.VIEW_TEMPERATURE_LOG));
		
		// go to enter to return at the end
		pressEnterToReturn();
	}
	
	/**
	 * This method is to perform the "ENTER" to return after each function is executed
	 */
	private static void pressEnterToReturn() {
		// takes anything and return to mainMenu
		System.out.println();
		try {
			input = stdin.readLine().trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// once something is returned, go back to main menu
		mainMenu();
	}
	
}
