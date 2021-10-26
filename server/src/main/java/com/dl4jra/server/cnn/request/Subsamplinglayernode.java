package com.dl4jra.server.cnn.request;

public class Subsamplinglayernode extends Nodeclass {
	/* Subsampling layer node data */

	private int ordering, kernalx, kernaly, stridex, stridey;
	private String poolingtype;
	
	public Subsamplinglayernode() {
		
	}
	
	public Subsamplinglayernode(String nodeId, int ordering, int kernalx, int kernaly, int stridex, int stridey, String poolingtype) {
		super(nodeId);
		this.ordering = ordering;
		this.kernalx = kernalx;
		this.kernaly = kernaly;
		this.stridex = stridex;
		this.stridey = stridey;
		this.poolingtype = poolingtype;
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
	
	public String getPoolingtype() {
		return poolingtype;
	}
	
	public void setPoolingtype(String poolingtype) {
		this.poolingtype = poolingtype;
	}
	
}
