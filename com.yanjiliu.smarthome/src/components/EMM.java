package components;

import java.io.BufferedReader;
import java.io.FileReader;

import org.avis.client.*;

public class EMM {

	private static FileReader fr;
	private static BufferedReader br;
	private static Elvin elvin;
	private static String dataFileName, elvinURL;
	private static MusicFileList mfl;
	private static String lineContent, fileName, title, disc, track;
	private static String elvinInstruction;
	private static String[] values;
	
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
	
	// read file into data structure
	try {
		readFile(dataFileName);
	} catch (Exception e1) {
		e1.printStackTrace();
	}
	
	// subscribe to elvin instructions
	try{
		elvin = new Elvin(elvinURL);
		Subscription sub = elvin.subscribe(Message.TO + " == " + Message.EMM_NAME); 
		sub.addListener(new NotificationListener(){
			// upon notification received, execute the following actions
			public void notificationReceived(NotificationEvent event){
				
				// initialize the fileName, disc, title, track for the use of following actions
				fileName = disc = title = track = "";
				
				// read the instruction
				elvinInstruction = event.notification.getString(Message.INSTRUCTION);
				
				if(elvinInstruction == "getTitle") {
					sendNotification(elvin, event.notification.getString("FROM"), elvinInstruction, (fileName = event.notification.getString("FILENAME")), mfl.getTitle(fileName));
				}
				else if (elvinInstruction == "getDisc") {
					
				}
				else if (elvinInstruction == "getTracks") {
					
				}
				else if (elvinInstruction == "getFiles") {
					
				}
           }

			// this method sends out response notifications
			private void sendNotification(Elvin elvin, String Destination,
					String elvinInstruction, String string2, String title) {
				Notification notification = new Notification();
				notification.set("DESTINATION", Destination);
				notification.set("FROM", Message.EMM_NAME);
				notification.set("INSTRUCTION", "a");
			}
         });
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * This method reads the predefined data file for the EMM. format is assumed correct with no violation
	 * @param fileName
	 * @throws Exception
	 */
	private static void readFile(String fileName) throws Exception {
		mfl = new MusicFileList();
		fr = new FileReader(fileName);
		br = new BufferedReader(fr);
		while((lineContent = br.readLine()) != null) {
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
			
			// third line is disc
			values = lineContent.split(":");
			track = values[1];
			lineContent = br.readLine();
			
			// add this record to music file list
			mfl.addFile(fileName, title, disc, track);
		}
	}
}
