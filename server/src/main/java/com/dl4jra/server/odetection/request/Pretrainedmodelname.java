package com.dl4jra.server.odetection.request;

public class Pretrainedmodelname {
	/* Name of pretrained model */

	private String modelname;
	
	public Pretrainedmodelname() {
		
	}

	public Pretrainedmodelname(String modelname) {
		this.modelname = modelname;
	}

	public String getModelname() {
		return modelname;
	}

	public void setModelname(String modelname) {
		this.modelname = modelname;
	}
}
