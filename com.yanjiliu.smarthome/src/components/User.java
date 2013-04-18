package components;

import pseudoRPC.Message;

public class User {

	private String name;
	private String status;
	
	public User(String name) {
		this.name = name;
		this.status = null;
	}
	
	public void setStatus(String status){
		if (status == Message.STATUS_AWAY || status == Message.STATUS_HOME) {
			this.status = status;
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getStatus() {
		return this.status;
	}

}
