package com.dl4jra.server.cnn.flowsaving;

import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.dl4jra.server.cnn.flowsaving.classes.CNNFlow;
import com.dl4jra.server.cnn.flowsaving.classes.Edge;
import com.dl4jra.server.cnn.flowsaving.classes.MTFlowEdge;
import com.dl4jra.server.cnn.flowsaving.classes.MTFlowNode;
import com.dl4jra.server.cnn.flowsaving.classes.Node;
import com.google.gson.JsonObject;

@Controller
public class MTFSController {
	/* MULTITAB FLOW (AUTO) SAVING CONTROLLER */
	
	@Autowired
	private SimpMessagingTemplate template;
	private static ArrayList<CNNFlow> flows = new ArrayList<CNNFlow>();
	
	/**
	 * [WEBSOCKET] Reset flows
	 * @throws Exception
	 */
	@MessageMapping("/cnnmtflowsaving/reset")
	public void resetflows() throws Exception {
		flows = new ArrayList<CNNFlow>();
	}
	
	/**
	 * @deprecated
	 */
	@MessageMapping("/cnnmtflowsaving/node/{tabindex}")
	public void saveNode(@DestinationVariable("tabindex") int tabindex, Node node) throws Exception {
		if (flows.size() <= tabindex) {
			int numbertoadd = tabindex - flows.size() + 1;
			for (int counter = 0; counter < numbertoadd; counter++) {
				flows.add(new CNNFlow());
			}
		}
		flows.get(tabindex).addnode(node);
		this.template.convertAndSend("/response/cnnmtflowsaving/elementsaved", "");
	}
	
	/**
	 * [WEBSOCKET] Save node
	 * @param mtnode - tab index + node data
	 * @throws Exception
	 */
	@MessageMapping("/cnnmtflowsaving/node")
	public void saveNode(MTFlowNode mtnode) throws Exception {
		int tabindex = mtnode.getFlowindex();
		if (flows.size() <= tabindex) {
			int numbertoadd = tabindex - flows.size() + 1;
			for (int counter = 0; counter < numbertoadd; counter++) {
				flows.add(new CNNFlow());
			}
		}
		flows.get(tabindex).addnode(mtnode.getNode());
		this.template.convertAndSend("/response/cnnmtflowsaving/elementsaved", "");
	}
	
	/**
	 * @deprecated
	 */
	@MessageMapping("/cnnmtflowsaving/edge/{tabindex}")
	public void saveEdge(@DestinationVariable("tabindex") int tabindex, Edge edge) throws Exception {
		if (flows.size() <= tabindex) {
			int numbertoadd = tabindex - flows.size() + 1;
			for (int counter = 0; counter < numbertoadd; counter++) {
				flows.add(new CNNFlow());
			}
		}
		flows.get(tabindex).addedge(edge);
		this.template.convertAndSend("/response/cnnmtflowsaving/elementsaved", "");
	}
	
	/**
	 * [WEBSOCKET] Save edge
	 * @param mtedge - tab index + edge data
	 * @throws Exception
	 */
	@MessageMapping("/cnnmtflowsaving/edge")
	public void saveEdge(MTFlowEdge mtedge) throws Exception {
		int tabindex = mtedge.getFlowindex();
		if (flows.size() <= tabindex) {
			int numbertoadd = tabindex - flows.size() + 1;
			for (int counter = 0; counter < numbertoadd; counter++) {
				flows.add(new CNNFlow());
			}
		}
		flows.get(tabindex).addedge(mtedge.getEdge());
		this.template.convertAndSend("/response/cnnmtflowsaving/elementsaved", "");
	}
	
	/**
	 * [WEBSOCKET]  Export to JSON file 
	 * @throws Exception
	 */
	@MessageMapping("/cnnmtflowsaving/saveflow")
	public void saveFlow() throws Exception {
		JsonObject cnnmtflows = new JsonObject();
		for (int counter = 0; counter < flows.size(); counter ++) {
			cnnmtflows.addProperty("FLOW" + counter, flows.get(counter).toJsonString());
		}
		String rootpath = Paths.get("").toAbsolutePath().normalize().toString();
		/* Multitab cnn flows will be saved to rootpath + "/cnnflows/multitabflows.json" */
		FileWriter jsonfile = new FileWriter(rootpath + "/cnnflows/multitabflows.json");
		jsonfile.write(cnnmtflows.toString());
		jsonfile.flush();
		jsonfile.close();
		this.template.convertAndSend("/response/cnnmtflowsaving/complete", "");
		System.out.println("[MTFSCONTROLLER] ENTIRE FLOW HAS BEEN SAVED.");
	}
}














