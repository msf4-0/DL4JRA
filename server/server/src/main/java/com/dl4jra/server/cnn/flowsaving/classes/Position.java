package com.dl4jra.server.cnn.flowsaving.classes;

public class Position {
	/* Position of node */

	private int x, y;
	
	public Position() {
		
	}
	
	/**
	 * Constructor
	 * @param x - x-coordinate of node
	 * @param y - y-coordinate of node
	 */
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/* x-coordinate getter function */
	public int getX() {
		return x;
	}

	/* x-coordinate setter function */
	public void setX(int x) {
		this.x = x;
	}

	/* y-coordinate getter function */
	public int getY() {
		return y;
	}

	/* y-coordinate setter function */
	public void setY(int y) {
		this.y = y;
	}
}
