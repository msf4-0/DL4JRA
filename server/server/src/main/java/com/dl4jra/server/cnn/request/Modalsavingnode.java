package com.dl4jra.server.cnn.request;

public class Modalsavingnode extends Nodeclass {
	/* Save (export) model node data */
	
	private String path, filename;
	
	public Modalsavingnode() {
		
	}
	
	public Modalsavingnode(String nodeId, String path, String filename) {
		super(nodeId);
		this.path = path;
		this.filename = filename;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
