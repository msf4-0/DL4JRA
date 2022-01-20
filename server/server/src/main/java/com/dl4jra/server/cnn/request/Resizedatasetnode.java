package com.dl4jra.server.cnn.request;

public class Resizedatasetnode extends Nodeclass {
	/* Resize dataset node data */
	private int imagewidth, imageheight;
	
	public Resizedatasetnode() {
		
	}
	
	public Resizedatasetnode(String nodeId, int imagewidth, int imageheight) {
		super(nodeId);
		this.imagewidth = imagewidth;
		this.imageheight = imageheight;
	}

	public int getImagewidth() {
		return imagewidth;
	}

	public void setImagewidth(int imagewidth) {
		this.imagewidth = imagewidth;
	}

	public int getImageheight() {
		return imageheight;
	}

	public void setImageheight(int imageheight) {
		this.imageheight = imageheight;
	}
	
}
