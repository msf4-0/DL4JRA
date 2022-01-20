package com.dl4jra.server.cnn.flowsaving.classes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class CNNFlow {
	/* CNNFlow */
	
	private JsonArray nodelist = new JsonArray();
	private JsonArray edgelist = new JsonArray();
	
	/**
	 * Add node to nodelist
	 * @param node - node data
	 */
	public void addnode(Node node) {
		this.nodelist.add(node.toJsonString());
	}
	
	/**
	 * Add node to nodelist
	 * @param node - node data (in JSON string)
	 */
	public void addnode(String nodejsonstring) {
		this.nodelist.add(nodejsonstring);
	}
	
	/**
	 * Add edge to edgelist
	 * @param edge - edge data
	 */
	public void addedge(Edge edge) {
		this.edgelist.add(edge.toJsonString());
	}
	
	/**
	 * Add edge to edgelist
	 * @param edge - edge data (in JSON string)
	 */
	public void addedge(String edgejsonstring) {
		this.edgelist.add(edgejsonstring);
	}
	
	/*nodelist getter function*/
	public JsonArray getNodelist() {
		return nodelist;
	}
	
	/* nodelist setter function */
	public void setNodelist(JsonArray nodelist) {
		this.nodelist = nodelist;
	}
	
	/* edgelist getter function */
	public JsonArray getEdgelist() {
		return edgelist;
	}
	
	/* edgelist setter function */
	public void setEdgelist(JsonArray edgelist) {
		this.edgelist = edgelist;
	}
	
	/* Return CNNFlow as JSON string */
	public String toJsonString() {
		return new Gson().toJson(this);
	}
	
	
}
