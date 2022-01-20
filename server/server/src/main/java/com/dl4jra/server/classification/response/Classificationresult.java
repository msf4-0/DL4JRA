package com.dl4jra.server.classification.response;

public class Classificationresult {
	/* [MESSAGE MAPPING RESPONSE] "/response/classification/result" */
	
	// Result (index of class) of classification (prediction)
	private int result;

	public Classificationresult() {
		
	}
	
	public Classificationresult(int result) {
		this.result = result;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
	
	
}
