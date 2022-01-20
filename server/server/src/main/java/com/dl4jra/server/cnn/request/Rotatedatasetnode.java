package com.dl4jra.server.cnn.request;

public class Rotatedatasetnode extends Nodeclass {
	/* Rotate dataset node data */

	private int angle;
	
	public Rotatedatasetnode() {
		
	}
	
	public Rotatedatasetnode(String nodeId, int angle) {
		super(nodeId);
		this.angle = angle;
	}
	
	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}
}
