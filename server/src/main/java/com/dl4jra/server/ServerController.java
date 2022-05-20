package com.dl4jra.server;

import org.nd4j.linalg.factory.Nd4j;
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
		Process processSetGpu = Runtime.getRuntime().exec("SETX BACKEND_PRIORITY_GPU \"20\"");
		processSetGpu.waitFor();
		Process processSetCpu = Runtime.getRuntime().exec("SETX BACKEND_PRIORITY_CPU \"1\"");
		processSetCpu.waitFor();
		restartEndpoint.restart();
	}
	
	// Set "BACKEND_PRIORITY_GPU" to 5 and reset server
	@MessageMapping("/backend/cpu")
	public void switchbackendtocpu() throws Exception {
		System.out.println("[SERVER BACKEND] - SWITCHING TO CPU");
		Process processSetGpu = Runtime.getRuntime().exec("SETX BACKEND_PRIORITY_CPU \"20\"");
		processSetGpu.waitFor();
		Process processSetCpu = Runtime.getRuntime().exec("SETX BACKEND_PRIORITY_GPU \"1\"");
		processSetCpu.waitFor();
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
		// get the enviroment variables
		String cpuprioritystring = System.getenv("BACKEND_PRIORITY_CPU");
		String gpuprioritystring = System.getenv("BACKEND_PRIORITY_GPU");

		// if unspecified try to default to GPU
		if (cpuprioritystring == null || gpuprioritystring == null) {
			return checkIsBackendGpu() ? "gpu" : "cpu";
		}
		int cpupriority = Integer.parseInt(cpuprioritystring);
		int gpupriority = Integer.parseInt(gpuprioritystring);
		if (gpupriority >= cpupriority) {
			// check if backend if gpu
			return checkIsBackendGpu() ? "gpu" : "cpu";
		} else{
			return "cpu";
		}
	}

	/**
	 *
	 * @return Boolean
	 *  true if backend is gpu, and false if backend used is cpu
	 */
	private Boolean checkIsBackendGpu(){
		try {
			// check if backend used is gpu
			if (Nd4j.backend.toString() != "org.nd4j.linalg.cpu.nativecpu.CpuBackend") return true;
		} catch (Exception e){
			System.out.println(e);
			return false;
		}
		return false;
	}
}
