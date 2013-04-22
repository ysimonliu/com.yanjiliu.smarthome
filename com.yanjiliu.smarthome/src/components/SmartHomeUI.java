package components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.avis.client.*;

import pseudoRPC.Message;
import pseudoRPC.SmartHomeUIPseudoRPCClientStub;
import pseudoRPC.SmartHomeUIPseudoRPCServerStub;

public class SmartHomeUI {
	
	private static String elvinURLInput; 
	private String input;
	private BufferedReader stdin;
	private SmartHomeUIPseudoRPCServerStub server;
	private SmartHomeUIPseudoRPCClientStub controller;
	
	public SmartHomeUI(String elvinURL) {
		// instantiate and start the client stub and server stub
		this.controller = new SmartHomeUIPseudoRPCClientStub(elvinURL);
		this.server = new SmartHomeUIPseudoRPCServerStub(elvinURL, this);
		// initialize standard input
		stdin = new BufferedReader(new InputStreamReader(System.in)); 
		// load menu
		welcomeMessage();
		mainMenu();
	}

	/**
	 * main method
	 * @param args
	 */
	public static void main(String[] args) {
		// reads the parameter into variable
		if (args.length == 1) {
			elvinURLInput = args[0];
		}
		else if (args.length == 0) {
			elvinURLInput = Message.DEFAULT_ELVIN_URL;
		}
		else {
			System.exit(1);
		}
	
		SmartHomeUI smartHomeUI = new SmartHomeUI(elvinURLInput);
	}
	
	/**
	 * This method displays the one time welcome message
	 */
	private void welcomeMessage() {
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
	private void mainMenu() {
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
	 * This method will execute the UI part of the view disc tracks command
	 */
	private void viewDiscTracks() {
		// ask and takes the input of the disc title
		System.out.println("Please enter the disc title: ");
		try {
			input = stdin.readLine().trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
		
		// get the result and display the result, the case of no tracks matched are already taken care of at lower levels of code
		System.out.println(controller.requestFromHomeManager(Message.GET_TRACKS, input));
		
		// go to enter to return at the end
		pressEnterToReturn();
	}

	/**
	 * This method executes the UI part of the view media files command
	 */
	private void viewMediaFiles() {
		System.out.println();
		// print out the result. the case of no files found are already taken care of at lower levels of code
		System.out.println(controller.requestFromHomeManager(Message.VIEW_MEDIA_FILES, Message.VIEW_MEDIA_FILES));
		
		// go to enter to return at the end
		pressEnterToReturn();
	}

	/**
	 * This method executes the UI part of the view temperature log command
	 */
	private void viewLog() {
		System.out.println();
		
		// display temperature log. the case of empty logs are already taken care of at lower levels of code
		System.out.println(controller.requestFromHomeManager(Message.VIEW_TEMPERATURE_LOG, Message.VIEW_TEMPERATURE_LOG));
		
		// go to enter to return at the end
		pressEnterToReturn();
	}
	
	/**
	 * This method is to perform the "ENTER" to return after each function is executed
	 */
	private void pressEnterToReturn() {
		try {
			input = stdin.readLine().trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// once something is returned, go back to main menu
		mainMenu();
	}
	
	/**
	 * This method will notify the home manager to shut down, remove subscription, close elvin connection
	 * tells the client stub to exit, and then exit the whole program itself
	 */
	private void exit() {
		controller.shutdownHomeManager();
		controller.exit();
		server.exit();
		System.exit(0);
	}
	
	public void energyWarning(String energyConsumption) {
		System.out.println("Energy Usage Warning: Current electricity consumption is " + energyConsumption + ".");
		System.out.println("Please consider the environment before switching on any electrical appliance.");
	}
	
}