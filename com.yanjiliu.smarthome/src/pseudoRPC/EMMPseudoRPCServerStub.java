package pseudoRPC;

import org.avis.client.Elvin;
import org.avis.client.NotificationEvent;
import org.avis.client.NotificationListener;
import org.avis.client.Subscription;

import components.EMM;
import components.MusicFileList;

public class EMMPseudoRPCServerStub {
	
	private Message message;
	private String query, from, temp;
	private MusicFileList mfl;
	private Elvin elvin;
	private Subscription sub;
	
	public EMMPseudoRPCServerStub(String elvinURL, MusicFileList mfl) {
		this.mfl = mfl;
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
			else if (query.equals(Message.SHUTDOWN)) {
				EMM.exit();
			}
		
		}
		// this method sends out response notifications
		private void sendNotification(String to, String instruction, String value, String result) {
			message.clear();
			message.setFrom(Message.EMM_NAME);
			message.setTo(to);
			message.setQuery(instruction);
			message.setValue(value);
			message.setResponse(result);
			message.sendNotification();
		}
	};
}
