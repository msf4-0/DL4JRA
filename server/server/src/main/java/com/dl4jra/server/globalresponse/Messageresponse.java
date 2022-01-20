package com.dl4jra.server.globalresponse;

public class Messageresponse {
	/* Simple response (message only) */
	private String message;
	
	public Messageresponse() {
		
	}
	
	public Messageresponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
