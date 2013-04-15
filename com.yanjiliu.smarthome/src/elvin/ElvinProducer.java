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
    	//Notification not2;

    	try{
    		not = new Notification();
    		//not2 = new Notification();
    		not.set("NAME", "test");
    		not.set("SEX", "male");
    		elvin.send(not); //send the notification
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    }


    public static void main(String [] args){
    	ElvinProducer me = new ElvinProducer("elvin://0.0.0.0:2917");
    	me.produce();
    }
}