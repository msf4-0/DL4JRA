package com.dl4jra.server.cnn.request;


public class Convolayernode extends Nodeclass{
	/* Convolution layer node data */
	
	private String activationfunction;
	private int ordering, kernalx, kernaly, stridex, stridey, nIn, nOut;
	
	public Convolayernode() {
		
	}
	
	public Convolayernode(String nodeId, int ordering, int kernalx, int kernaly, int stridex, int stridey, int nIn, int nOut, String activationfunction) {
		super(nodeId);
		this.ordering = ordering;
		this.kernalx = kernalx;
		this.kernaly = kernaly;
		this.stridex = stridex;
		this.stridey = stridey;
		this.nIn = nIn;
		this.nOut = nOut;
		this.activationfunction = activationfunction;
	}

	public int getOrdering() {
		return ordering;
	}
	
	public void setOrdering(int ordering) {
		this.ordering = ordering;
	}
	
	public int getKernalx() {
		return kernalx;
	}
	
	public void setKernalx(int kernalx) {
		this.kernalx = kernalx;
	}
	
	public int getKernaly() {
		return kernaly;
	}
	
	public void setKernaly(int kernaly) {
		this.kernaly = kernaly;
	}
	
	public int getStridex() {
		return stridex;
	}
	
	public void setStridex(int stridex) {
		this.stridex = stridex;
	}
	
	public int getStridey() {
		return stridey;
	}
	
	public void setStridey(int stridey) {
		this.stridey = stridey;
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
	
	
}
