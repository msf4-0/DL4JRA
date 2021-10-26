package com.dl4jra.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Service
@Controller
public class ServerController {
	
	@Autowired
	private RestartEndpoint restartEndpoint;
	@Autowired
	private Environment env;
	
	// Set "BACKEND_PRIORITY_GPU" to 20 and reset server
	@MessageMapping("/backend/gpu")
	public void switchbackendtogpu() throws Exception {
		System.out.println("[SERVER BACKEND] - SWITCHING TO GPU");
		Process process = Runtime.getRuntime().exec("SETX BACKEND_PRIORITY_GPU \"20\"");
		process.waitFor();
		restartEndpoint.restart();
	}
	
	// Set "BACKEND_PRIORITY_GPU" to 5 and reset server
	@MessageMapping("/backend/cpu")
	public void switchbackendtocpu() throws Exception {
		System.out.println("[SERVER BACKEND] - SWITCHING TO CPU");
		Process process = Runtime.getRuntime().exec("SETX BACKEND_PRIORITY_GPU \"5\"");
		process.waitFor();
		restartEndpoint.restart();
	}
	
	// Get CPU & GPU backend priority value
	@MessageMapping("/backend/testbackend")
	public void testbackend() throws Exception {
		String cpupriority = env.getProperty("BACKEND_PRIORITY_CPU");
		String gpupriority = env.getProperty("BACKEND_PRIORITY_GPU");
		System.out.println("CPU PRIORITY - " + cpupriority);
		System.out.println("GPU PRIORITY - " + gpupriority);
	}
	
	// Get CPU & GPU backend priority value
	@MessageMapping("/getbackend")
	@SendTo("/response/backend")
	public String getbackend() throws Exception {
		String cpuprioritystring = System.getenv("BACKEND_PRIORITY_CPU");
		String gpuprioritystring = System.getenv("BACKEND_PRIORITY_GPU");
		if (cpuprioritystring == null || gpuprioritystring == null) return "gpu";
		int cpupriority = Integer.parseInt(cpuprioritystring);
		int gpupriority = Integer.parseInt(gpuprioritystring);
		return gpupriority >= cpupriority? "gpu" : "cpu";
	}
}
