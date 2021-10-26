package com.dl4jra.server.odetection.response;


public class Modelconfigurationdata {
	/* Pretrained model name and screenshot dimension (input dimension) */
	
	private String name;
	private int screenshotdimension;
	
	public Modelconfigurationdata () {
		
	}
	/**
	 * Constructor 
	 * @param name - Name of pretrained model
	 * @param screenshotdimension - Input dimension of pretrained model
	 */
	public Modelconfigurationdata(String name, int screenshotdimension) {
		this.name = name;
		this.screenshotdimension = screenshotdimension;
	}

	/* name getter function */
	public String getName() {
		return name;
	}

	/* name setter function */
	public void setName(String name) {
		this.name = name;
	}

	/* screenshotdimension getter function */
	public int getScreenshotdimension() {
		return screenshotdimension;
	}

	/* screenshotdimension setter function */
	public void setScreenshotdimension(int screenshotdimension) {
		this.screenshotdimension = screenshotdimension;
	}
}
