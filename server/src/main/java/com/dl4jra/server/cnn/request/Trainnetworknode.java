package com.dl4jra.server.cnn.request;

public class Trainnetworknode extends Nodeclass {
	/* Train network node data */
	
	private int epochs, scoreListener;
	
	public Trainnetworknode() {
		
	}
	
	public Trainnetworknode(String nodeId, int epochs, int scoreListener) {
		super(nodeId);
		this.epochs = epochs;
		this.scoreListener = scoreListener;
	}

	public Trainnetworknode(String nodeId, int epochs) {
		super(nodeId);
		this.epochs = epochs;
	}
	
	public int getEpochs() {
		return epochs;
	}

	public void setEpochs(int epochs) {
		this.epochs = epochs;
	}

	public int getScoreListener() {
		return scoreListener;
	}

	public void setScoreListener(int scoreListener) {
		this.scoreListener = scoreListener;
	}
	
	
}
