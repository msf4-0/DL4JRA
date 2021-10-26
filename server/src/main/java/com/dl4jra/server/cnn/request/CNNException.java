package com.dl4jra.server.cnn.request;

@SuppressWarnings("serial")
public class CNNException extends Exception{
	/* Exception handling class (for error node) */
	private String nodeId;

	public CNNException(String message, String nodeId) {
		super(message);
		this.nodeId = nodeId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
}
