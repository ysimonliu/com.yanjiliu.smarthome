package components;

import java.util.ArrayList;

import pseudoRPC.Message;


public class UsersLocation {
	
	// we only support up to 2 occupants of the house
	private User user1;
	private User user2;
	private String status, previousStatus;
	
	public UsersLocation() {
		 user1 = null;
		 user2 = null;
		 status = previousStatus = "";
	}
	
	/**
	 * Add a user
	 * @param name
	 */
	public void addUser(String name) {
		if (user1 == null) {
			user1 = new User(name);
		} else if (user2 == null) {
			user2 = new User(name);
		}
	}
	
	/**
	 * Changes one user's status identified by the user's name
	 * @param name
	 * @param status
	 */
	public void setStatus(String name, String status){
		if (user1.getName().equals(name)) {
			user1.setStatus(status);
		} else if (user2.getName().equals(name)) {
			user2.setStatus(status);
		}
	}
	
	/**
	 * Remove an existing user
	 * @param name
	 */
	public void removeUser(String name){
		if (user1.getName().equals(name)) {
			user1 = null;
		} else if (user2.getName().equals(name)) {
			user2 = null;
		}
	}
	
	/**
	 * Get the current location status. if one is home, then return home, else return away
	 * @return
	 */
	public String getStatus() {
		if (user1.getStatus().equals(Message.STATUS_HOME) || user2.getStatus().equals(Message.STATUS_HOME)) {
			status =  Message.STATUS_HOME;
		} else {
			status = Message.STATUS_AWAY;
		}
		previousStatus = status;
		return status;
	}
	
	/**
	 * Get previous status at last update
	 * @return
	 */
	public String getPreviousStatus(){
		return this.previousStatus;
	}

	public String[] getWhosHome() {
		ArrayList<String> whosHome = new ArrayList<String>();
		if (user1.getStatus().equals(Message.STATUS_HOME)) {
			whosHome.add(user1.getName());
		}
		if (user2.getStatus().equals(Message.STATUS_HOME)) {
			whosHome.add(user2.getName());
		}
		
		// cast array list to string[]
		String[] result = new String[whosHome.size()];
		for (int i = 0; i < whosHome.size(); i++){
			result[i] = whosHome.get(i);
		}
		return result;
	}

}
