package components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import PseudoRPC.Message;

public class SmartHomeUI {
	
	private static String elvinURL;
	private static Message message;
	private static String input;
	private static BufferedReader stdin;
	
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
		
		message = new Message(elvinURL);
		
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
