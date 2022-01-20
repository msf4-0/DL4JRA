package com.dl4jra.server.cnn.flowsaving;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.dl4jra.server.cnn.flowsaving.classes.CNNFlow;
import com.dl4jra.server.cnn.flowsaving.classes.Edge;
import com.dl4jra.server.cnn.flowsaving.classes.MTFlowEdge;
import com.dl4jra.server.cnn.flowsaving.classes.MTFlowNode;
import com.dl4jra.server.cnn.flowsaving.classes.Node;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
public class MTFLController {
	/* MULTITAB FLOW (AUTO) LOADING CONTROLLER */
	
	@Autowired
	private SimpMessagingTemplate template;
	
	/**
	 * [WEBSOCKET] Restore saved nodes/edges
	 * @throws Exception
	 */
	@MessageMapping("/cnnmtflowloading")
	public void loadMTFlow() throws Exception {
		String rootpath = Paths.get("").toAbsolutePath().normalize().toString();
		/* Json file is saved at rootpath + "/cnnflows/multitabflows.json" */
		String path = rootpath + "/cnnflows/multitabflows.json";
		File file = new File(path);
		if (file.exists()) {
			String cnnmtflowsjsonstring = new String(Files.readAllBytes(Paths.get(path)));
			JsonObject cnnmtflows = new Gson().fromJson(cnnmtflowsjsonstring, JsonObject.class);
			for (int counter = 0; counter < cnnmtflows.size(); counter++) {
				String cnnflowjsonstring = cnnmtflows.get("FLOW" + counter).getAsString();
				CNNFlow flow = new Gson().fromJson(cnnflowjsonstring, CNNFlow.class);
				JsonArray nodelist = flow.getNodelist();
				for (int index = 0; index < nodelist.size(); index++) {
					String nodestring = nodelist.get(index).getAsString();
					Node node = new Gson().fromJson(nodestring, Node.class);
					this.template.convertAndSend("/response/cnnmtflowloading/node", 
							new MTFlowNode(counter, node));
				}
				JsonArray edgelist = flow.getEdgelist();
				for (int index = 0; index < edgelist.size(); index++) {
					String edgestring = edgelist.get(index).getAsString();
					Edge edge = new Gson().fromJson(edgestring, Edge.class);
					this.template.convertAndSend("/response/cnnmtflowloading/edge", 
							new MTFlowEdge(counter, edge));
				}
			}
		}
	}
}










