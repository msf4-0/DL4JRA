package com.dl4jra.server.classification.request;

public class Modelpath {
	/* [MESSAGE MAPPING PARAM] "/classification/classify" */
	
	// Path to classifier (classification model)
	private String path;

	public Modelpath() {
		
	}
	
	public Modelpath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
