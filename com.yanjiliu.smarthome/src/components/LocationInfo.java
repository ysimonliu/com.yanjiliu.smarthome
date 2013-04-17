package components;

import PseudoRPC.Message;

public class LocationInfo {
	
	// we only support up to 2 occupants of the house
	private User user1;
	private User user2;
	
	public LocationInfo() {
		 user1 = null;
		 user2 = null;
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
		if (user1.getName() == name) {
			user1.setStatus(status);
		} else if (user2.getName() == name) {
			user2.setStatus(status);
		}
	}
	
	/**
	 * Remove an existing user
	 * @param name
	 */
	public void removeUser(String name){
		if (user1.getName() == name) {
			user1 = null;
		} else if (user2.getName() == name) {
			user2 = null;
		}
	}
	
	/**
	 * Get the current location status. if one is home, then return home, else return away
	 * @return
	 */
	public String getStatus() {
		if (user1.getStatus() == Message.STATUS_HOME || user2.getStatus() == Message.STATUS_HOME) {
			return Message.STATUS_HOME;
		}
		return Message.STATUS_AWAY;
	}

}
