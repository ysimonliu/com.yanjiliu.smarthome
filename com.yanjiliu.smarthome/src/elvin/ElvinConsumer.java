package elvin;

import org.avis.client.*;

public class ElvinConsumer{

    private Elvin elvin;

    public ElvinConsumer(String url){

    	try{
    		elvin = new Elvin(url);
    		Subscription sub = elvin.subscribe("NAME == 'test'"); 
    		sub.addListener(new NotificationListener(){
    			public void notificationReceived(NotificationEvent event){
    				//print out the notification
    				System.out.println(event.notification.get("NAME"));
               }
             });
    		} catch (Exception e){
    			e.printStackTrace();
    		}
    }

    public static void main(String [] args){
    	ElvinConsumer me = new ElvinConsumer(args[0]);
    } 
}
