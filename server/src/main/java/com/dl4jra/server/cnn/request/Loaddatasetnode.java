package com.dl4jra.server.cnn.request;

public class Loaddatasetnode extends Nodeclass{
	/* Load dataset node data */
	
	private String path, trainPath, testPath;
	private char delimeter;
	private int imagewidth, imageheight, channels, batchsize, numLabels, numSkipLines, numClassLabels, labelIndex;
	private float fractionTrain;
	
	public Loaddatasetnode() {
		
	}



	public Loaddatasetnode(String nodeId, String path, String trainPath, String testPath, int imagewidth, int imageheight, int channels, int batchsize, int numLabels,
						   int numSkipLines, int numClassLabels, char delimeter, int labelIndex, float fractionTrain) {
		super(nodeId);
		this.path = path;
		this.trainPath = trainPath;
		this.testPath = testPath;
		this.imagewidth = imagewidth;
		this.imageheight = imageheight;
		this.channels = channels;
		this.batchsize = batchsize;
		this.numLabels = numLabels;
		this.numSkipLines = numSkipLines;
		this.numClassLabels = numClassLabels;
		this.delimeter = delimeter;
		this.labelIndex = labelIndex;
		this.fractionTrain = fractionTrain;
	}
	
	@Override
	public String toString() {
		return String.format("Loading dataset from : %s\nDimension: %d x %d x %d\nBatchsize: %d\nLabels: %d", 
				this.path, this.imagewidth, this.imageheight, this.channels, this.batchsize, this.numLabels);
	}

	public String getTrainPath() {
		return trainPath;
	}

	public void setTrainPath(String trainPath) {
		this.trainPath = trainPath;
	}

	public String getTestPath() {
		return testPath;
	}

	public void setTestPath(String testPath) {
		this.testPath = testPath;
	}

	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public int getImagewidth() {
		return imagewidth;
	}
	
	public void setImagewidth(int imagewidth) {
		this.imagewidth = imagewidth;
	}
	
	public int getImageheight() {
		return imageheight;
	}
	
	public void setImageheight(int imageheight) {
		this.imageheight = imageheight;
	}
	
	public int getChannels() {
		return channels;
	}
	
	public void setChannels(int channels) {
		this.channels = channels;
	}
	
	public int getBatchsize() {
		return batchsize;
	}
	
	public void setBatchsize(int batchsize) {
		this.batchsize = batchsize;
	}
	
	public int getNumLabels() {
		return numLabels;
	}

	public char getDelimeter() {
		return delimeter;
	}

	public void setDelimeter(char delimeter) {
		this.delimeter = delimeter;
	}

	public int getNumSkipLines() {
		return numSkipLines;
	}

	public void setNumSkipLines(int numSkipLines) {
		this.numSkipLines = numSkipLines;
	}

	public int getNumClassLabels() {
		return numClassLabels;
	}

	public void setNumClassLabels(int numClassLabels) {
		this.numClassLabels = numClassLabels;
	}

	public void setNumLabels(int numLabels) {
		this.numLabels = numLabels;
	}

	public int getLabelIndex() {
		return labelIndex;
	}

	public void setLabelIndex(int labelIndex) {
		this.labelIndex = labelIndex;
	}

	public float getFractionTrain() {
		return fractionTrain;
	}

	public void setFractionTrain(float fractionTrain) {
		this.fractionTrain = fractionTrain;
	}

//	public String getDelimeter() {
//		return delimeter;
//	}
//
//	public void setDelimeter(String delimeter) {
//		this.delimeter = delimeter;
//	}
}
