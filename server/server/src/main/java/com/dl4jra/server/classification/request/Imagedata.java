package com.dl4jra.server.classification.request;

public class Imagedata {
	/* [MESSAGE MAPPING PARAM] "/classification/modelchanged" */
	
	// Base64 encoded image data
	private String base64encodedimage;

	public Imagedata() {
		
	}
	
	public Imagedata(String base64encodedimage) {
		this.base64encodedimage = base64encodedimage;
	}

	public String getBase64encodedimage() {
		return base64encodedimage;
	}

	public void setBase64encodedimage(String base64encodedimage) {
		this.base64encodedimage = base64encodedimage;
	}
	
}
