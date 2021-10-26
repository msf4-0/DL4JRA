package com.dl4jra.server.cnn.flowsaving;

import java.io.FileWriter;
import java.nio.file.Paths;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.dl4jra.server.cnn.flowsaving.classes.Edge;
import com.dl4jra.server.cnn.flowsaving.classes.Node;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
public class FSController {
	private static JsonArray nodelist, edgelist;
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@PostConstruct
	public void initialization() {
		this.resetNodeAndEdgeList();
	}
	
	// RESET FLOW
	@MessageMapping("/cnnflowsaving/reset")
	public void resetFlow() throws Exception {
		System.out.println("[FSCONTROLLER] NODE/EDGE LIST HAS BEEN RESET");
		this.resetNodeAndEdgeList();
	}
	
	// ADD NODE
	@MessageMapping("/cnnflowsaving/node")
	public void saveNode(Node node) throws Exception {
		String nodejson = node.toJsonString();
		nodelist.add(nodejson);
		this.template.convertAndSend("/response/cnnflowsaving/elementsaved", "");
		System.out.println("[FSCONTROLLER] NODE ID " + node.getId() + " HAS BEEN SAVED.");
	}
	
	// ADD EDGE
	@MessageMapping("/cnnflowsaving/edge")
	public void saveEdge(Edge edge) throws Exception {
		String edgejson = edge.toJsonString();
		edgelist.add(edgejson);
		this.template.convertAndSend("/response/cnnflowsaving/elementsaved", "");
		System.out.println("[FSCONTROLLER] EDGE ID " + edge.getId() + " HAS BEEN SAVED.");
	}
	
	// SAVE TO JSON FILE
	@MessageMapping("/cnnflowsaving/saveflow")
	public void saveFlow() throws Exception {
		JsonObject cnnflow = new JsonObject();
		cnnflow.add("nodelist", nodelist);
		cnnflow.add("edgelist", edgelist);
		String rootpath = Paths.get("").toAbsolutePath().normalize().toString();
		FileWriter jsonfile = new FileWriter(rootpath + "/cnnflows/flow.json");
		jsonfile.write(cnnflow.toString());
		jsonfile.flush();
		jsonfile.close();
		this.template.convertAndSend("/response/cnnflowsaving/complete", "");
		System.out.println("[FSCONTROLLER] ENTIRE FLOW HAS BEEN SAVED.");
	}
	
	// EMPTY NODE AND EDGE LIST
	private void resetNodeAndEdgeList() {
		nodelist = new JsonArray();
		edgelist = new JsonArray();
	}
}
