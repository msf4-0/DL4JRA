package com.dl4jra.server.cnn.flowsaving.classes;

public class MTFlowNode {
	/* (MULTI-TAB) Node class */
	
	private int flowindex;
	private Node node;
	
	public MTFlowNode() {
		
	}
	
	/**
	 * Constructor
	 * @param flowindex - index of flow 
	 * @param node - node data
	 */
	public MTFlowNode(int flowindex, Node node) {
		this.flowindex = flowindex;
		this.node = node;
	}

	/* flowindex getter function */
	public int getFlowindex() {
		return flowindex;
	}

	/* flowindex setter function */
	public void setFlowindex(int flowindex) {
		this.flowindex = flowindex;
	}

	/* node getter function */
	public Node getNode() {
		return node;
	}

	/* node setter function */
	public void setNode(Node node) {
		this.node = node;
	}
}
