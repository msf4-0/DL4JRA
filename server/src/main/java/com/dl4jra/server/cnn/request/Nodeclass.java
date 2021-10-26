package com.dl4jra.server.cnn.request;

public class Nodeclass {
	/* Node class (superclass) */

	private String nodeId;
	
	public Nodeclass() {
		
	}
	
	public Nodeclass(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
}
