package com.dl4jra.server.cnn.request;

import org.deeplearning4j.nn.conf.ConvolutionMode;

public class Subsamplinglayernode extends Nodeclass {
	/* Subsampling layer node data */

	private int ordering, kernalx, kernaly, stridex, stridey, paddingx, paddingy;
	private String poolingtype, convolutionMode;
	
	public Subsamplinglayernode() {
		
	}
	
	public Subsamplinglayernode(String nodeId, int ordering, int kernalx, int kernaly, int stridex, int stridey,
								int paddingx, int paddingy, String poolingtype, String convolutionMode) {
		super(nodeId);
		this.ordering = ordering;
		this.kernalx = kernalx;
		this.kernaly = kernaly;
		this.stridex = stridex;
		this.stridey = stridey;
		this.paddingx = paddingx;
		this.paddingy = paddingy;
		this.poolingtype = poolingtype;
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

	public String getPoolingtype() {
		return poolingtype;
	}
	
	public void setPoolingtype(String poolingtype) {
		this.poolingtype = poolingtype;
	}

	public String getConvolutionMode() {
		return convolutionMode;
	}

	public void setConvolutionMode(String convolutionMode) {
		this.convolutionMode = convolutionMode;
	}
}
