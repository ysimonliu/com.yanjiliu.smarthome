package components;

import java.io.BufferedReader;
import java.io.FileReader;

import org.avis.client.*;

import pseudoRPC.Message;


public class EMM {

	private static FileReader fr;
	private static BufferedReader br;
	private static Elvin elvin;
	private static String dataFileName, elvinURL;
	private static MusicFileList mfl;
	private static String lineContent, fileName, title, disc, track, from, instruction, temp;
	private static String[] values;
	private static Message message;
	
	private static NotificationListener emmlistener = new NotificationListener(){
		// upon notification received, execute the following actions
		public void notificationReceived(NotificationEvent event){
			
			// read the instruction
			instruction = event.notification.getString(Message.QUERY);
			from = event.notification.getString(Message.FROM);
			
			// and respond with the result
			if(instruction == Message.GET_TITLE) {
				sendNotification(from, instruction, 
						(temp = event.notification.getString(Message.VALUE)), mfl.getTitle(temp));
			}
			else if (instruction == Message.GET_DISC) {
				sendNotification(from, instruction, 
						(temp = event.notification.getString(Message.VALUE)), mfl.getDisc(temp));
			}
			else if (instruction == Message.GET_TRACKS) {
				sendNotification(from, instruction, 
						(temp = event.notification.getString(Message.VALUE)), mfl.getTracksinString(temp));
			}
			else if (instruction == Message.GET_FILES) {
				sendNotification(from, instruction, 
						event.notification.getString(Message.VALUE), mfl.getFilesinString());
			}
		
		}

		// this method sends out response notifications
		private void sendNotification(String to, String instruction, String value, String title) {
			message.clear();
			message.setFrom(Message.EMM_NAME);
			message.setTo(to);
			message.setQuery(instruction);
			message.setValue(value);
			message.setResponse(title);
			message.sendNotification();
		}
	};

	/**
	 * main method, takes two argument, [fileName] and [elvinURL]
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 2) {
			dataFileName = args[0];
			elvinURL = args[1];
		} else {
			System.exit(1);
		}
	
		// initialize message
		message = new Message(elvinURL);
		
		// read file into data structure
		try {
			readFile(dataFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		// subscribe to elvin instructions
		try{
			elvin = new Elvin(elvinURL);
			Subscription sub = elvin.subscribe(Message.TO + " == " + Message.EMM_NAME); 
			sub.addListener(emmlistener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method reads the predefined data file for the EMM. format is assumed correct with no violation
	 * @param fileName
	 * @throws Exception
	 */
	private static void readFile(String dataFileName) throws Exception {
		mfl = new MusicFileList();
		fr = new FileReader(dataFileName);
		br = new BufferedReader(fr);
		while((lineContent = br.readLine()) != null) {
			// if empty line, skip to read the next line
			if (lineContent == ""){
				lineContent = br.readLine();
			}
			
			// first line is fileName
			values = lineContent.split(":");
			fileName = values[1];
			lineContent = br.readLine();
			
			// second line is title
			values = lineContent.split(":");
			title = values[1];
			lineContent = br.readLine();
			
			// third line is disc
			values = lineContent.split(":");
			disc = values[1];
			lineContent = br.readLine();
			
			// fourth line is track
			values = lineContent.split(":");
			track = values[1];
			lineContent = br.readLine();
			
			// add this record to music file list
			mfl.addFile(fileName, title, disc, track);
		}
	}
}