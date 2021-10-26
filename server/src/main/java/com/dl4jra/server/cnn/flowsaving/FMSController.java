package com.dl4jra.server.cnn.flowsaving;

import java.io.File;
import java.io.FileWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.dl4jra.server.cnn.flowsaving.classes.Edge;
import com.dl4jra.server.cnn.flowsaving.classes.Flowsaving;
import com.dl4jra.server.cnn.flowsaving.classes.Node;
import com.dl4jra.server.globalresponse.Messageresponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
public class FMSController {
	/* MANUAL FLOW SAVING CONTROLLER */ 
	
	private static JsonArray nodelist = new JsonArray(); 
	private static JsonArray edgelist = new JsonArray();
	
	@Autowired
	private SimpMessagingTemplate template;
	
	/**
	 * Reset nodelist and edgelist
	 * @throws Exception
	 */
	@MessageMapping("/cnnmanualflowsaving/reset")
	public void resetFlow() throws Exception {
		System.out.println("[MANUAL FSCONTROLLER] NODE/EDGE LIST HAS BEEN RESET");
		this.resetNodeAndEdgeList();
		this.template.convertAndSend("/response/cnnmanualflowsaving/readytostart", "");
	}
	
	/**
	 * Add node to nodelist for saving
	 * @param node - node data
	 * @throws Exception
	 */
	@MessageMapping("/cnnmanualflowsaving/node")
	public void saveNode(Node node) throws Exception {
		String nodejson = node.toJsonString();
		nodelist.add(nodejson);
		this.template.convertAndSend("/response/cnnmanualflowsaving/elementsaved", "");
		System.out.println("[MANUAL FSCONTROLLER] NODE ID " + node.getId() + " HAS BEEN SAVED.");
	}
	
	/**
	 * Add edge to edgelist for saving
	 * @param edge - edge data
	 * @throws Exception
	 */
	@MessageMapping("/cnnmanualflowsaving/edge")
	public void saveEdge(Edge edge) throws Exception {
		String edgejson = edge.toJsonString();
		edgelist.add(edgejson);
		this.template.convertAndSend("/response/cnnmanualflowsaving/elementsaved", "");
		System.out.println("[MANUAL FSCONTROLLER] EDGE ID " + edge.getId() + " HAS BEEN SAVED.");
	}
	
	/**
	 * Export to JSON file
	 * @param flowsavingdata
	 * @throws Exception
	 */
	@MessageMapping("/cnnmanualflowsaving/saveflow")
	public void saveFlow(Flowsaving flowsavingdata) throws Exception {
		File directorylocation = new File(flowsavingdata.getDirectory());
		if (! directorylocation.exists() || ! directorylocation.isDirectory()) {
			throw new Exception("DIRECTORY NOT FOUND");
		}
		String filepath = flowsavingdata.getDirectory() + "/" + flowsavingdata.getFilename() + ".json";
		File filelocation = new File(filepath);
		if (filelocation.exists() && ! filelocation.isDirectory()) {
			throw new Exception("FILENAME ALREADY EXISTS");
		}
		JsonObject cnnflow = new JsonObject();
		cnnflow.add("nodelist", nodelist);
		cnnflow.add("edgelist", edgelist);
		FileWriter jsonfile = new FileWriter(filepath);
		jsonfile.write(cnnflow.toString());
		jsonfile.flush();
		jsonfile.close();
		this.template.convertAndSend("/response/cnnmanualflowsaving/complete", "");
		System.out.println("[MANUAL FSCONTROLLER] ENTIRE FLOW HAS BEEN SAVED TO " + filepath.toUpperCase());
	}
	
	/**
	 * Exception handling
	 * @param exception
	 */
	@MessageExceptionHandler
	public void handleException(Exception exception) {
		System.out.println("[MANUAL FSCONTROLLER] MESSAGE EXCEPTION : " + exception.getMessage().toUpperCase());
		this.template.convertAndSend("/response/cnnmanualflowsaving/error", new Messageresponse(exception.getMessage()));
	}
	
	/**
	 * Reset nodelist and edgelist
	 */
	private void resetNodeAndEdgeList() {
		nodelist = new JsonArray();
		edgelist = new JsonArray();
	}
}
