package com.dl4jra.server.odetection.request;

public class Loggingdata {
	private boolean logging;
	
	public Loggingdata() {
		
	}

	public Loggingdata(boolean logging) {
		this.logging = logging;
	}

	public boolean isLogging() {
		return logging;
	}

	public void setLogging(boolean logging) {
		this.logging = logging;
	}
}
