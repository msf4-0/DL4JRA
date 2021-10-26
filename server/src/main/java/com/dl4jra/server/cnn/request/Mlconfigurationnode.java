package com.dl4jra.server.cnn.request;

public class Mlconfigurationnode extends Nodeclass {
	/* MultiLayerConfiguration node data */
	
	private int seed;
	private double learningrate;
	private String optimizationalgorithm;
	
	public Mlconfigurationnode() {
		
	}
	
	public Mlconfigurationnode(String nodeId, int seed, double learningrate, String optimizationalgorithm) {
		super(nodeId);
		this.seed = seed;
		this.learningrate = learningrate;
		this.optimizationalgorithm = optimizationalgorithm;
	}
	
	public int getSeed() {
		return seed;
	}
	
	public void setSeed(int seed) {
		this.seed = seed;
	}
	
	public double getLearningrate() {
		return learningrate;
	}
	
	public void setLearningrate(double learningrate) {
		this.learningrate = learningrate;
	}
	
	public String getOptimizationalgorithm() {
		return optimizationalgorithm;
	}
	
	public void setOptimizationalgorithm(String optimizationalgorithm) {
		this.optimizationalgorithm = optimizationalgorithm;
	}
	
}
