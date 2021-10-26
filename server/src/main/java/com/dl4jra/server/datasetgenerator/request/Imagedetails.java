package com.dl4jra.server.datasetgenerator.request;

public class Imagedetails {
	/* Image data required for screenshot saving */

	private String base64encodedstring;
	private String directory;
	private String filename;
	private int length;
	
	public Imagedetails() {
		
	}
	
	/**
	 * Constructor
	 * @param base64encodedstring - Base64 encoded image data
	 * @param length - Length of string (check/validation)
	 * @param directory - Directory to save
	 * @param filename - Filename of image 
	 */
	public Imagedetails(String base64encodedstring, int length, String directory, String filename) {
		this.base64encodedstring = base64encodedstring;
		this.length = length;
		this.directory = directory;
		this.filename = filename;
	}
	
	/* base64encodedstring getter function */
	public String getBase64encodedstring() {
		return base64encodedstring;
	}

	/* base64encodedstring setter function */
	public void setBase64encodedstring(String base64encodedstring) {
		this.base64encodedstring = base64encodedstring;
	}
	
	/* directory getter function */
	public String getDirectory() {
		return directory;
	}
	
	/* directory setter function */
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/* filename getter function */
	public String getFilename() {
		return filename;
	}

	/* filename setter function */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/* length getter function */
	public int getLength() {
		return length;
	}

	/* length setter function */
	public void setLength(int length) {
		this.length = length;
	}
}
