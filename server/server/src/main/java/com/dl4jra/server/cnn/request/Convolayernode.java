package com.dl4jra.server.cnn.request;


import org.deeplearning4j.nn.conf.ConvolutionMode;

public class Convolayernode extends Nodeclass{
	/* Convolution layer node data */
	
	private String activationfunction, convolutionMode;
	private int ordering, kernalx, kernaly, stridex, stridey, paddingx, paddingy, nIn, nOut;
	private double dropOut, biasInit;
	
	public Convolayernode() {

	}
	
	public Convolayernode(String nodeId, int ordering, int kernalx, int kernaly, int stridex, int stridey, int paddingx, int paddingy,
						  int nIn, int nOut, String activationfunction, double dropOut, double biasInit, String convolutionMode) {
		super(nodeId);
		this.ordering = ordering;
		this.kernalx = kernalx;
		this.kernaly = kernaly;
		this.stridex = stridex;
		this.stridey = stridey;
//		=================================
		this.paddingx = paddingx;
		this.paddingy = paddingy;
		this.nIn = nIn;
		this.nOut = nOut;
		this.activationfunction = activationfunction;
		this.dropOut = dropOut;
		this.biasInit = biasInit;
		this.convolutionMode = convolutionMode;
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

//=========================================================
	public int getPaddingx() { return paddingx; }

	public void setPaddingx(int paddingx) { this.paddingx = paddingx; }

	public int getPaddingy() { return paddingy; }

	public void setPaddingy(int paddingy) { this.paddingy = paddingy; }
//	========================================================

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

	public String getConvolutionMode() {
		return convolutionMode;
	}

	public void setConvolutionMode(String convolutionMode) {
		this.convolutionMode = convolutionMode;
	}
}
