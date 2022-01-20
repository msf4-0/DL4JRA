package com.dl4jra.server.cnn.flowsaving.classes;

public class Flowsaving {
	/* Flowsaving data */
	private String directory, filename;
	
	public Flowsaving() {
		
	}

	/**
	 * Constructor
	 * @param directory - Directory to save JSON file
	 * @param filename - Filename of JSON file
	 */
	public Flowsaving(String directory, String filename) {
		this.directory = directory;
		this.filename = filename;
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
}
