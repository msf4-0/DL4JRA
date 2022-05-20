package com.dl4jra.server.cnn.flowsaving;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.dl4jra.server.cnn.flowsaving.classes.*;
import com.dl4jra.server.globalresponse.Messageresponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
public class FLController {

	@Autowired
	private SimpMessagingTemplate template;

	@MessageMapping("/cnnflowloading")
	public void loadFlow(Flowsaving flowsavingInfo) throws Exception {
		File directorylocation = new File(flowsavingInfo.getDirectory());
		if (! directorylocation.exists() || ! directorylocation.isDirectory()) {
			throw new Exception("DIRECTORY NOT FOUND");
		}
		String path = flowsavingInfo.getDirectory() + "/" + flowsavingInfo.getFilename() + ".json";
		System.out.println(path);
		File file = new File(path);

		if (file.exists()) {
			String jsonstring = new String(Files.readAllBytes(Paths.get(path)));
			JsonObject flows = new Gson().fromJson(jsonstring, JsonObject.class);
			JsonArray nodelist = flows.get("nodelist").getAsJsonArray();
			System.out.println(nodelist);
			JsonArray edgelist = flows.get("edgelist").getAsJsonArray();
			System.out.println(edgelist);
			System.out.println("[FLCONTROLLER] NUMBER OF NODES: " + nodelist.size());
			System.out.println("[FLCONTROLLER] NUMBER OF EDGES: " + edgelist.size());
			
			for (int index = 0; index < nodelist.size(); index ++) {
				String nodestring = nodelist.get(index).getAsString();
				Node node = new Gson().fromJson(nodestring, Node.class);
				System.out.println(node);
				System.out.println(nodestring);
				this.template.convertAndSend("/response/cnnflowloading/node", new FLFlowNode(node));
			}
			
			for (int index = 0; index < edgelist.size(); index ++) {
				String edgestring = edgelist.get(index).getAsString();
				Edge edge = new Gson().fromJson(edgestring, Edge.class);
				System.out.println(edge);
				System.out.println(edgestring);
				this.template.convertAndSend("/response/cnnflowloading/edge", new FLFlowEdge(edge));
			}
			// inform the front end that the loading of nodes and edges are completed
			this.template.convertAndSend("/response/cnnflowloading/complete", "");
		} else {
			throw new Exception("FILE DOES NOT EXIST");
		}


	}

	/**
	 * Exception handling
	 * @param exception
	 */
	@MessageExceptionHandler
	public void handleException(Exception exception) {
		System.out.println("[MANUAL FLCONTROLLER] MESSAGE EXCEPTION : " + exception.getMessage().toUpperCase());
		this.template.convertAndSend("/response/cnnflowloading/error", new Messageresponse(exception.getMessage()));
	}

}















