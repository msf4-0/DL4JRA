package com.dl4jra.server.cnn.request;

public class Inputnode extends Nodeclass {
	/* Set input type node data */

	private int imagewidth, imageheight, channels;
	
	public Inputnode() {
		
	}
	
	public Inputnode(String nodeId, int imagewidth, int imageheight, int channels) {
		super(nodeId);
		this.imagewidth = imagewidth;
		this.imageheight = imageheight;
		this.channels = channels;
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

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}
}
