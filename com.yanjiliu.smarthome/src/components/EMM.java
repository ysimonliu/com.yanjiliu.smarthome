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
	private static String fileName, elvinURL;
	
	public static void main(String[] args) {
		
	if (args.length == 2) {
		fileName = args[0];
		elvinURL = args[1];
	} else {
		System.exit(1);
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
	
	private void readFile(String fileName) throws FileNotFoundException {
		fr = new FileReader(fileName);
		br = new BufferedReader(fr);
	}
}
