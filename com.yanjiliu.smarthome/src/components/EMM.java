package components;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.avis.client.Elvin;
import org.avis.client.NotificationEvent;
import org.avis.client.NotificationListener;
import org.avis.client.Subscription;

public class EMM {

	private static FileReader fr;
	private static BufferedReader br;
	private static Elvin elvin;
	private static String dataFileName, elvinURL;
	private static MusicFileList mfl;
	private static String lineContent, fileName, title, disc, track;
	private static String[] values;
	
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
		Subscription sub = elvin.subscribe("TYPE == 'EMM'"); 
		sub.addListener(new NotificationListener(){
			public void notificationReceived(NotificationEvent event){
				// if told to shutdown, do it
				if(event.notification.get("VALUE") == "shutdown") {
					//TODO: exit
				}
           }
         });
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
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
