package com.dl4jra.server.cnn.request;

import org.deeplearning4j.nn.weights.WeightInit;

public class Denselayernode extends Nodeclass{
	/* Dense layer node data */ 

	private int ordering, nIn, nOut;
	private String activationfunction, weightInit;
	private double dropOut, biasInit;
	
	public Denselayernode() {
		
	}

	public Denselayernode(String nodeId, int ordering, int nIn, int nOut, String activationfunction, double dropOut, double biasInit,
						  String weightInit) {
		super(nodeId);
		this.ordering = ordering;
		this.nIn = nIn;
		this.nOut = nOut;
		this.activationfunction = activationfunction;
		this.dropOut = dropOut;
		this.biasInit = biasInit;
		this.weightInit = weightInit;
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


	public double getDropOut() {
		return dropOut;
	}

	public void setDropOut(double dropOut) {
		this.dropOut = dropOut;
	}

	public double getBiasInit() {
		return biasInit;
	}

	public void setBiasInit(double biasInit) {
		this.biasInit = biasInit;
	}

	public String getWeightInit() {
		return weightInit;
	}

	public void setWeightInit(String weightInit) {
		this.weightInit = weightInit;
	}
}
