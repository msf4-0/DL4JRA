package com.dl4jra.server.cnn.response;

public class RBProcessCompleted {
	/* Process complete signal + message */

	private String message;

	public RBProcessCompleted(String message) {
		this.message = message;
	}

	/* message getter function */
	public String getMessage() {
		return message;
	}

	/* message setter function */
	public void setMessage(String message) {
		this.message = message;
	}
}
