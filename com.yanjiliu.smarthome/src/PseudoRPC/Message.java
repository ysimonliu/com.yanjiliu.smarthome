package PseudoRPC;

import java.io.IOException;

import org.avis.client.*;

public class Message {

	// key in notifications
	public final static String TO = "TO";
	public final static String FROM = "FROM";
	public final static String QUERY = "QUERY";
	public final static String USER = "USER";
	public final static String VALUE = "VALUE";
	public final static String RESPONSE = "RESPONSE";
	
	// component names
	public final static String EMM_NAME = "emm";
	public static final String SENSOR_NAME = "sensor";
	public static final String SMART_UI_NAME = "smartUI";
	public static final String HOME_MANAGER_NAME = "homeManager";
	
	// general instructions
	public final static String SHUTDOWN = "shutdown";
	
	// sensor specific instructions
	public static final String PERIODIC = "periodic";
	public static final String NON_PERIODIC = "nonperiodic";
	
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
	
	public Message() {
		this.notification = new Notification();
		// connect to elvin server
		try {
			elvin = new Elvin(DEFAULT_ELVIN_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Message(String elvinURL) {
		this.notification = new Notification();
		// connect to elvin server
		try {
			elvin = new Elvin(elvinURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// TODO: other constructors accepted
	
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
	
	public boolean isComplete() {
		return this.getFrom() != null && this.getTo() != null && (this.getQuery() != null || this.getValue() != null);
	}
	
	public Notification getNotification() {
		if (this.isComplete()){
			return notification;
		}
		return null;
	}
	
	public void clear() {
		this.notification.clear();
	}
	
	public void sendNotification() {
		if (this.isComplete()){
			try {
				elvin.send(this.notification);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.clear();
		}
	}
}
