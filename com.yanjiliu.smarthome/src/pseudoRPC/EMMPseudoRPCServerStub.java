package pseudoRPC;

import java.io.IOException;

import org.avis.client.Elvin;
import org.avis.client.NotificationEvent;
import org.avis.client.NotificationListener;
import org.avis.client.Subscription;

import components.EMM;
import components.MusicFileList;

public class EMMPseudoRPCServerStub {
	
	private Message message;
	private EMM emm;
	private String query, from, temp;
	private MusicFileList mfl;
	private Elvin elvin;
	private Subscription sub;
	
	/**
	 * Constructor
	 * @param elvinURL
	 * @param emm
	 */
	public EMMPseudoRPCServerStub(String elvinURL, EMM emm) {
		// initialize emm, mfl, and message
		this.emm = emm;
		this.mfl = emm.getMFL();
		this.message = new Message(elvinURL);
		// subscribe to elvin instructions
		try{
			this.elvin = new Elvin(elvinURL);
			this.sub = elvin.subscribe(Message.criteriaBuilder(Message.TO, Message.EMM_NAME)); 
			sub.addListener(emmlistener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// define the listener for the EMM PseudoRPCServerStub
	private NotificationListener emmlistener = new NotificationListener(){
		// upon notification received, execute the following actions
		public void notificationReceived(NotificationEvent event){
			
			// read the instruction
			query = event.notification.getString(Message.QUERY);
			from = event.notification.getString(Message.FROM);
			
			// and respond with the result
			if(query.equals(Message.GET_TITLE)) {
				sendNotification(from, query, 
						(temp = event.notification.getString(Message.VALUE)), mfl.getTitle(temp));
			}
			else if (query.equals(Message.GET_DISC)) {
				sendNotification(from, query, 
						(temp = event.notification.getString(Message.VALUE)), mfl.getDisc(temp));
			}
			else if (query.equals(Message.GET_TRACKS)) {
				sendNotification(from, query, 
						(temp = event.notification.getString(Message.VALUE)), mfl.getTracks(temp));
			}
			else if (query.equals(Message.GET_FILES)) {
				sendNotification(from, query, 
						event.notification.getString(Message.VALUE), mfl.getFiles());
			}
			// if told to shutdown, then exit
			else if (query.equals(Message.SHUTDOWN)) {
				exit();
			}
		
		}
		
		/**
		 * this method sends out response notifications with the corresponding inputs
		 * @param to
		 * @param instruction
		 * @param value
		 * @param result
		 */
		private void sendNotification(String to, String instruction, String value, String result) {
			message.clear();
			message.setFrom(Message.EMM_NAME);
			message.setTo(to);
			message.setQuery(instruction);
			message.setValue(value);
			message.setResponse(result);
			message.sendNotification();
		}
		
		/**
		 * This method will gracefully exit this server stub as well as the sensor
		 */
		private void exit() {
			// remove subscription and close elvin connection
			try {
				sub.remove();
			} catch (IOException e) {
				e.printStackTrace();
			}
			elvin.close();
			// close EMM
			emm.exit();
		}
	};
}
