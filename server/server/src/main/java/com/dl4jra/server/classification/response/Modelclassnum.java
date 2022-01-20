package com.dl4jra.server.classification.response;

public class Modelclassnum {
	/* [MESSAGE MAPPING RESPONSE] "/response/classification/modelchanged" */

	// Filename of model (classifier)
	private String filename;
	// Size of output layer for classifier (Number of classes)
	private int classnum;

	public Modelclassnum() {
		
	}
	
	public Modelclassnum(String filename, int classnum) {
		this.filename = filename;
		this.classnum = classnum;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getClassnum() {
		return classnum;
	}

	public void setClassnum(int classnum) {
		this.classnum = classnum;
	}
}
