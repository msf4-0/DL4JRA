package com.dl4jra.server.cnn.response;

public class UpdateResponse {
	/* Update current and max progress */

	private int currentprogress, maxprogress;

	public UpdateResponse() {
		
	}

	public UpdateResponse(int currentprogress, int maxprogress) {
		this.currentprogress = currentprogress;
		this.maxprogress = maxprogress;
	}

	/* currentprogress getter function */
	public int getCurrentprogress() {
		return currentprogress;
	}

	/* currentprogress setter function */
	public void setCurrentprogress(int currentprogress) {
		this.currentprogress = currentprogress;
	}

	/* maxprogress getter function */
	public int getMaxprogress() {
		return maxprogress;
	}

	/* maxprogress setter function */
	public void setMaxprogress(int maxprogress) {
		this.maxprogress = maxprogress;
	}
	
}
