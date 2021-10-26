package com.dl4jra.server.cnn.request;

public class Outputlayernode extends Nodeclass{
	/* Output layer node data */

	private int ordering, nIn, nOut;
	private String activationfunction, lossfunction;
	
	public Outputlayernode() {
		
	}
	
	public Outputlayernode(String nodeId, int ordering, int nIn, int nOut, String activationfunction, String lossfunction) {
		super(nodeId);
		this.ordering = ordering;
		this.nIn = nIn;
		this.nOut = nOut;
		this.activationfunction = activationfunction;
		this.lossfunction = lossfunction;
	}

	public int getOrdering() {
		return ordering;
	}
	
	public void setOrdering(int ordering) {
		this.ordering = ordering;
	}
	
	public int getnIn() {
		return nIn;
	}
	
	public void setnIn(int nIn) {
		this.nIn = nIn;
	}
	
	public int getnOut() {
		return nOut;
	}
	
	public void setnOut(int nOut) {
		this.nOut = nOut;
	}
	
	public String getActivationfunction() {
		return activationfunction;
	}
	
	public void setActivationfunction(String activationfunction) {
		this.activationfunction = activationfunction;
	}
	
	public String getLossfunction() {
		return lossfunction;
	}
	
	public void setLossfunction(String lossfunction) {
		this.lossfunction = lossfunction;
	}
	
}
