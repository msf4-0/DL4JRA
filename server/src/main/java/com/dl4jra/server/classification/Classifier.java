package com.dl4jra.server.classification;

import java.io.File;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.opencv.core.Mat;

import com.dl4jra.server.LibraryLoader;

public class Classifier {
	static { LibraryLoader.loadOpencvLibrary(); } 
	private MultiLayerNetwork network;
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
		this.network = null;
	}

	/**
	 * Load a classifier from path (local) if exists
	 * @param path - Path to classifier
	 * @return Size of output layer (number of classes)
	 * @throws Exception
	 */
	public int LoadClassifier(String path) throws Exception {
		File location = new File(path);
		boolean modelexists = location.exists() && !location.isDirectory();
		if (!modelexists)
			throw new Exception("CLASSIFIER (MODEL) NOT FOUND");
		this.network = ModelSerializer.restoreMultiLayerNetwork(path, true);
		System.out.println("Network has input size of " + this.network.layerInputSize(0));
		System.out.println("Network has output size of " + this.network.layerSize(this.network.getnLayers() - 1));
		return this.network.layerSize(this.network.getnLayers() - 1);
	}
	
	/**
	 * Classify image/ predict result
	 * @param image - Image to classify (opencv Mat object)
	 * @return Classification result (index) of image
	 * @throws Exception
	 */
	public int Classify(Mat image) throws Exception {
		if (this.network == null)
			throw new Exception("CLASSIFIER (MODEL) NOT FOUND");
		INDArray ds = loader.asMatrix(image);
		return this.network.predict(ds)[0];
	}
	
	/**
	 * Classify image/ predict result
	 * @param ds - Image data to classify (INDArray object)
	 * @return Classification result (index) of image
	 * @throws Exception
	 */
	public int Classify(INDArray ds) throws Exception {
		if (this.network == null)
			throw new Exception("CLASSIFIER (MODEL) IS NOT LOADED");
		return this.network.predict(ds)[0];
	}
	
}
