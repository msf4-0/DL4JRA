package com.dl4jra.server.odetection.response;

public class Processedimage {
	private String base64encodedstring;
	private int length;
	
	public Processedimage() {
		
	}
	
	public Processedimage(String base64encodedstring, int length) {
		this.base64encodedstring = base64encodedstring;
		this.length = length;
	}

	public String getBase64encodedstring() {
		return base64encodedstring;
	}
	public void setBase64encodedstring(String base64encodedstring) {
		this.base64encodedstring = base64encodedstring;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	
	
}
