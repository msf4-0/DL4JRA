package com.dl4jra.server.cnn.flowsaving.classes;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

public class Node {
	/* Node data */
	private String id, type;
	private Position position;
	private Hashtable<String, String> data;
	
	public Node() {
		
	}
	
	/**
	 * Node constructor
	 * @param id
	 * @param type
	 * @param position
	 * @param data
	 */
	public Node(String id, String type, Position position, Hashtable<String, String> data) {
		this.id = id;
		this.type = type;
		this.position = position;
		this.data = data;
	}

	/* id getter function */
	public String getId() {
		return id;
	}

	/* id setter function */
	public void setId(String id) {
		this.id = id;
	}

	/* nodetype getter function */
	public String getType() {
		return type;
	}

	/* nodetype setter function */
	public void setType(String type) {
		this.type = type;
	}

	/* data getter function */
	public Hashtable<String, String> getData() {
		return data;
	}

	/* data setter function */
	public void setData(Hashtable<String, String> data) {
		this.data = data;
	}
	
	/* position getter function */
	public Position getPosition() {
		return position;
	}

	/* position setter function */
	public void setPosition(Position position) {
		this.position = position;
	}

	/* Get Node class as JSON string */
	public String toJsonString() {
		return new Gson().toJson(this);
	}

	@Override
	public String toString() {
		return String.format(
				"=========================\n"
				+ "[NODE]\n"
				+ "ID: %s\n"
				+ "TYPE: %s\n"
				+ "POSITION: (%d, %d)\n"
//				+ getDataInString()
				+ "=========================\n",
				this.id, this.type, this.position.getX(), this.position.getY(), this.data.get("name"));
	}
	
	@SuppressWarnings("unused")
	private String getDataInString() {
		String output = "";
		Iterator<Entry<String, String>> itr = this.data.entrySet().iterator();
		 
		Map.Entry<String, String> entry = null;
		while(itr.hasNext()){
		    entry = itr.next();
		    output += entry.getKey() + " -> " + entry.getValue() + "\n";
		}
		return output;
	}

}
