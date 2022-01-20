package com.dl4jra.server.cnn.response;

public class ErrorResponse {
	/* Response when encounter error during running sequence */

	private String nodeId, message;

	public ErrorResponse() {
		
	}
	
	/**
	 * Constructor
	 * @param nodeId - Error node's id
	 * @param message - Reason of error
	 */
	public ErrorResponse(String nodeId, String message) {
		this.nodeId = nodeId;
		this.message = message;
	}

	/* nodeid getter function */
	public String getNodeId() {
		return nodeId;
	}

	/* nodeid setter function */
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
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
