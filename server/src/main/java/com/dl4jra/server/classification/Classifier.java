package com.dl4jra.server.classification;

import java.io.File;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.SqueezeNet;
import org.nd4j.common.base.Preconditions;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.exception.ND4JArraySizeException;
import org.opencv.core.Mat;

import com.dl4jra.server.LibraryLoader;

import static java.lang.Math.toIntExact;

public class Classifier {
	static { LibraryLoader.loadOpencvLibrary(); } 
	private MultiLayerNetwork multiLayerNetwork;
	private ComputationGraph computationGraph;
	private static NativeImageLoader loader = new NativeImageLoader();

	/**
	 * Constructor
	 */
	public Classifier() {
		
	}
	
	/**
	 * Reset classifier
	 */
	public void resetclassifier() {
		this.multiLayerNetwork = null;
		this.computationGraph = null;
	}

	/**
	 * Load a classifier from path (local) if exists
	 * @param path - Path to classifier
	 * @return Size of output layer (number of classes)
	 * @throws Exception
	 */
	public int 	LoadClassifier(String path) throws Exception {
		File location = new File(path);

		boolean modelexists = location.exists() && !location.isDirectory();
		if (!modelexists)
			throw new Exception("CLASSIFIER (MODEL) NOT FOUND");
		try {
			this.multiLayerNetwork = ModelSerializer.restoreMultiLayerNetwork(path, true);
			System.out.println("Network has input size of " + this.multiLayerNetwork.layerInputSize(0));
			System.out.println("Network has output size of " + this.multiLayerNetwork.layerSize(this.multiLayerNetwork.getnLayers() - 1));
			return this.multiLayerNetwork.layerSize(this.multiLayerNetwork.getnLayers() - 1);
		} catch	(Exception ignored){
		}
		try{
			this.computationGraph = ModelSerializer.restoreComputationGraph(path, true);
			ComputationGraph testComputationGraph = (ComputationGraph) SqueezeNet.builder().build().initPretrained();
			System.out.println(computationGraph.summary());
			System.out.println("Network has input size of " + this.computationGraph.layerInputSize(0));
			int nOut = 0;
			// Iterate through computation graph layers and find the furthest back layer that has outputs > 0
			// as some graphs dont have their output as the final layer
			for (int layerNum = this.computationGraph.getNumLayers() - 1; layerNum >= 0; layerNum--){
				if (nOut < this.computationGraph.layerSize(layerNum)){
					nOut = toIntExact(this.computationGraph.layerSize(layerNum));
					break;
				}
			}
			System.out.println("Network has output size of " + nOut);
			return nOut;
		} catch (Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			throw new Exception("Cant load model");
		}


	}

	
	/**
	 * Classify image/ predict result
	 * @param image - Image to classify (opencv Mat object)
	 * @return Classification result (index) of image
	 * @throws Exception
	 */
	public int Classify(Mat image) throws Exception {
		INDArray ds = loader.asMatrix(image);
		if (this.multiLayerNetwork != null){
			return this.multiLayerNetwork.predict(ds)[0];
		} else if(this.computationGraph != null){
			return this.computationGraph.outputSingle(ds).argMax(1).toIntVector()[0];
		}
			throw new Exception("CLASSIFIER (MODEL) NOT FOUND");
	}
	
	/**
	 * Classify image/ predict result
	 * @param ds - Image data to classify (INDArray object)
	 * @return Classification result (index) of image
	 * @throws Exception
	 */
	public int Classify(INDArray ds) throws Exception {
		if (this.multiLayerNetwork != null){
			return this.multiLayerNetwork.predict(ds)[0];
		} else if(this.computationGraph != null){
			System.out.println(this.computationGraph.outputSingle(ds).argMax(1).toIntVector()[0]);
			return this.computationGraph.outputSingle(ds).argMax(1).toIntVector()[0];
		}
		throw new Exception("CLASSIFIER (MODEL) NOT FOUND");
	}
	
}
