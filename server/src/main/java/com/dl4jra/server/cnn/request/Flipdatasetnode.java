package com.dl4jra.server.cnn.request;

public class Flipdatasetnode extends Nodeclass{
	/* Flip dataset node data */
	
	private int flipmode;
	
	public Flipdatasetnode() {
		
	}
	
	public Flipdatasetnode(String nodeId, int flipmode) {
		super(nodeId);
		this.flipmode = flipmode;
	}

	public int getFlipmode() {
		return flipmode;
	}

	public void setFlipmode(int flipmode) {
		this.flipmode = flipmode;
	}
}
