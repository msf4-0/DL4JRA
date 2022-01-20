package com.dl4jra.server.cnn.flowsaving.classes;

public class MTFlowEdge {
	/* (MULTI-TAB) Edge class */
	
	private int flowindex;
	private Edge edge;
	
	public MTFlowEdge() {
		
	}

	/**
	 * Constructor
	 * @param flowindex - index of flow 
	 * @param edge - edge data
	 */
	public MTFlowEdge(int flowindex, Edge edge) {
		this.flowindex = flowindex;
		this.edge = edge;
	}

	/* flowindex getter function */
	public int getFlowindex() {
		return flowindex;
	}

	/* flowindex setter function */
	public void setFlowindex(int flowindex) {
		this.flowindex = flowindex;
	}
	
	/* edge getter function */
	public Edge getEdge() {
		return edge;
	}

	/* edge setter function */
	public void setEdge(Edge edge) {
		this.edge = edge;
	}
}
