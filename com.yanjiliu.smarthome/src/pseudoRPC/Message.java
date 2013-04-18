package pseudoRPC;

import java.io.IOException;

import org.avis.client.*;

/**
 * This class is mainly responsible for sending out notifications that does not require responses
 * Additionally, it defines the standard template for a message, but some fields are optional in a message
 * @author Yanji Liu
 *
 */
public class Message {

	/* Full dictionary of message template
	 * From - the component which originated the message
	 * To - destination component
	 * Query - command/instruction to execute, but:
	 *				in the case of sensor sending data, this is sensor type
	 * Value - necessary value
	 * Response - response data for a request, usually all other fields remain the same except
	 * 				that the from and to fields are the opposite. But:
	 * 				in the case of location sensor sending data, this is the user name
	 * User - specific to location sensor data messages. this contains the user name
	 */
	
	// key in notifications
	public final static String TO = "TO";
	public final static String FROM = "FROM";
	public final static String QUERY = "QUERY";
	public final static String VALUE = "VALUE";
	public final static String RESPONSE = "RESPONSE";
	// location sensor special field
	public final static String USER = "USER";
	
	// component names
	public final static String EMM_NAME = "emm";
	public static final String SENSOR_NAME = "sensor";
	public static final String SMART_UI_NAME = "smartUI";
	public static final String HOME_MANAGER_CLIENT_STUB = "homeManagerClient";
	public static final String HOME_MANAGER_SERVER_STUB = "homeManagerServer";
	
	// general instructions
	public final static String SHUTDOWN = "shutdown";
	
	// temp sensor specific instructions and data
	public static final String PERIODIC = "periodic";
	public static final String NON_PERIODIC = "nonperiodic";
	public static final int HOME_TEMP = 22;
	public static final int AWAY_MIN_TEMP = 15, AWAY_MAX_TEMP = 28;
	
	// different sensor type
	public final static String TYPE_TEMPERATURE = "temperature";
	public final static String TYPE_LOCATION = "location";
	public final static String TYPE_ENERGY = "energy";
	
	// location sensor status
	public final static String STATUS_HOME = "home";
	public final static String STATUS_AWAY = "away";
	public final static String VALUE_REGISTRATION = "register";
	public final static String VALUE_DEREGISTRATION = "deregister";
	
	// EMM specific instructions
	public static final String GET_TITLE = "getTitle";
	public static final String GET_DISC = "getDisc";
	public static final String GET_TRACKS = "getTracks";
	public static final String GET_FILES = "getFiles";
	
	// UI specific instructions
	public static final String WARN = "warn";
	
	public final static String DEFAULT_ELVIN_URL = "elvin://0.0.0.0:2917";
	private Notification notification;
	private Elvin elvin;
	
	/**
	 * Constructor. initialize notification and connect to Elvin server for each instance of this class
	 */
	public Message() {
		this.notification = new Notification();
		// connect to elvin server
		try {
			elvin = new Elvin(DEFAULT_ELVIN_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructor. does the same thing with the one above except that it takes a customized elvinURL
	 * @param elvinURL
	 */
	public Message(String elvinURL) {
		this.notification = new Notification();
		// connect to elvin server
		try {
			elvin = new Elvin(elvinURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// TODO: other constructors may be accepted too, expand as this being used
	
	public String getTo() {
		return this.notification.getString(TO);
	}
	
	public String getFrom() {
		return this.notification.getString(FROM);
	}
	
	public String getQuery() {
		return this.notification.getString(QUERY);
	}
	
	public String getValue() {
		return this.notification.getString(VALUE);
	}
	
	public String getResponse() {
		return this.notification.getString(RESPONSE);
	}
	
	public String getUser() {
		return this.notification.getString(USER);
	}
	
	public void setTo(String to) {
		this.notification.set(TO, to);
	}
	
	public void setFrom(String from) {
		this.notification.set(FROM, from);
	}
	
	public void setQuery(String query) {
		this.notification.set(QUERY, query);
	}
	
	public void setValue(String value) {
		this.notification.set(VALUE, value);
	}
	
	public void setResponse(String response) {
		this.notification.set(RESPONSE, response);
	}
	
	public void setUser(String user) {
		this.notification.set(USER, user);
	}
	
	/**
	 * Determine whether a message is complete. 
	 * From and To fields are mandatory, also one of the field of Query or Value is required. User must not be filled.
	 * but in the special case of location sensor, the field user is mandatory
	 * @return
	 */
	public boolean isComplete() {
		if(this.getFrom() == SENSOR_NAME && this.getQuery() == TYPE_LOCATION) {
			return this.getTo() != null && this.getUser() != null && this.getValue() != null;
		}
		return this.getFrom() != null && this.getTo() != null && (this.getQuery() != null || this.getValue() != null) && this.getUser() == null;
	}
	
	/**
	 * Get a notification of the message
	 * message is basically a wrapper of a notification
	 * @return
	 */
	public Notification getNotification() {
		if (this.isComplete()){
			return notification;
		}
		return null;
	}
	
	/**
	 * clear up a message
	 */
	public void clear() {
		this.notification.clear();
	}
	
	/**
	 * send the notification onto the elvin server
	 * and upon completion the message will be restored
	 */
	public void sendNotification() {
		if (this.isComplete()){
			try {
				elvin.send(this.notification);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.clear();
		}
	}
}
