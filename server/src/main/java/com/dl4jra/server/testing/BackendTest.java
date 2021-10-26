package com.dl4jra.server.testing;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

public class BackendTest {

	/**
	 * 1. Get the value of system environment variables (BACKEND_PRIORITY_CPU/GPU)
	 * 2. Load a MultiLayerNetwork 
	 * 3. Check the backend used by DL4J
	 */
	public static void main(String[] args) {
		// Change the modelpath (eg. "C://Testmodel.zip" or "C://Testmodel.data")
		String modelpath = "";
		try {
			String cpuprioritystring = System.getenv("BACKEND_PRIORITY_CPU");
			String gpuprioritystring = System.getenv("BACKEND_PRIORITY_GPU");
			System.out.println("CPU: " + cpuprioritystring);
			System.out.println("GPU: " + gpuprioritystring);
			MultiLayerNetwork network = ModelSerializer.restoreMultiLayerNetwork(modelpath, true);
			System.out.println(String.format("Output size of loaded model: %d\n", network.layerSize(network.getnLayers() - 1)));
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

	}

}
