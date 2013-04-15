package elvin;

import org.avis.client.*;

public class ElvinProducer{

    private Elvin elvin;

    public ElvinProducer(String server){
    	try{
			elvin = new Elvin(server);
			System.out.println("connected");
		} catch (Exception e){
			e.printStackTrace();
		}
    }


    public void produce(){

    	Notification not;

    	try{
    		not = new Notification();
    		not.set("NAME", "test");     
    		elvin.send(not); //send the notification
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    }


    public static void main(String [] args){
    	ElvinProducer me = new ElvinProducer(args[0]);
    	me.produce();
    }
}