package com.dl4jra.server.odetection.request;

public class Preprocessedimage {
	/* Image data for object detection */

	private String base64encodedstring;
	private int length;
	private int outputwidth;
	private int outputheight;
	
	public Preprocessedimage() {
		
	}
	
	/**
	 * Constructor
	 * @param base64encodedstring - Base64 encoded image data
	 * @param length - Length of image data (validation)
	 * @param outputwidth - Expected output image width
	 * @param outputheight - Expected output image height
	 */
	public Preprocessedimage(String base64encodedstring, int length, int outputwidth, int outputheight) {
		this.base64encodedstring = base64encodedstring;
		this.length = length;
		this.outputwidth = outputwidth;
		this.outputheight = outputheight;
	}

	/* base64encodedstring getter function */
	public String getBase64encodedstring() {
		return base64encodedstring;
	}

	/* base64encodedstring setter function */
	public void setBase64encodedstring(String base64encodedstring) {
		this.base64encodedstring = base64encodedstring;
	}

	/* length getter function */
	public int getLength() {
		return length;
	}

	/* length setter function */
	public void setLength(int length) {
		this.length = length;
	}

	/* outputwidth getter function */
	public int getOutputwidth() {
		return outputwidth;
	}

	/* outputwidth setter function */
	public void setOutputwidth(int outputwidth) {
		this.outputwidth = outputwidth;
	}

	/* outputheight getter function */
	public int getOutputheight() {
		return outputheight;
	}

	/* outputheight setter function */
	public void setOutputheight(int outputheight) {
		this.outputheight = outputheight;
	}
	
	
}
