package com.dl4jra.server.odetection;

import java.util.ArrayList;

public class ODModelConfigurationData {
	/* Pretrained model details class */
	
	private ArrayList<String> classes;
	private int modelinputwidth, modelinputheight, modelinputchannel, gridwidth, gridheight;
	private String modelpath;
	
	public ODModelConfigurationData() {
		
	}

	/**
	 * Constructor
	 * @param classes - model's labels
	 * @param modelinputwidth
	 * @param modelinputheight
	 * @param modelinputchannel
	 * @param gridwidth
	 * @param gridheight
	 * @param modelpath - path to pretrained model
	 */
	public ODModelConfigurationData(ArrayList<String> classes, int modelinputwidth, int modelinputheight,
										 int modelinputchannel, int gridwidth, int gridheight, String modelpath) {
		this.classes = classes;
		this.modelinputwidth = modelinputwidth;
		this.modelinputheight = modelinputheight;
		this.modelinputchannel = modelinputchannel;
		this.gridwidth = gridwidth;
		this.gridheight = gridheight;
		this.modelpath = modelpath;
	}

	/**
	 * Constructor
	 * @param classes - model's labels
	 * @param modelinputwidth
	 * @param modelinputheight
	 * @param modelinputchannel
	 * @param gridwidth
	 * @param gridheight
	 */
	public ODModelConfigurationData(ArrayList<String> classes, int modelinputwidth, int modelinputheight,
									int modelinputchannel, int gridwidth, int gridheight) {
		this.classes = classes;
		this.modelinputwidth = modelinputwidth;
		this.modelinputheight = modelinputheight;
		this.modelinputchannel = modelinputchannel;
		this.gridwidth = gridwidth;
		this.gridheight = gridheight;
		this.modelpath = null;
	}

	/* classes getter function */
	public ArrayList<String> getClasses() {
		return classes;
	}

	/* classes setter function */
	public void setClasses(ArrayList<String> classes) {
		this.classes = classes;
	}

	/* modelinputwidth getter function */
	public int getModelinputwidth() {
		return modelinputwidth;
	}

	/* modelinputwidth setter function */
	public void setModelinputwidth(int modelinputwidth) {
		this.modelinputwidth = modelinputwidth;
	}

	/* modelinputheight getter function */
	public int getModelinputheight() {
		return modelinputheight;
	}

	/* modelinputheight setter function */
	public void setModelinputheight(int modelinputheight) {
		this.modelinputheight = modelinputheight;
	}

	/* modelinputchannel getter function */
	public int getModelinputchannel() {
		return modelinputchannel;
	}

	/* modelinputchannel setter function */
	public void setModelinputchannel(int modelinputchannel) {
		this.modelinputchannel = modelinputchannel;
	}

	/* gridwidth getter function */
	public int getGridwidth() {
		return gridwidth;
	}
	
	/* gridwidth setter function */
	public void setGridwidth(int gridwidth) {
		this.gridwidth = gridwidth;
	}

	/* gridheight getter function */
	public int getGridheight() {
		return gridheight;
	}

	/* gridheight setter function */
	public void setGridheight(int gridheight) {
		this.gridheight = gridheight;
	}

	/* modelpath getter function */
	public String getModelpath() {
		return modelpath;
	}
	
	/* modelpath setter function */
	public void setModelpath(String modelpath) {
		this.modelpath = modelpath;
	}
}
