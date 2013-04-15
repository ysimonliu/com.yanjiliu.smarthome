package elvin;

import org.avis.client.*;

public class ElvinConsumer{

    private Elvin elvin;

    public ElvinConsumer(String url){

    	try{
    		elvin = new Elvin(url);
    		System.out.println("Connected");
    		Subscription sub = elvin.subscribe("TYPE == 'temperature'"); 
    		sub.addListener(new NotificationListener(){
    			public void notificationReceived(NotificationEvent event){
    				//print out the notification
    				System.out.println(event.notification.get("VALUE"));
               }
             });
    		} catch (Exception e){
    			e.printStackTrace();
    		}
    }

    public static void main(String [] args){
    	ElvinConsumer me = new ElvinConsumer("elvin://0.0.0.0:2917");
    } 
}
