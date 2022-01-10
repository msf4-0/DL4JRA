package com.dl4jra.server.cnn.request;

public class Mlconfigurationnode extends Nodeclass {
	/* MultiLayerConfiguration node data */
	
	private int seed;
	private double learningrate;
	private String optimizationalgorithm, convolutionMode, activationfunction, weightInit, gradientNormalization;
	
	public Mlconfigurationnode() {
		
	}
	
	public Mlconfigurationnode(String nodeId, int seed, double learningrate, String optimizationalgorithm, String convolutionMode,
							   String activationfunction, String weightInit, String gradientNormalization) {
		super(nodeId);
		this.seed = seed;
		this.learningrate = learningrate;
		this.optimizationalgorithm = optimizationalgorithm;
		this.convolutionMode = convolutionMode;
		this.activationfunction = activationfunction;
		this.weightInit = weightInit;
		this.gradientNormalization = gradientNormalization;
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

	public String getConvolutionMode() {
		return convolutionMode;
	}

	public void setConvolutionMode(String convolutionMode) {
		this.convolutionMode = convolutionMode;
	}

	public void setOptimizationalgorithm(String optimizationalgorithm) {
		this.optimizationalgorithm = optimizationalgorithm;
	}

	public String getActivationfunction() {
		return activationfunction;
	}

	public void setActivationfunction(String activationfunction) {
		this.activationfunction = activationfunction;
	}

	public String getWeightInit() {
		return weightInit;
	}

	public void setWeightInit(String weightInit) {
		this.weightInit = weightInit;
	}

	public String getGradientNormalization() {
		return gradientNormalization;
	}

	public void setGradientNormalization(String gradientNormalization) {
		this.gradientNormalization = gradientNormalization;
	}
}
