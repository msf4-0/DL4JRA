package com.dl4jra.server.cnn.request;

public class Loaddatasetnode extends Nodeclass{
	/* Load dataset node data */
	
	private String path;
	private char delimeter;
	private int imagewidth, imageheight, channels, batchsize, numLabels, numSkipLines, numClassLabels;
	
	public Loaddatasetnode() {
		
	}
	
	public Loaddatasetnode(String nodeId, String path, int imagewidth, int imageheight, int channels, int batchsize, int numLabels,
						   int numSkipLines, int numClassLabels, char delimeter) {
		super(nodeId);
		this.path = path;
		this.imagewidth = imagewidth;
		this.imageheight = imageheight;
		this.channels = channels;
		this.batchsize = batchsize;
		this.numLabels = numLabels;
		this.numSkipLines = numSkipLines;
		this.numClassLabels = numClassLabels;
		this.delimeter = delimeter;
	}
	
	@Override
	public String toString() {
		return String.format("Loading dataset from : %s\nDimension: %d x %d x %d\nBatchsize: %d\nLabels: %d", 
				this.path, this.imagewidth, this.imageheight, this.channels, this.batchsize, this.numLabels);
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

//	public String getDelimeter() {
//		return delimeter;
//	}
//
//	public void setDelimeter(String delimeter) {
//		this.delimeter = delimeter;
//	}
}
