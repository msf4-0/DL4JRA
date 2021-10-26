package com.dl4jra.server.cnn.flowsaving.classes;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

public class Edge {
	/*  Edge data */
	public String id, source, sourceHandle, target, targetHandle, type, arrowHeadType;
	public Hashtable<String, String> style;

	public Edge() {
		
	}
	/**
	 * Edge constructor
	 * @param id 
	 * @param source
	 * @param sourceHandle
	 * @param target
	 * @param targetHandle
	 * @param type
	 * @param arrowHeadType
	 * @param style
	 */
	public Edge(String id, String source, String sourceHandle, String target, String targetHandle, String type,
			String arrowHeadType, Hashtable<String, String> style) {
		this.id = id;
		this.source = source;
		this.sourceHandle = sourceHandle;
		this.target = target;
		this.targetHandle = targetHandle;
		this.type = type;
		this.arrowHeadType = arrowHeadType;
		this.style = style;
	}

	/* id getter function */
	public String getId() {
		return id;
	}

	/* id setter function */
	public void setId(String id) {
		this.id = id;
	}

	/* source getter function */
	public String getSource() {
		return source;
	}

	/* source setter function */
	public void setSource(String source) {
		this.source = source;
	}

	/* sourcehandle getter function */
	public String getSourceHandle() {
		return sourceHandle;
	}

	/* sourcehandle setter function */
	public void setSourceHandle(String sourceHandle) {
		this.sourceHandle = sourceHandle;
	}

	/* target getter function */
	public String getTarget() {
		return target;
	}

	/* target setter function */
	public void setTarget(String target) {
		this.target = target;
	}

	/* targethandle getter function */
	public String getTargetHandle() {
		return targetHandle;
	}

	/* targethandle setter function */
	public void setTargetHandle(String targetHandle) {
		this.targetHandle = targetHandle;
	}

	/* type getter function */
	public String getType() {
		return type;
	}

	/* type setter function */
	public void setType(String type) {
		this.type = type;
	}

	/* arrowheadtype getter function */
	public String getArrowHeadType() {
		return arrowHeadType;
	}

	/* arrowheadtype setter function */
	public void setArrowHeadType(String arrowHeadType) {
		this.arrowHeadType = arrowHeadType;
	}

	/* style getter function */
	public Hashtable<String, String> getStyle() {
		return style;
	}

	/* style setter function */
	public void setStyle(Hashtable<String, String> style) {
		this.style = style;
	}
	
	/* Get Edge class as JSON string */
	public String toJsonString() {
		return new Gson().toJson(this);
	}

	@Override
	public String toString() {
		return String.format(""
				+ "\n=========================\n"
				+ "[EDGE]\n"
				+ "ID: %s\n"
				+ "SOURCE: %s\n"
				+ "TARGET: %s\n"
//				+ getStyleInString()
				+ "=========================\n",
				this.id, this.source, this.target);
	}

	@SuppressWarnings("unused")
	private String getStyleInString() {
		String output = "";
		Iterator<Entry<String, String>> itr = this.style.entrySet().iterator();
		 
		Map.Entry<String, String> entry = null;
		while(itr.hasNext()){
		    entry = itr.next();
		    output += entry.getKey() + " -> " + entry.getValue() + "\n";
		}
		return output;
	}
}
