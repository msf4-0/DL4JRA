package com.dl4jra.server.cnn.flowsaving;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.dl4jra.server.cnn.flowsaving.classes.Edge;
import com.dl4jra.server.cnn.flowsaving.classes.Node;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
public class FLController {

	@Autowired
	private SimpMessagingTemplate template;

	@MessageMapping("/cnnflowloading")
	public void loadFlow() throws Exception {
		String rootpath = Paths.get("").toAbsolutePath().normalize().toString();
		String path = rootpath + "/cnnflows/flow.json";
		File file = new File(path);
		if (file.exists()) {
			String jsonstring = new String(Files.readAllBytes(Paths.get(path)));
			JsonObject flows = new Gson().fromJson(jsonstring, JsonObject.class);
			JsonArray nodelist = flows.get("nodelist").getAsJsonArray();
			JsonArray edgelist = flows.get("edgelist").getAsJsonArray();
			System.out.println("[FLCONTROLLER] NUMBER OF NODES: " + nodelist.size());
			System.out.println("[FLCONTROLLER] NUMBER OF EDGES: " + edgelist.size());
			
			for (int index = 0; index < nodelist.size(); index ++) {
				String nodestring = nodelist.get(index).getAsString();
				Node node = new Gson().fromJson(nodestring, Node.class);
				this.template.convertAndSend("/response/cnnflowloading/node", node);
			}
			
			for (int index = 0; index < edgelist.size(); index ++) {
				String edgestring = edgelist.get(index).getAsString();
				Edge edge = new Gson().fromJson(edgestring, Edge.class);
				this.template.convertAndSend("/response/cnnflowloading/edge", edge);
			}
		}
	}
}















